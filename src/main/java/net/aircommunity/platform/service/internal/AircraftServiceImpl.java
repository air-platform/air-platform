package net.aircommunity.platform.service.internal;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import net.aircommunity.platform.nls.M;
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
	private static final Logger LOG = LoggerFactory.getLogger(AircraftServiceImpl.class);

	private static final String CACHE_NAME = "cache.aircraft";

	@Resource
	private AircraftRepository aircraftRepository;

	@Override
	public Aircraft createAircraft(String tenantId, Aircraft aircraft) {
		Tenant vendor = findAccount(tenantId, Tenant.class);
		// TODO shared?
		Aircraft aircraftExisting = aircraftRepository.findByFlightNo(aircraft.getFlightNo());
		if (aircraftExisting != null) {
			throw new AirException(Codes.AIRCRAFT_ALREADY_EXISTS,
					M.msg(M.AIRCRAFT_ALREADY_EXISTS, aircraft.getFlightNo()));
		}
		Aircraft newAircraft = new Aircraft();
		copyProperties(aircraft, newAircraft);
		newAircraft.setVendor(vendor);
		return safeExecute(() -> aircraftRepository.save(newAircraft), "Create Aircraft %s failed", aircraft);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Aircraft findAircraft(String aircraftId) {
		Aircraft aircraft = aircraftRepository.findOne(aircraftId);
		if (aircraft == null) {
			LOG.error("Aircraft: {} is not found", aircraftId);
			throw new AirException(Codes.AIRCRAFT_NOT_FOUND, M.msg(M.AIRJET_NOT_FOUND));
		}
		return aircraft;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#aircraftId")
	@Override
	public Aircraft updateAircraft(String aircraftId, Aircraft newAircraft) {
		Aircraft aircraft = findAircraft(aircraftId);
		Aircraft aircraftExisting = aircraftRepository.findByFlightNo(aircraft.getFlightNo());
		if (aircraftExisting != null && !aircraftExisting.getId().equals(aircraftId)) {
			throw new AirException(Codes.AIRCRAFT_ALREADY_EXISTS,
					M.msg(M.AIRCRAFT_ALREADY_EXISTS, aircraft.getFlightNo()));
		}
		copyProperties(newAircraft, aircraft);
		return safeExecute(() -> aircraftRepository.save(aircraft), "Update aircraft %s to %s failed", aircraftId,
				aircraft);
	}

	private void copyProperties(Aircraft src, Aircraft tgt) {
		tgt.setName(src.getName());
		tgt.setImage(src.getImage());
		tgt.setDescription(src.getDescription());
		tgt.setFlightNo(src.getFlightNo());
		tgt.setCategory(src.getCategory());
		tgt.setMinPassengers(src.getMinPassengers());
		tgt.setSeats(src.getSeats());
		tgt.setType(src.getType());
		tgt.setCreationDate(new Date());
	}

	@Override
	public Page<Aircraft> listAircrafts(String tenantId, int page, int pageSize) {
		return Pages.adapt(aircraftRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
				Pages.createPageRequest(page, pageSize)));
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

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAircrafts(String tenantId) {
		aircraftRepository.deleteByVendorId(tenantId);
	}

}
