package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.AirTourRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.AirTourService;
import net.aircommunity.platform.service.AircraftService;

/**
 * Created by guankai on 14/04/2017.
 */
@Service
@Transactional
public class AirTourServiceImpl extends AircraftAwareProductService<AirTour> implements AirTourService {
	private static final String CACHE_NAME = "cache.airtour";

	@Resource
	private AirTourRepository airTourRepository;

	@Resource
	private AircraftService aircraftService;

	@Override
	public AirTour createAirTour(String tenantId, AirTour airTour) {
		return doCreateProduct(tenantId, airTour);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTour findAirTour(String airTourId) {
		return doFindProduct(airTourId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTourId")
	@Override
	public AirTour updateAirTour(String airTourId, AirTour newAirTour) {
		return doUpdateProduct(airTourId, newAirTour);
	}

	@Override
	protected void doCopyProperties(AirTour src, AirTour tgt) {
		tgt.setCity(src.getCity());
		tgt.setTourDistance(src.getTourDistance());
		tgt.setBoardingLocation(src.getBoardingLocation());
		tgt.setTourPoint(src.getTourPoint());
		tgt.setTourShow(src.getTourShow());
		tgt.setTourTime(src.getTourTime());
		tgt.setTraffic(src.getTraffic());
		tgt.setTourRoute(src.getTourRoute());
	}

	@Override
	public Set<String> listAirTourCities() {
		return airTourRepository.listCities();
	}

	@Override
	public Page<AirTour> listAirTours(int page, int pageSize) {
		return doListAllProducts(page, pageSize);
	}

	@Override
	public Page<AirTour> listAirTours(String tenantId, int page, int pageSize) {
		return doListTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<AirTour> listAirToursByCity(String city, int page, int pageSize) {
		return Pages.adapt(airTourRepository.findByCity(city, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTourId")
	@Override
	public void deleteAirTour(String airTourId) {
		doDeleteProduct(airTourId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirTours(String tenantId) {
		doDeleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.TOUR_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<AirTour> getProductRepository() {
		return airTourRepository;
	}

}
