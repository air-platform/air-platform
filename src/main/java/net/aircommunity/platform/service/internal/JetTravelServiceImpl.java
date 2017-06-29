package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Strings;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
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

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.jettravel";

	@Resource
	private JetTravelRepository jetTravelRepository;

	@Override
	public JetTravel createJetTravel(String tenantId, JetTravel jetTravel) {
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

	@Override
	public Page<JetTravel> listJetTravelsByFuzzyName(String name, int page, int pageSize) {
		if (Strings.isBlank(name)) {
			return listJetTravels(page, pageSize);
		}
		// NOTE: the first 1 has better performance, but less match result
		// return
		// Pages.adapt(jetTravelRepository.findByPublishedTrueAndNameStartingWithIgnoreCaseOrderByRankDescScoreDesc(
		// name, Pages.createPageRequest(page, pageSize)));
		return Pages.adapt(jetTravelRepository.findByPublishedTrueAndNameContainingIgnoreCaseOrderByRankDescScoreDesc(
				name, Pages.createPageRequest(page, pageSize)));
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
