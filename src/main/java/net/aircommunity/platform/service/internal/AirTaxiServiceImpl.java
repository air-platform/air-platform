package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
import net.aircommunity.platform.repository.AirTaxiRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.platform.service.AircraftService;

/**
 * AirTaxiService implementation
 * 
 * @author guankai
 */
@Service
@Transactional
public class AirTaxiServiceImpl extends AircraftAwareProductService<AirTaxi> implements AirTaxiService {
	private static final String CACHE_NAME = "cache.airtaxi";

	@Resource
	private AirTaxiRepository airTaxiRepository;

	@Resource
	private AircraftService aircraftService;

	@Override
	public AirTaxi createAirTaxi(String tenantId, AirTaxi airTaxi) {
		airTaxi.setCategory(Category.AIR_TAXI);
		return doCreateProduct(tenantId, airTaxi);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTaxi findAirTaxi(String taxiId) {
		return doFindProduct(taxiId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#taxiId")
	@Override
	public AirTaxi updateAirTaxi(String taxiId, AirTaxi newAirTaxi) {
		return doUpdateProduct(taxiId, newAirTaxi);
	}

	@Override
	protected void doCopyProperties(AirTaxi src, AirTaxi tgt) {
		tgt.setFlightRoute(src.getFlightRoute());
		tgt.setDistance(src.getDistance());
		tgt.setDuration(src.getDuration());
	}

	@Override
	public Page<AirTaxi> listAllAirTaxis(ReviewStatus reviewStatus, int page, int pageSize) {
		return doListAllProducts(reviewStatus, page, pageSize);
	}

	@Override
	public long countAllAirTaxis(ReviewStatus reviewStatus) {
		return doCountAllProducts(reviewStatus);
	}

	@Override
	public Page<AirTaxi> listTenantAirTaxis(String tenantId, ReviewStatus reviewStatus, int page, int pageSize) {
		return doListTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	@Override
	public long countTenantAirTaxis(String tenantId, ReviewStatus reviewStatus) {
		return doCountTenantProducts(tenantId, reviewStatus);
	}

	@Override
	public Page<AirTaxi> listAirTaxis(int page, int pageSize) {
		return doListProductsForUsers(page, pageSize);
	}

	@Override
	public Page<AirTaxi> listAirTaxisByDeparture(String departure, int page, int pageSize) {
		return Pages.adapt(
				airTaxiRepository.listAirTaxisByDepartureForUser(departure, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTaxi> listAirTaxisByArrival(String arrival, int page, int pageSize) {
		return Pages.adapt(
				airTaxiRepository.listAirTaxisByArrivalForUser(arrival, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTaxi> searchAirTaxisByLocation(String location, int page, int pageSize) {
		return Pages
				.adapt(airTaxiRepository.searchByLocationForUser(location, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTaxiId")
	@Override
	public void deleteAirTaxi(String airTaxiId) {
		doDeleteProduct(airTaxiId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirTaxis(String tenantId) {
		doDeleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.TAXI_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<AirTaxi> getProductRepository() {
		return airTaxiRepository;
	}
}
