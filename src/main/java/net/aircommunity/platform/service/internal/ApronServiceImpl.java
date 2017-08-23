package net.aircommunity.platform.service.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import io.micro.common.Strings;
import io.micro.common.io.MoreFiles;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Apron;
import net.aircommunity.platform.model.domain.Apron.Type;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.ApronRepository;
import net.aircommunity.platform.service.ApronService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Apron service implementation.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class ApronServiceImpl extends AbstractServiceSupport implements ApronService {
	private static final Logger LOG = LoggerFactory.getLogger(ApronServiceImpl.class);

	private static final String CACHE_NAME = "cache.apron";
	private static final String APRONS_INFO = "data/aprons.json";
	// earth radius
	private static final double EARTH_RADIUS = 6378.137;
	@Resource
	private ApronRepository apronRepository;


	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
				Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}


	@PostConstruct
	private void init() {
		if (apronRepository.count() > 0) {
			LOG.warn("Apron information is already imported, skip importing apron info to DB");
			return;
		}
		try {
			String json = MoreFiles.toString(APRONS_INFO);
			if (json == null) {
				LOG.warn("No apron information provided, skip importing apron info to DB");
				return;
			}
			List<Apron> aprons = objectMapper.readValue(json, new TypeReference<List<Apron>>() {
			});
			for (Apron apron : aprons) {
				createApron(apron);
			}
		}
		catch (Exception e) {
			LOG.warn("Failed to initialize aprons information, casue: " + e.getMessage(), e);
		}
	}

	@Transactional
	@Override
	public Apron createApron(Apron apron) {
		Apron newApron = new Apron();
		newApron.setPublished(true);
		copyProperties(apron, newApron);
		return safeExecute(() -> apronRepository.save(newApron), "Create apron %s failed", apron);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Apron findApron(String apronId) {
		Apron apron = apronRepository.findOne(apronId);
		if (apron == null) {
			LOG.error("Apron {} not found", apronId);
			throw new AirException(Codes.APRON_NOT_FOUND, M.msg(M.APRON_NOT_FOUND));
		}
		return apron;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#apronId")
	@Override
	public Apron updateApron(String apronId, Apron newApron) {
		Apron apron = findApron(apronId);
		copyProperties(newApron, apron);
		return safeExecute(() -> apronRepository.save(apron), "Update apron %s to %s failed", apronId, apron);
	}

	@Transactional
	@Override
	public Apron publishApron(String apronId, boolean published) {
		Apron apron = findApron(apronId);
		apron.setPublished(published);
		return safeExecute(() -> apronRepository.save(apron), "Update apron %s to published to %d", apronId, published);
	}

	private void copyProperties(Apron src, Apron tgt) {
		tgt.setName(src.getName());
		tgt.setProvince(src.getProvince());
		tgt.setCity(src.getCity());
		tgt.setLocation(src.getLocation());
		tgt.setType(src.getType());
		tgt.setDescription(src.getDescription());
	}

	@Override
	public Page<Apron> listAprons(int page, int pageSize) {
		return Pages.adapt(apronRepository.findAll(createPageRequest(page, pageSize)));
	}

	@Override
	public List<Apron> listApronsByDistinctCities() {
		return apronRepository.listApronsByDistinctCities();
	}

	@Override
	public List<Apron> listPublishedCityAprons(String city, Type type) {
		return apronRepository.findPublishedByFuzzyCity(city, type);
	}

	@Override
	public List<Apron> listPublishedProvinceAprons(String province, Type type) {
		return apronRepository.findPublishedByFuzzyProvince(province, type);
	}


	@Override
	public List<Apron> listNearPublishedAprons(String province, int distance, double latitude, double longitude, Type type) {

		List<Apron> la = null;
		double curLat, curLon;
		if (Strings.isNotBlank(province)) {
			la = apronRepository.findPublishedByFuzzyProvince(province, type);
		}
		else {
			la = apronRepository.findByPublishedTrue();
		}

		Iterator<Apron> iter = la.iterator();
		while (iter.hasNext()) {
			Apron s = iter.next();
			curLat = s.getLocation().getLatitude().doubleValue();
			curLon = s.getLocation().getLongitude().doubleValue();
			//TODO Use baidu map sdk to get accurate distance
			double dist = GetDistance(latitude, longitude, curLat, curLon);
			if (dist > distance) {
				iter.remove();
			}
		}
		return la;
	}

	@Override
	public List<String> listCities() {
		// because city allows null, so we need filter out null and empty cities
		return apronRepository.findDistinctCity().stream().filter(city -> Strings.isNotBlank(city))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> listProvinces() {
		return apronRepository.findDistinctProvince();
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#apronId")
	@Override
	public void deleteApron(String apronId) {
		safeExecute(() -> apronRepository.delete(apronId), "Delete apron %s failed", apronId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAprons() {
		safeExecute(() -> apronRepository.deleteAll(), "Delete all aprons failed");
	}

}
