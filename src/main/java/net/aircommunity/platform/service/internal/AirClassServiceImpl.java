package net.aircommunity.platform.service.internal;

import java.util.Date;

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
import net.aircommunity.platform.model.domain.AirClass;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AirClassRepository;
import net.aircommunity.platform.service.AirClassService;

/**
 * AirClass service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirClassServiceImpl extends AbstractServiceSupport implements AirClassService {
	private static final Logger LOG = LoggerFactory.getLogger(AirClassServiceImpl.class);

	private static final String CACHE_NAME = "cache.airclass";

	@Resource
	private AirClassRepository airClassRepository;

	@Override
	public AirClass createAirClass(AirClass airClass) {
		AirClass newAirClass = new AirClass();
		newAirClass.setCreationDate(new Date());
		copyProperties(airClass, newAirClass);
		return safeExecute(() -> airClassRepository.save(newAirClass), "Create airClass %s failed", airClass);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirClass findAirClass(String airClassId) {
		AirClass airClass = airClassRepository.findOne(airClassId);
		if (airClass == null) {
			LOG.error("Air class {} not found", airClassId);
			throw new AirException(Codes.AIRCLASS_NOT_FOUND, M.msg(M.AIRCLASS_NOT_FOUND));
		}
		return airClass;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airClassId")
	@Override
	public AirClass updateAirClass(String airClassId, AirClass newAirClass) {
		AirClass airClass = findAirClass(airClassId);
		copyProperties(newAirClass, airClass);
		return safeExecute(() -> airClassRepository.save(airClass), "Update airClass %s to %s failed", airClassId,
				airClass);
	}

	private void copyProperties(AirClass src, AirClass tgt) {
		tgt.setTitle(src.getTitle());
		tgt.setContent(src.getContent());
	}

	@Override
	public Page<AirClass> listAirClasses(int page, int pageSize) {
		return Pages.adapt(airClassRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airClassId")
	@Override
	public void deleteAirClass(String airClassId) {
		safeExecute(() -> airClassRepository.delete(airClassId), "Delete airClass %s failed", airClassId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirClasses() {
		safeExecute(() -> airClassRepository.deleteAll(), "Delete all AirClasss failed");
	}

}
