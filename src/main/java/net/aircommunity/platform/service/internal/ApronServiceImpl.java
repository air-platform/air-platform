package net.aircommunity.platform.service.internal;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Apron;
import net.aircommunity.platform.model.domain.Apron.Type;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.ApronRepository;
import net.aircommunity.platform.service.ApronService;

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

	@Resource
	private ApronRepository apronRepository;

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
	public List<Apron> listPublishedCityAprons(String city, Type type) {
		return apronRepository.findPublishedByCity(city, type);
	}

	@Override
	public List<Apron> listPublishedProvinceAprons(String province, Type type) {
		return apronRepository.findPublishedByProvince(province, type);
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
