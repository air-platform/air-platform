package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.AircraftItem;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.AirTourRepository;
import net.aircommunity.platform.service.AirTourService;
import net.aircommunity.platform.service.AircraftService;

/**
 * Created by guankai on 14/04/2017.
 */
@Service
@Transactional
public class AirTourServiceImpl extends AbstractServiceSupport implements AirTourService {
	private static final String CACHE_NAME = "cache.airtour";

	@Resource
	private AirTourRepository airTourRepository;

	@Resource
	private AircraftService aircraftService;

	@Override
	public AirTour createAirTour(String tenantId, AirTour airTour) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		AirTour newAirTour = new AirTour();
		copyProperties(airTour, newAirTour);
		airTour.setVendor(tenant);
		return airTourRepository.save(airTour);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTour findAirTour(String airTourId) {
		AirTour airTour = airTourRepository.findOne(airTourId);
		if (airTour == null) {
			throw new AirException(Codes.TOUR_NOT_FOUND, String.format("Air tour %s not found", airTourId));
		}
		return airTour;
	}

	@Cacheable(cacheNames = CACHE_NAME, key = "#airTourId")
	@Override
	public AirTour updateAirTour(String airTourId, AirTour newAirTour) {
		AirTour airTour = findAirTour(airTourId);
		copyProperties(newAirTour, airTour);
		return airTourRepository.save(airTour);
	}

	private void copyProperties(AirTour src, AirTour tgt) {
		tgt.setCity(src.getCity());
		tgt.setTourDistance(src.getTourDistance());
		tgt.setBoardingLoc(src.getBoardingLoc());
		tgt.setTourPoint(src.getTourPoint());
		tgt.setTourShow(src.getTourShow());
		tgt.setTourTime(src.getTourTime());
		tgt.setTraffic(src.getTraffic());
		tgt.setName(src.getName());
		tgt.setDescription(src.getDescription());
		Set<AircraftItem> aircraftItems = src.getAircraftItems();
		if (aircraftItems != null) {
			aircraftItems.stream().forEach(aircraftItem -> {
				Aircraft aircraft = aircraftItem.getAircraft();
				if (aircraft != null) {
					aircraft = aircraftService.findAircraft(aircraft.getId());
					aircraftItem.setAircraft(aircraft);
				}
			});
			tgt.setAircraftItems(aircraftItems);
		}
	}

	@Override
	public Page<AirTour> listAirTours(int page, int pageSize) {
		return Pages.adapt(airTourRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTour> listAirTours(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(airTourRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTour> listAirToursByCity(String city, int page, int pageSize) {
		return Pages.adapt(airTourRepository.findByCity(city, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public void deleteAirTour(String airTourId) {
		airTourRepository.delete(airTourId);
	}

	@Override
	public void deleteAirTours(String tenantId) {
		airTourRepository.deleteByVendorId(tenantId);
	}

}
