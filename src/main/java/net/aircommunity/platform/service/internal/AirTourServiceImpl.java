package net.aircommunity.platform.service.internal;

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
public class AirTourServiceImpl extends AircraftAwareService<AirTour> implements AirTourService {
	private static final String CACHE_NAME = "cache.airtour";

	@Resource
	private AirTourRepository airTourRepository;

	@Resource
	private AircraftService aircraftService;

	@Override
	public AirTour createAirTour(String tenantId, AirTour airTour) {
		return createProduct(tenantId, airTour);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTour findAirTour(String airTourId) {
		return findProduct(airTourId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTourId")
	@Override
	public AirTour updateAirTour(String airTourId, AirTour newAirTour) {
		return updateProduct(airTourId, newAirTour);
	}

	@Override
	protected void doCopyProperties(AirTour src, AirTour tgt) {
		tgt.setCity(src.getCity());
		tgt.setTourDistance(src.getTourDistance());
		tgt.setBoardingLoc(src.getBoardingLoc());
		tgt.setTourPoint(src.getTourPoint());
		tgt.setTourShow(src.getTourShow());
		tgt.setTourTime(src.getTourTime());
		tgt.setTraffic(src.getTraffic());
		tgt.setTourRoute(src.getTourRoute());
	}

	@Override
	public Page<AirTour> listAirTours(int page, int pageSize) {
		return listAllProducts(page, pageSize);
	}

	@Override
	public Page<AirTour> listAirTours(String tenantId, int page, int pageSize) {
		return listTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<AirTour> listAirToursByCity(String city, int page, int pageSize) {
		return Pages.adapt(airTourRepository.findByCity(city, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTourId")
	@Override
	public void deleteAirTour(String airTourId) {
		deleteProduct(airTourId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirTours(String tenantId) {
		deleteProducts(tenantId);
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
