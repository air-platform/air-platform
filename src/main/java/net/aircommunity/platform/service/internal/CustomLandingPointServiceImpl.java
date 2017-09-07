package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CustomLandingPoint;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.CustomLandingPointRepository;
import net.aircommunity.platform.service.CustomLandingPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * CustomLandingPoint service implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class CustomLandingPointServiceImpl extends AbstractServiceSupport implements CustomLandingPointService {
	private static final Logger LOG = LoggerFactory.getLogger(CustomLandingPointServiceImpl.class);

	private static final String CACHE_NAME = "cache.customlandingpoint";
	private static final String CITYSITES_INFO = "data/citysites.json";
	@Resource
	private CustomLandingPointRepository customLandingPointRepository;


	@Transactional
	@Override
	public CustomLandingPoint createCustomLandingPoint(CustomLandingPoint customLandingPoint) {
		CustomLandingPoint newCustomLandingPoint = new CustomLandingPoint();
		copyProperties(customLandingPoint, newCustomLandingPoint);
		return safeExecute(() -> customLandingPointRepository.save(newCustomLandingPoint), "Create CustomLandingPoint %s failed", customLandingPoint);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public CustomLandingPoint findCustomLandingPoint(String customLandingPointId) {
		CustomLandingPoint customLandingPoint = customLandingPointRepository.findOne(customLandingPointId);
		if (customLandingPoint == null) {
			LOG.error("CustomLandingPoint {} not found", customLandingPointId);
			throw new AirException(Codes.CITYSITE_NOT_FOUND, M.msg(M.CITYSITE_NOT_FOUND));
		}
		return customLandingPoint;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#customLandingPointId")
	@Override
	public CustomLandingPoint updateCustomLandingPoint(String customLandingPointId, CustomLandingPoint newCustomLandingPoint) {
		CustomLandingPoint customLandingPoint = findCustomLandingPoint(customLandingPointId);
		copyProperties(newCustomLandingPoint, customLandingPoint);
		return safeExecute(() -> customLandingPointRepository.save(customLandingPoint), "Update customLandingPoint %s to %s failed", customLandingPointId, customLandingPoint);
	}


	private void copyProperties(CustomLandingPoint src, CustomLandingPoint tgt) {
		tgt.setName(src.getName());
		tgt.setLocation(src.getLocation());
		tgt.setAddress(src.getAddress());
		tgt.setDescription(src.getDescription());
		tgt.setImages(src.getImages());
	}

	@Override
	public Page<CustomLandingPoint> listCustomLandingPoints(int page, int pageSize) {
		return Pages.adapt(customLandingPointRepository.findAll(createPageRequest(page, pageSize)));
	}


	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#customLandingPointId")
	@Override
	public void deleteCustomLandingPoint(String customLandingPointId) {
		safeExecute(() -> customLandingPointRepository.delete(customLandingPointId), "Delete customLandingPoint %s failed", customLandingPointId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteCustomLandingPoints() {
		safeExecute(() -> customLandingPointRepository.deleteAll(), "Delete all citysites failed");
	}

}
