package net.aircommunity.platform.service.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import io.micro.common.Strings;
import io.micro.common.io.MoreFiles;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CitySite;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.CitySiteRepository;
import net.aircommunity.platform.service.CitySiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CitySite service implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class CitySiteServiceImpl extends AbstractServiceSupport implements CitySiteService {
    private static final Logger LOG = LoggerFactory.getLogger(CitySiteServiceImpl.class);

    private static final String CACHE_NAME = "cache.citysite";
    private static final String CITYSITES_INFO = "data/citysites.json";
    @Resource
    private CitySiteRepository citySiteRepository;


    @PostConstruct
    private void init() {
        if (citySiteRepository.count() > 0) {
            LOG.warn("CitySite information is already imported, skip importing apron info to DB");
            return;
        }
        try {
            String json = MoreFiles.toString(CITYSITES_INFO);
            if (json == null) {
                LOG.warn("No CitySite information provided, skip importing apron info to DB");
                return;
            }
            List<CitySite> citySites = objectMapper.readValue(json, new TypeReference<List<CitySite>>() {
            });
            for (CitySite citySite : citySites) {
                createCitySite(citySite);
            }


			/*for (CitySite citySite : citySites) {
                // XXX NOT USE LOCAL FILE
				// jet.setImage(String.format(BASE_URL_FORMAT, configuration.getPublicHost(),
				// configuration.getPublicPort(), jet.getImage()));
				CitySite found = citySiteRepository.findByNameIgnoreCase(citySite.getName());
				if (found == null) {
					createCitySite(citySite);
				}
				else {
					updateCitySite(found.getId(), citySite);
				}
			}*/


        } catch (Exception e) {
            LOG.warn("Failed to initialize aprons information, casue: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public CitySite createCitySite(CitySite citySite) {
        CitySite newCitySite = new CitySite();
        copyProperties(citySite, newCitySite);
        return safeExecute(() -> citySiteRepository.save(newCitySite), "Create CitySite %s failed", citySite);
    }

    @Cacheable(cacheNames = CACHE_NAME)
    @Override
    public CitySite findCitySite(String citySiteId) {
        CitySite citySite = citySiteRepository.findOne(citySiteId);
        if (citySite == null) {
            LOG.error("CitySite {} not found", citySiteId);
            throw new AirException(Codes.CITYSITE_NOT_FOUND, M.msg(M.CITYSITE_NOT_FOUND));
        }
        return citySite;
    }

    @Transactional
    @CachePut(cacheNames = CACHE_NAME, key = "#citySiteId")
    @Override
    public CitySite updateCitySite(String citySiteId, CitySite newCitySite) {
        CitySite citySite = findCitySite(citySiteId);
        copyProperties(newCitySite, citySite);
        return safeExecute(() -> citySiteRepository.save(citySite), "Update citySite %s to %s failed", citySiteId, citySite);
    }


    private void copyProperties(CitySite src, CitySite tgt) {
        tgt.setName(src.getName());
        tgt.setCity(src.getCity());
        tgt.setLocation(src.getLocation());
        tgt.setAddress(src.getAddress());
        tgt.setDescription(src.getDescription());
    }

    @Override
    public Page<CitySite> listCitySites(int page, int pageSize) {
        return Pages.adapt(citySiteRepository.findAll(createPageRequest(page, pageSize)));
    }

    @Override
    public List<CitySite> listCitySitesByCity(String city) {
        return citySiteRepository.listCitySitesByCity(city);
    }


    @Override
    public List<String> listCities() {
        // because city allows null, so we need filter out null and empty cities
        return citySiteRepository.findDistinctCity().stream().filter(city -> Strings.isNotBlank(city))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, key = "#citySiteId")
    @Override
    public void deleteCitySite(String citySiteId) {
        safeExecute(() -> citySiteRepository.delete(citySiteId), "Delete citysite %s failed", citySiteId);
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    @Override
    public void deleteCitySites() {
        safeExecute(() -> citySiteRepository.deleteAll(), "Delete all citysites failed");
    }

}
