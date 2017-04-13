package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.repository.FleetRepository;
import net.aircommunity.platform.service.FleetService;

/**
 * Fleet service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class FleetServiceImpl extends AbstractServiceSupport implements FleetService {
	private static final String CACHE_NAME = "cache.fleet";

	@Resource
	private FleetRepository fleetRepository;

	@Override
	public Fleet createFleet(String tenantId, Fleet fleet) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		Fleet existing = fleetRepository.findByFlightNo(fleet.getFlightNo());
		if (existing != null) {
			throw new AirException(Codes.FLEET_ALREADY_EXISTS,
					String.format("Flight NO: %s already exists", fleet.getFlightNo()));
		}
		// create new
		Fleet newFleet = new Fleet();
		copyProperties(fleet, newFleet);
		// set vendor
		newFleet.setVendor(tenant);
		return fleetRepository.save(newFleet);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Fleet findFleet(String fleetId) {
		Fleet fleet = fleetRepository.findOne(fleetId);
		if (fleet == null) {
			throw new AirException(Codes.FLEET_NOT_FOUND, String.format("Fleet %s is not found", fleetId));
		}
		return fleet;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#fleetId")
	@Override
	public Fleet updateFleet(String fleetId, Fleet newFleet) {
		Fleet fleet = findFleet(fleetId);
		copyProperties(newFleet, fleet);
		return fleetRepository.save(fleet);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	private void copyProperties(Fleet src, Fleet tgt) {
		tgt.setAircraftType(src.getAircraftType());
		tgt.setLocation(src.getLocation());
		tgt.setStatus(src.getStatus());
		tgt.setBeds(src.getBeds());
		tgt.setCapacity(src.getCapacity());
		tgt.setCurrencyUnit(src.getCurrencyUnit());
		tgt.setDescription(src.getDescription());
		tgt.setFacilities(src.getFacilities());
		tgt.setFlightNo(src.getFlightNo());
		tgt.setFullloadRange(src.getFullloadRange());
		tgt.setName(src.getName());
		tgt.setPrice(src.getPrice());
		tgt.setWeight(src.getWeight());
		tgt.setAppearances(src.getAppearances());
		tgt.setInterior(src.getInterior());
	}

	@Override
	public Page<Fleet> listFleets(String tenantId, int page, int pageSize) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		return Pages.adapt(fleetRepository.findByVendor(tenant, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Fleet> listFleets(int page, int pageSize) {
		return Pages.adapt(fleetRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#fleetId")
	@Override
	public void deleteFleet(String fleetId) {
		fleetRepository.delete(fleetId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteFleets(String tenantId) {
		Tenant tenant = findAccount(tenantId, Tenant.class);
		fleetRepository.deleteByVendor(tenant);
	}

}
