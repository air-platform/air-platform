package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JetTravel;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.JetTravelRepository;
import net.aircommunity.platform.service.JetTravelService;

/**
 * JetTravel service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class JetTravelServiceImpl extends AbstractProductService<JetTravel> implements JetTravelService {
	private static final String CACHE_NAME = "cache.jettravel";

	@Resource
	private JetTravelRepository jetTravelRepository;

	@Override
	public JetTravel createJetTravel(String tenantId, JetTravel jetTravel) {
		jetTravel.setCategory(Category.AIR_JET);
		return doCreateProduct(tenantId, jetTravel);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public JetTravel findJetTravel(String jetTravelId) {
		return doFindProduct(jetTravelId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#jetTravelId")
	@Override
	public JetTravel updateJetTravel(String jetTravelId, JetTravel newJetTravel) {
		return doUpdateProduct(jetTravelId, newJetTravel);
	}

	@Override
	protected void copyProperties(JetTravel src, JetTravel tgt) {
		// do copy if any (not prop for now)
	}

	@Override
	public Page<JetTravel> listAllJetTravels(ReviewStatus reviewStatus, int page, int pageSize) {
		return doListAllProducts(reviewStatus, page, pageSize);
	}

	@Override
	public long countAllJetTravels(ReviewStatus reviewStatus) {
		return doCountAllProducts(reviewStatus);
	}

	@Override
	public Page<JetTravel> listTenantJetTravels(String tenantId, ReviewStatus reviewStatus, int page, int pageSize) {
		return doListTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	@Override
	public long countTenantJetTravels(String tenantId, ReviewStatus reviewStatus) {
		return doCountTenantProducts(tenantId, reviewStatus);
	}

	@Override
	public Page<JetTravel> listJetTravels(int page, int pageSize) {
		return doListProductsForUsers(page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#jetTravelId")
	@Override
	public void deleteJetTravel(String jetTravelId) {
		doDeleteProduct(jetTravelId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteJetTravels(String tenantId) {
		doDeleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.JETTRAVEL_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<JetTravel> getProductRepository() {
		return jetTravelRepository;
	}

}
