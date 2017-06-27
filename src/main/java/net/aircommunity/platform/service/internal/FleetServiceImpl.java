package net.aircommunity.platform.service.internal;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.FleetProvider;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.FleetRepository;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.FleetService;

/**
 * Fleet service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class FleetServiceImpl extends AbstractProductService<Fleet> implements FleetService {

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.fleet";

	@Resource
	private AccountService accountService;

	@Resource
	private FleetRepository fleetRepository;

	@Override
	public Fleet createFleet(String tenantId, Fleet fleet) {
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
		Fleet fleet = findFleet(fleetId);
		Fleet fleetExisting = fleetRepository.findByFlightNo(newFleet.getFlightNo());
		if (fleetExisting != null && !fleetExisting.getId().equals(fleetId)) {
			throw new AirException(Codes.FLEET_ALREADY_EXISTS, M.msg(M.FLEET_ALREADY_EXISTS, fleet.getFlightNo()));
		}
		return doUpdateProduct(fleetId, newFleet);
	}

	@Override
	protected void copyProperties(Fleet src, Fleet tgt) {
		tgt.setFlightNo(src.getFlightNo());
		tgt.setAircraftType(src.getAircraftType());
		tgt.setLocation(src.getLocation());
		tgt.setStatus(src.getStatus());
		tgt.setBeds(src.getBeds());
		tgt.setCapacity(src.getCapacity());
		tgt.setFacilities(src.getFacilities());
		tgt.setFullloadRange(src.getFullloadRange());
		tgt.setWeight(src.getWeight());
		tgt.setAppearances(src.getAppearances());
		tgt.setInterior(src.getInterior());
	}

	@Override
	public Page<Fleet> listAllFleets(ReviewStatus reviewStatus, int page, int pageSize) {
		return doListAllProducts(reviewStatus, page, pageSize);
	}

	@Override
	public long countAllFleets(ReviewStatus reviewStatus) {
		return doCountAllProducts(reviewStatus);
	}

	@Override
	public Page<Fleet> listTenantFleets(String tenantId, ReviewStatus reviewStatus, int page, int pageSize) {
		return doListTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	@Override
	public long countTenantFleets(String tenantId, ReviewStatus reviewStatus) {
		return doCountTenantProducts(tenantId, reviewStatus);
	}

	@Override
	public Page<Fleet> listFleets(int page, int pageSize) {
		return doListProductsForUsers(page, pageSize);
	}

	@Override
	public Page<Fleet> listFleetsByType(String aircraftType, int page, int pageSize) {
		return Pages.adapt(fleetRepository.findByPublishedTrueAndAircraftTypeOrderByRankDescScoreDesc(aircraftType,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Fleet> listFleetsByProvider(String provider, int page, int pageSize) {
		return Pages.adapt(fleetRepository.findByPublishedTrueAndVendorIdOrderByRankDescScoreDesc(provider,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Fleet> listFleets(String aircraftType, String provider, int page, int pageSize) {
		return Pages.adapt(fleetRepository.findByPublishedTrueAndVendorIdAndAircraftTypeOrderByRankDescScoreDesc(
				provider, aircraftType, Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public List<FleetProvider> listFleetProviders() {
		List<Account> tenants = accountService.listAccounts(Role.TENANT, 1, Integer.MAX_VALUE).getContent();
		return tenants.stream().filter(account -> countTenantFleets(account.getId(), ReviewStatus.APPROVED) > 0)
				.map(account -> new FleetProvider(account.getId(), account.getNickName(), account.getAvatar()))
				.collect(Collectors.toList());
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
