package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.AircraftItem;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.AirTaxiRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.platform.service.AircraftService;

/**
 * Created by guankai on 15/04/2017.
 */
@Service
@Transactional
public class AirTaxiServiceImpl extends AbstractProductService<AirTaxi> implements AirTaxiService {
	private static final String CACHE_NAME = "cache.airtaxi";

	@Resource
	private AirTaxiRepository airTaxiRepository;

	@Resource
	private AircraftService aircraftService;

	@Override
	public AirTaxi createAirTaxi(String tenantId, AirTaxi airTaxi) {
		return createProduct(tenantId, airTaxi);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTaxi findAirTaxi(String taxiId) {
		return findProduct(taxiId);
	}

	@Cacheable(cacheNames = CACHE_NAME, key = "#taxiId")
	@Override
	public AirTaxi updateAirTaxi(String taxiId, AirTaxi newAirTaxi) {
		return updateProduct(taxiId, newAirTaxi);
	}

	@Override
	protected void copyProperties(AirTaxi src, AirTaxi tgt) {
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
		return listAirTaxis(page, pageSize);
	}

	@Nonnull
	@Override
	public Page<AirTaxi> listAirTaxis(String tenantId, int page, int pageSize) {
		return listAirTaxis(tenantId, page, pageSize);
	}

	@Nonnull
	@Override
	public Page<AirTaxi> listAirTaxisByDeparture(String departure, int page, int pageSize) {
		return Pages.adapt(airTaxiRepository.findByDeparture(departure, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTaxiId")
	@Override
	public void deleteAirTaxi(String airTaxiId) {
		deleteProduct(airTaxiId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirTaxis(String tenantId) {
		deleteProducts(tenantId);
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
