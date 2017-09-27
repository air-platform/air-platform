package net.aircommunity.platform.service.internal;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.Resource;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.VenueCategory;
import net.aircommunity.platform.model.domain.VenueInfo;
import net.aircommunity.platform.model.domain.VenueTemplate;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.VenueInfoRepository;
import net.aircommunity.platform.service.VenueInfoService;
import net.aircommunity.platform.service.VenueTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * VenueInfo service implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class VenueInfoServiceImpl extends AbstractServiceSupport implements VenueInfoService {
	private static final Logger LOG = LoggerFactory.getLogger(VenueInfoServiceImpl.class);

	private static final String CACHE_NAME = "cache.venueinfo";
	@Resource
	private VenueInfoRepository venueInfoRepository;

	@Resource
	private VenueTemplateService venueTemplateService;

	@Transactional
	@Override
	public VenueInfo createVenueInfo(VenueInfo venueInfo) {

		//VenueTemplate temp = venueTemplateService.findVenueTemplate(venueInfo.getVenueTemplate().getId());
		//venueInfo.setVenueTemplate(temp);
		VenueInfo newVenueInfo = new VenueInfo();
		copyProperties(venueInfo, newVenueInfo);
		return safeExecute(() -> venueInfoRepository.save(newVenueInfo), "Create VenueInfo %s failed", venueInfo);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public VenueInfo findVenueInfo(String venueInfoId) {
		VenueInfo venueInfo = venueInfoRepository.findOne(venueInfoId);
		if (venueInfo == null) {
			LOG.error("VenueInfo {} not found", venueInfoId);
			throw new AirException(Codes.VENUE_INFO_NOT_FOUND, M.msg(M.VENUE_INFO_NOT_FOUND));
		}

		List<VenueCategory> depdupe = new ArrayList<>(new LinkedHashSet<>(venueInfo.getVenueCategories()));
		venueInfo.setVenueCategories(depdupe);
		return venueInfo;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#venueInfoId")
	@Override
	public VenueInfo updateVenueInfo(String venueInfoId, VenueInfo newVenueInfo) {
		VenueInfo venueInfo = findVenueInfo(venueInfoId);
		copyProperties(newVenueInfo, venueInfo);
		return safeExecute(() -> venueInfoRepository.save(venueInfo), "Update venueInfo %s to %s failed", venueInfoId, venueInfo);
	}


	private void copyProperties(VenueInfo src, VenueInfo tgt) {

		tgt.setVenueTemplate(src.getVenueTemplate());
		tgt.setName(src.getName());
		tgt.setPicture(src.getPicture());
		tgt.setDescription(src.getDescription());
		tgt.setBackgroundColor(src.getBackgroundColor());
		tgt.setVenueCategories(src.getVenueCategories());
	}

	@Override
	public Page<VenueInfo> listVenueInfos(int page, int pageSize) {
		return Pages.adapt(venueInfoRepository.findAll(createPageRequest(page, pageSize)));
	}


	@Override
	public List<VenueInfo> listVenueInfosByVenueTemplate(String venueTemplateId) {
		VenueTemplate vt = venueTemplateService.findVenueTemplate(venueTemplateId);
		return venueInfoRepository.findByVenueTemplate(vt);
	}


	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#venueInfoId")
	@Override
	public void deleteVenueInfo(String venueInfoId) {
		//safeExecute(() -> venueInfoRepository.delete(venueInfoId), "Delete venueInfo %s failed", venueInfoId);

		safeDeletion(venueInfoRepository, venueInfoId, Codes.VENUE_INFO_CANNOT_BE_DELETED, M.msg(M.VENUE_INFO_CANNOT_BE_DELETED));

	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteVenueInfos() {
		safeExecute(() -> venueInfoRepository.deleteAll(), "Delete all citysites failed");
	}

}
