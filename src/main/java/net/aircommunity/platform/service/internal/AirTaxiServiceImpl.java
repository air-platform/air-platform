package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.repository.AirTaxiRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.platform.service.AircraftService;

/**
 * AirTaxiService implementation
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTaxiServiceImpl extends SalesPackageProductService<AirTaxi> implements AirTaxiService {

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.airtaxi";

	@Resource
	private AirTaxiRepository airTaxiRepository;

	@Resource
	private AircraftService aircraftService;

	@Override
	public AirTaxi createAirTaxi(String tenantId, AirTaxi airTaxi) {
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
	public Set<String> listArrivalsFromDeparture(String departure) {
		return airTaxiRepository.findArrivalsFromDeparture(departure);
	}

	@Override
	public Set<String> listDeparturesToArrival(String arrival) {
		return airTaxiRepository.findDeparturesToArrival(arrival);
	}

	@Override
	public Page<AirTaxi> listAirTaxisWithConditions(String departure, String arrival, String tenantId, int page,
			int pageSize) {
		return Pages.adapt(airTaxiRepository.findWithConditions(departure, arrival, tenantId,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTaxi> listAirTaxisByFuzzyLocation(String location, int page, int pageSize) {
		return Pages.adapt(airTaxiRepository.findFuzzyByLocation(location, Pages.createPageRequest(page, pageSize)));
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
