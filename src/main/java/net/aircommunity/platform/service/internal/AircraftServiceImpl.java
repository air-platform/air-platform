package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.AircraftRepository;
import net.aircommunity.platform.service.AircraftService;

/**
 * Aircraft service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AircraftServiceImpl extends AbstractServiceSupport implements AircraftService {
	private static final String CACHE_NAME = "cache.aircraft";

	@Resource
	private AircraftRepository aircraftRepository;

	@Override
	public Aircraft createAircraft(String tenantId, Aircraft aircraft) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		Aircraft aircraftExisting = aircraftRepository.findByFlightNo(aircraft.getFlightNo());
		if (aircraftExisting == null) {
			throw new AirException(Codes.AIRCRAFT_ALREADY_EXISTS,
					String.format("Aircraft Flight NO: %s is not found", aircraft.getFlightNo()));
		}
		// create new
		Aircraft newAircraft = new Aircraft();
		copyProperties(aircraft, newAircraft);
		// set vendor
		newAircraft.setVendor(tenant);
		return aircraftRepository.save(newAircraft);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Aircraft findAircraft(String aircraftId) {
		Aircraft aircraft = aircraftRepository.findOne(aircraftId);
		if (aircraft == null) {
			throw new AirException(Codes.AIRCRAFT_NOT_FOUND, String.format("Aircraft %s is not found", aircraftId));
		}
		return aircraft;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#aircraftId")
	@Override
	public Aircraft updateAircraft(String aircraftId, Aircraft newAircraft) {
		Aircraft Aircraft = findAircraft(aircraftId);
		copyProperties(newAircraft, Aircraft);
		return aircraftRepository.save(Aircraft);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	private void copyProperties(Aircraft src, Aircraft tgt) {
		tgt.setFlightNo(src.getFlightNo());
		tgt.setCategory(src.getCategory());
		tgt.setDescription(src.getDescription());
		tgt.setImage(src.getImage());
		tgt.setMinPassengers(src.getMinPassengers());
		tgt.setName(src.getName());
		tgt.setSeats(src.getSeats());
		tgt.setType(src.getType());
	}

	@Override
	public Page<Aircraft> listAircrafts(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(aircraftRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Aircraft> listAircrafts(int page, int pageSize) {
		return Pages.adapt(aircraftRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#aircraftId")
	@Override
	public void deleteAircraft(String aircraftId) {
		aircraftRepository.delete(aircraftId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteAircrafts(String tenantId) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		aircraftRepository.deleteByVendor(tenant);
	}

}
