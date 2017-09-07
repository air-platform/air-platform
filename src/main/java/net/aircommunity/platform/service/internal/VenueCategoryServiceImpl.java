package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.VenueCategory;
import net.aircommunity.platform.model.domain.VenueCategoryProduct;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.VenueCategoryRepository;
import net.aircommunity.platform.service.VenueCategoryService;
import net.aircommunity.platform.service.product.AirTourService;
import net.aircommunity.platform.service.product.CommonProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * VenueCategory service implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class VenueCategoryServiceImpl extends AbstractServiceSupport implements VenueCategoryService {
	private static final Logger LOG = LoggerFactory.getLogger(VenueCategoryServiceImpl.class);

	private static final String CACHE_NAME = "cache.venuecategory";
	@Resource
	private VenueCategoryRepository venueCategoryRepository;

	@Resource
	private AirTourService airTourService;

	@Resource
	private CommonProductService commonProductService;

	@Transactional
	@Override
	public VenueCategory createVenueCategory(VenueCategory venueCategory) {

		for (VenueCategoryProduct cp : venueCategory.getVenueCategoryProducts()) {
			Product p = commonProductService.findProduct(cp.getProductId());
			cp.setProduct(p);
		}

		LOG.info("type:{}..", venueCategory.getVenueCategoryProducts().size());


		VenueCategory newVenueCategory = new VenueCategory();
		/*Set<VenueCategoryProduct> cpSet = new HashSet<VenueCategoryProduct>();

		AirTour at = airTourService.findAirTour("dcfa401a-5e32-1efd-815e-35e74a420001");
		VenueCategoryProduct cp1 = new VenueCategoryProduct();
		cp1.setVenueCategory(venueCategory);
		cp1.setProduct(at);

		cpSet.add(cp1);*/

		//venueCategory.setVenueCategoryProducts(cpSet);

		copyProperties(venueCategory, newVenueCategory);
		return safeExecute(() -> venueCategoryRepository.save(newVenueCategory), "Create VenueCategory %s failed", venueCategory);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public VenueCategory findVenueCategory(String venueCategoryId) {
		VenueCategory venueCategory = venueCategoryRepository.findOne(venueCategoryId);
		if (venueCategory == null) {
			LOG.error("VenueCategory {} not found", venueCategoryId);
			throw new AirException(Codes.VENUE_CATEGORY_NOT_FOUND, M.msg(M.VENUE_CATEGORY_NOT_FOUND));
		}
		return venueCategory;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#venueCategoryId")
	@Override
	public VenueCategory updateVenueCategory(String venueCategoryId, VenueCategory newVenueCategory) {
		VenueCategory venueCategory = findVenueCategory(venueCategoryId);

		for (VenueCategoryProduct cp : newVenueCategory.getVenueCategoryProducts()) {
			Product p = commonProductService.findProduct(cp.getProductId());
			cp.setProduct(p);
		}

		copyProperties(newVenueCategory, venueCategory);
		return safeExecute(() -> venueCategoryRepository.save(venueCategory), "Update venueCategory %s to %s failed", venueCategoryId, venueCategory);
	}


	private void copyProperties(VenueCategory src, VenueCategory tgt) {
		tgt.setName(src.getName());
		tgt.setPicture(src.getPicture());
		tgt.setDescription(src.getDescription());
		tgt.setVenueInfo(src.getVenueInfo());
		tgt.setVenueCategoryProducts(src.getVenueCategoryProducts());
	}

	@Override
	public Page<VenueCategory> listVenueCategorys(int page, int pageSize) {
		return Pages.adapt(venueCategoryRepository.findAll(createPageRequest(page, pageSize)));
	}


	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#venueCategoryId")
	@Override
	public void deleteVenueCategory(String venueCategoryId) {
		safeDeletion(venueCategoryRepository, venueCategoryId, Codes.VENUE_CATEGORY_CANNOT_BE_DELETED, M.msg(M.VENUE_CATEGORY_CANNOT_BE_DELETED));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteVenueCategorys() {
		safeExecute(() -> venueCategoryRepository.deleteAll(), "Delete all citysites failed");
	}

}
