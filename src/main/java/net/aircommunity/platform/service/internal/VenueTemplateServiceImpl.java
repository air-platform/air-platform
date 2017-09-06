package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.VenueTemplate;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.VenueTemplateRepository;
import net.aircommunity.platform.service.VenueTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * VenueTemplate service implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class VenueTemplateServiceImpl extends AbstractServiceSupport implements VenueTemplateService {
	private static final Logger LOG = LoggerFactory.getLogger(VenueTemplateServiceImpl.class);

	private static final String CACHE_NAME = "cache.venuetemplate";
	@Resource
	private VenueTemplateRepository venueTemplateRepository;


	@Transactional
	@Override
	public VenueTemplate createVenueTemplate(VenueTemplate venueTemplate) {
		VenueTemplate newVenueTemplate = new VenueTemplate();
		copyProperties(venueTemplate, newVenueTemplate);
		return safeExecute(() -> venueTemplateRepository.save(newVenueTemplate), "Create VenueTemplate %s failed", venueTemplate);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public VenueTemplate findVenueTemplate(String venueTemplateId) {
		VenueTemplate venueTemplate = venueTemplateRepository.findOne(venueTemplateId);
		if (venueTemplate == null) {
			LOG.error("VenueTemplate {} not found", venueTemplateId);
			throw new AirException(Codes.VENUE_TEMPLATE_NOT_FOUND, M.msg(M.VENUE_TEMPLATE_NOT_FOUND));
		}
		return venueTemplate;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#venueTemplateId")
	@Override
	public VenueTemplate updateVenueTemplate(String venueTemplateId, VenueTemplate newVenueTemplate) {
		VenueTemplate venueTemplate = findVenueTemplate(venueTemplateId);
		copyProperties(newVenueTemplate, venueTemplate);
		return safeExecute(() -> venueTemplateRepository.save(venueTemplate), "Update venueTemplate %s to %s failed", venueTemplateId, venueTemplate);
	}


	private void copyProperties(VenueTemplate src, VenueTemplate tgt) {

		tgt.setName(src.getName());
		tgt.setBackgroundColor(src.getBackgroundColor());
		tgt.setBackgroundPic(src.getBackgroundPic());
		tgt.setDescription(src.getDescription());
	}

	@Override
	public Page<VenueTemplate> listVenueTemplates(int page, int pageSize) {
		return Pages.adapt(venueTemplateRepository.findAll(createPageRequest(page, pageSize)));
	}


	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#venueTemplateId")
	@Override
	public void deleteVenueTemplate(String venueTemplateId) {
		safeExecute(() -> venueTemplateRepository.delete(venueTemplateId), "Delete citysite %s failed", venueTemplateId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteVenueTemplates() {
		safeExecute(() -> venueTemplateRepository.deleteAll(), "Delete all citysites failed");
	}

}
