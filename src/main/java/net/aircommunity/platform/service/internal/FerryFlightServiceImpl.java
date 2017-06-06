package net.aircommunity.platform.service.internal;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.repository.FerryFlightRepository;
import net.aircommunity.platform.service.FerryFlightService;

/**
 * FerryFlight service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class FerryFlightServiceImpl extends AbstractProductService<FerryFlight> implements FerryFlightService {
	private static final String CACHE_NAME = "cache.ferryflight";

	@Resource
	private FerryFlightRepository ferryFlightRepository;

	@Override
	public FerryFlight createFerryFlight(String tenantId, FerryFlight ferryFlight) {
		return doCreateProduct(tenantId, ferryFlight);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public FerryFlight findFerryFlight(String ferryFlightId) {
		return doFindProduct(ferryFlightId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	@Override
	public FerryFlight updateFerryFlight(String ferryFlightId, FerryFlight newFerryFlight) {
		return doUpdateProduct(ferryFlightId, newFerryFlight);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	@Override
	protected void copyProperties(FerryFlight src, FerryFlight tgt) {
		tgt.setFlightNo(src.getFlightNo());
		tgt.setAircraftType(src.getAircraftType());
		tgt.setArrival(src.getArrival());
		tgt.setTimeSlot(src.getTimeSlot());
		tgt.setDepartureDate(src.getDepartureDate());
		tgt.setDeparture(src.getDeparture());
		tgt.setMinPassengers(src.getMinPassengers());
		tgt.setCurrencyUnit(src.getCurrencyUnit());
		tgt.setPrice(src.getPrice());
		tgt.setSeatPrice(src.getSeatPrice());
		tgt.setSeats(src.getSeats());
		tgt.setAppearances(src.getAppearances());
	}

	@Override
	public Page<FerryFlight> listFerryFlights(String tenantId, int page, int pageSize) {
		return doListTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<FerryFlight> listFerryFlights(int page, int pageSize) {
		return doListAllProducts(page, pageSize);
	}

	@Override
	public Page<FerryFlight> listFerryFlights(boolean approved, int page, int pageSize) {
		return doListAllProducts(approved, page, pageSize);
	}

	@Override
	public long countFerryFlights(boolean approved) {
		return doCountAllProducts(approved);
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByDeparture(String departure, int page, int pageSize) {
		return Pages.adapt(ferryFlightRepository.findByDepartureOrderByRankAscPriceAsc(departure,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> listFerryFlightsByArrival(String arrival, int page, int pageSize) {
		return Pages.adapt(ferryFlightRepository.findByArrivalOrderByRankAscPriceAsc(arrival,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<FerryFlight> searchFerryFlightsByLocation(String location, int page, int pageSize) {
		return Pages.adapt(ferryFlightRepository.findByDepartureContainingOrArrivalContainingOrderByRankAsc(location,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public List<FerryFlight> listTop3FerryFlights(String departure) {
		if (departure == null) {
			return ferryFlightRepository.findTop3ByOrderByRankAscPriceAsc();
		}
		return ferryFlightRepository.findTop3ByDepartureOrderByRankAscPriceAsc(departure);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	@Override
	public void deleteFerryFlight(String ferryFlightId) {
		doDeleteProduct(ferryFlightId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteFerryFlights(String tenantId) {
		doDeleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.FERRYFLIGHT_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<FerryFlight> getProductRepository() {
		return ferryFlightRepository;
	}
}
