package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.AircraftItem;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.AirTaxiRepository;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.platform.service.AircraftService;

/**
 * Created by guankai on 15/04/2017.
 */
@Service
@Transactional
public class AirTaxiServiceImpl extends AbstractServiceSupport implements AirTaxiService {
	private static final String CACHE_NAME = "cache.airtaxi";

	@Resource
	private AirTaxiRepository airTaxiRepository;

	@Resource
	private AircraftService aircraftService;

	@Override
	public AirTaxi createAirTaxi(String tenantId, @Nonnull AirTaxi airTaxi) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		AirTaxi newAirTaxi = new AirTaxi();
		copyProperties(airTaxi, newAirTaxi);
		airTaxi.setVendor(tenant);
		return airTaxiRepository.save(airTaxi);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTaxi findAirTaxi(String taxiId) {
		AirTaxi airTaxi = airTaxiRepository.findOne(taxiId);
		if (airTaxi == null) {
			throw new AirException(Codes.TAXI_NOT_FOUND, String.format("air taxi %s not found...", taxiId));
		}
		return airTaxi;
	}

	@Cacheable(cacheNames = CACHE_NAME, key = "#taxiId")
	@Override
	public AirTaxi updateAirTaxi(String taxiId, AirTaxi newAirTaxi) {
		AirTaxi airTaxi = findAirTaxi(taxiId);
		copyProperties(newAirTaxi, airTaxi);
		return airTaxiRepository.save(airTaxi);
	}

	private void copyProperties(AirTaxi src, AirTaxi tgt) {
		tgt.setName(src.getName());
		tgt.setDescription(src.getDescription());
		tgt.setArrival(src.getArrival());
		tgt.setArrivalLoc(src.getArrivalLoc());
		tgt.setDeparture(src.getDeparture());
		tgt.setDepartLoc(src.getDepartLoc());
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
	public Page<AirTaxi> listAirTaxis(int page, int pageSize) {
		return Pages.adapt(airTaxiRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Nonnull
	@Override
	public Page<AirTaxi> listAirTaxis(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(airTaxiRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Nonnull
	@Override
	public Page<AirTaxi> listAirTaxisByDeparture(String departure, int page, int pageSize) {
		return Pages.adapt(airTaxiRepository.findByDeparture(departure, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public void deleteAirTaxi(String airTaxiId) {
		airTaxiRepository.delete(airTaxiId);
	}

	@Override
	public void deleteAirTaxis(String tenantId) {
		airTaxiRepository.deleteByVendorId(tenantId);
	}

}
