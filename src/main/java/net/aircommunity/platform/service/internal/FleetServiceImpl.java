package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.FleetRepository;
import net.aircommunity.platform.service.FleetService;

/**
 * Fleet service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class FleetServiceImpl extends AbstractProductService<Fleet> implements FleetService {
	private static final String CACHE_NAME = "cache.fleet";

	@Resource
	private FleetRepository fleetRepository;

	@Override
	public Fleet createFleet(String tenantId, Fleet fleet) {
		// TODO shared
		Fleet existing = fleetRepository.findByFlightNo(fleet.getFlightNo());
		if (existing != null) {
			throw new AirException(Codes.FLEET_ALREADY_EXISTS, M.msg(M.FLEET_ALREADY_EXISTS, fleet.getFlightNo()));
		}
		return doCreateProduct(tenantId, fleet);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Fleet findFleet(String fleetId) {
		return doFindProduct(fleetId);
	}

	@Override
	public Fleet findFleetByFlightNo(String flightNo) {
		Fleet fleet = fleetRepository.findByFlightNo(flightNo);
		if (fleet == null) {
			throw new AirException(productNotFoundCode(), M.msg(M.FLEET_NOT_FOUND, flightNo));
		}
		return fleet;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#fleetId")
	@Override
	public Fleet updateFleet(String fleetId, Fleet newFleet) {
		return doUpdateProduct(fleetId, newFleet);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	@Override
	protected void copyProperties(Fleet src, Fleet tgt) {
		tgt.setFlightNo(src.getFlightNo());
		tgt.setAircraftType(src.getAircraftType());
		tgt.setLocation(src.getLocation());
		tgt.setStatus(src.getStatus());
		tgt.setBeds(src.getBeds());
		tgt.setCapacity(src.getCapacity());
		tgt.setCurrencyUnit(src.getCurrencyUnit());
		tgt.setFacilities(src.getFacilities());
		tgt.setFullloadRange(src.getFullloadRange());
		tgt.setPrice(src.getPrice());
		tgt.setWeight(src.getWeight());
		tgt.setAppearances(src.getAppearances());
		tgt.setInterior(src.getInterior());
	}

	@Override
	public Page<Fleet> listFleets(String tenantId, int page, int pageSize) {
		return doListTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<Fleet> listFleets(int page, int pageSize) {
		return doListAllProducts(page, pageSize);
	}

	@Override
	public Page<Fleet> listFleetsByType(String type, int page, int pageSize) {
		return Pages.adapt(fleetRepository.findByAircraftTypeOrderByCreationDateDesc(type,
				Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#fleetId")
	@Override
	public void deleteFleet(String fleetId) {
		doDeleteProduct(fleetId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteFleets(String tenantId) {
		doDeleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.FLEET_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<Fleet> getProductRepository() {
		return fleetRepository;
	}

}
