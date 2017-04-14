package net.aircommunity.platform.service.internal;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.AircraftItem;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.AirTransportRepository;
import net.aircommunity.platform.service.AirTransportService;
import net.aircommunity.platform.service.AircraftService;

/**
 * AirTransport service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTransportServiceImpl extends AbstractServiceSupport implements AirTransportService {
	private static final String CACHE_NAME = "cache.airtransport";

	@Resource
	private AircraftService aircraftService;

	@Resource
	private AirTransportRepository airTransportRepository;

	@Override
	public AirTransport createAirTransport(String tenantId, AirTransport airTransport) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		// create new
		AirTransport newAirTransport = new AirTransport();
		copyProperties(airTransport, newAirTransport);
		// set vendor
		newAirTransport.setVendor(tenant);
		return airTransportRepository.save(newAirTransport);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTransport findAirTransport(String airTransportId) {
		AirTransport airTransport = airTransportRepository.findOne(airTransportId);
		if (airTransport == null) {
			throw new AirException(Codes.AIRTRANSPORT_NOT_FOUND,
					String.format("AirTransport %s is not found", airTransportId));
		}
		return airTransport;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTransportId")
	@Override
	public AirTransport updateAirTransport(String airTransportId, AirTransport newAirTransport) {
		AirTransport AirTransport = findAirTransport(airTransportId);
		copyProperties(newAirTransport, AirTransport);
		return airTransportRepository.save(AirTransport);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	private void copyProperties(AirTransport src, AirTransport tgt) {
		tgt.setName(src.getName());
		tgt.setDescription(src.getDescription());
		tgt.setFamily(src.getFamily());
		tgt.setTimeEstimation(src.getTimeEstimation());
		tgt.setFlightRoute(src.getFlightRoute());
		Set<AircraftItem> aircraftItems = src.getAircraftItems();
		if (aircraftItems != null) {
			aircraftItems.stream().forEach(aircraftItem -> {
				Aircraft aircraft = aircraftItem.getAircraft();
				if (aircraft != null) {
					aircraft = aircraftService.findAircraft(aircraft.getId());
					aircraftItem.setAircraft(aircraft);
				}
			});
			tgt.setAircraftItems(src.getAircraftItems());
		}
	}

	@Override
	public Page<AirTransport> listAirTransports(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(airTransportRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransport> listAirTransports(int page, int pageSize) {
		return Pages.adapt(airTransportRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTransportId")
	@Override
	public void deleteAirTransport(String airTransportId) {
		airTransportRepository.delete(airTransportId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteAirTransports(String tenantId) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		airTransportRepository.deleteByVendor(tenant);
	}

}
