package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.FerryFlightRepository;
import net.aircommunity.platform.service.FerryFlightService;

/**
 * FerryFlight service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class FerryFlightServiceImpl extends AbstractServiceSupport implements FerryFlightService {
	private static final String CACHE_NAME = "cache.ferryflight";

	@Resource
	private FerryFlightRepository ferryFlightRepository;

	@Override
	public FerryFlight createFerryFlight(String tenantId, FerryFlight ferryFlight) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		// create new
		FerryFlight newFerryFlight = new FerryFlight();
		copyProperties(ferryFlight, newFerryFlight);
		// set vendor
		newFerryFlight.setVendor(tenant);
		return ferryFlightRepository.save(newFerryFlight);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public FerryFlight findFerryFlight(String ferryFlightId) {
		FerryFlight ferryFlight = ferryFlightRepository.findOne(ferryFlightId);
		if (ferryFlight == null) {
			throw new AirException(Codes.FERRYFLIGHTI_NOT_FOUND,
					String.format("FerryFlight %s is not found", ferryFlightId));
		}
		return ferryFlight;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	@Override
	public FerryFlight updateFerryFlight(String ferryFlightId, FerryFlight newFerryFlight) {
		FerryFlight ferryFlight = findFerryFlight(ferryFlightId);
		copyProperties(newFerryFlight, ferryFlight);
		return ferryFlightRepository.save(ferryFlight);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	private void copyProperties(FerryFlight src, FerryFlight tgt) {
		tgt.setAircraftType(src.getAircraftType());
		tgt.setArrival(src.getArrival());
		tgt.setAvatar(src.getAvatar());
		tgt.setCurrencyUnit(src.getCurrencyUnit());
		tgt.setDate(src.getDate());
		tgt.setDeparture(src.getDeparture());
		tgt.setDescription(src.getDescription());
		tgt.setFlightNo(src.getFlightNo());
		tgt.setMinPassengers(src.getMinPassengers());
		tgt.setName(src.getName());
		tgt.setPrice(src.getPrice());
		tgt.setSeatPrice(src.getSeatPrice());
		tgt.setSeats(src.getSeats());
	}

	@Override
	public Page<FerryFlight> listFerryFlights(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(ferryFlightRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlights(int page, int pageSize) {
		return Pages.adapt(ferryFlightRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	@Override
	public void deleteFerryFlight(String ferryFlightId) {
		ferryFlightRepository.delete(ferryFlightId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteFerryFlights(String tenantId) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		ferryFlightRepository.deleteByVendor(tenant);
	}

}
