package net.aircommunity.platform.service.internal.product;

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
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Aircraft;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AircraftRepository;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.product.AircraftService;

/**
 * Aircraft service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class AircraftServiceImpl extends AbstractServiceSupport implements AircraftService {
	private static final Logger LOG = LoggerFactory.getLogger(AircraftServiceImpl.class);

	private static final String CACHE_NAME = "cache.aircraft";

	@Resource
	private AircraftRepository aircraftRepository;

	@Transactional
	@Override
	public Aircraft createAircraft(String tenantId, Aircraft aircraft) {
		Tenant vendor = findAccount(tenantId, Tenant.class);
		Aircraft aircraftExisting = aircraftRepository.findByFlightNo(aircraft.getFlightNo());
		if (aircraftExisting != null) {
			throw new AirException(Codes.AIRCRAFT_ALREADY_EXISTS,
					M.msg(M.AIRCRAFT_ALREADY_EXISTS, aircraft.getFlightNo()));
		}
		Aircraft newAircraft = new Aircraft();
		copyProperties(aircraft, newAircraft);
		newAircraft.setCreationDate(new Date());
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

	@Transactional
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

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#aircraftId")
	@Override
	public Aircraft updateAircraftScore(String aircraftId, double newScore) {
		Aircraft aircraft = findAircraft(aircraftId);
		if (newScore < 0) {
			newScore = 0;
		}
		aircraft.setScore(newScore);
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
	}

	@Override
	public Page<Aircraft> listAircrafts(String tenantId, int page, int pageSize) {
		return Pages.adapt(aircraftRepository.findByVendorId(tenantId, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Aircraft> listAircrafts(int page, int pageSize) {
		return Pages.adapt(aircraftRepository.findAll(createPageRequest(page, pageSize)));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#aircraftId")
	@Override
	public void deleteAircraft(String aircraftId) {
		safeDeletion(aircraftRepository, aircraftId, Codes.AIRCRAFT_CANNOT_BE_DELETED,
				M.msg(M.AIRCRAFT_CANNOT_BE_DELETED));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAircrafts(String tenantId) {
		safeDeletion(aircraftRepository, () -> aircraftRepository.deleteByVendorId(tenantId),
				Codes.AIRCRAFT_CANNOT_BE_DELETED, M.msg(M.AIRCRAFTS_CANNOT_BE_DELETED));
	}

}
