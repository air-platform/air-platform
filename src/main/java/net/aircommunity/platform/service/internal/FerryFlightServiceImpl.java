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
		// TODO shared
		FerryFlight existing = ferryFlightRepository.findByFlightNo(ferryFlight.getFlightNo());
		if (existing != null) {
			throw new AirException(Codes.FERRYFLIGHTI_ALREADY_EXISTS,
					String.format("FerryFlight Flight NO: %s is already exists", ferryFlight.getFlightNo()));
		}
		return createProduct(tenantId, ferryFlight);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public FerryFlight findFerryFlight(String ferryFlightId) {
		return findProduct(ferryFlightId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	@Override
	public FerryFlight updateFerryFlight(String ferryFlightId, FerryFlight newFerryFlight) {
		return updateProduct(ferryFlightId, newFerryFlight);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	@Override
	protected void copyProperties(FerryFlight src, FerryFlight tgt) {
		tgt.setAircraftType(src.getAircraftType());
		tgt.setArrival(src.getArrival());
		tgt.setTimeSlot(src.getTimeSlot());
		tgt.setImage(src.getImage());
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
		return listTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<FerryFlight> listFerryFlights(int page, int pageSize) {
		return listAllProducts(page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#ferryFlightId")
	@Override
	public void deleteFerryFlight(String ferryFlightId) {
		deleteProduct(ferryFlightId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteFerryFlights(String tenantId) {
		deleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.FERRYFLIGHTI_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<FerryFlight> getProductRepository() {
		return ferryFlightRepository;
	}

}
