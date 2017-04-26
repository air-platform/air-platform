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
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AircraftRepository;
import net.aircommunity.platform.repository.BaseProductRepository;
import net.aircommunity.platform.service.AircraftService;

/**
 * Aircraft service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AircraftServiceImpl extends AbstractProductService<Aircraft> implements AircraftService {
	private static final String CACHE_NAME = "cache.aircraft";

	@Resource
	private AircraftRepository aircraftRepository;

	@Override
	public Aircraft createAircraft(String tenantId, Aircraft aircraft) {
		// TODO shared?
		Aircraft aircraftExisting = aircraftRepository.findByFlightNo(aircraft.getFlightNo());
		if (aircraftExisting != null) {
			throw new AirException(Codes.AIRCRAFT_ALREADY_EXISTS,
					M.msg(M.AIRCRAFT_ALREADY_EXISTS, aircraft.getFlightNo()));
		}
		return createProduct(tenantId, aircraft);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Aircraft findAircraft(String aircraftId) {
		return findProduct(aircraftId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#aircraftId")
	@Override
	public Aircraft updateAircraft(String aircraftId, Aircraft newAircraft) {
		return updateProduct(aircraftId, newAircraft);
	}

	/**
	 * Copy properties from src to tgt without ID
	 */
	@Override
	protected void copyProperties(Aircraft src, Aircraft tgt) {
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
		return listTenantProducts(tenantId, page, pageSize);
	}

	@Override
	public Page<Aircraft> listAircrafts(int page, int pageSize) {
		return listAllProducts(page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#aircraftId")
	@Override
	public void deleteAircraft(String aircraftId) {
		deleteProduct(aircraftId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAircrafts(String tenantId) {
		deleteProducts(tenantId);
	}

	@Override
	protected Code productNotFoundCode() {
		return Codes.AIRCRAFT_NOT_FOUND;
	}

	@Override
	protected BaseProductRepository<Aircraft> getProductRepository() {
		return aircraftRepository;
	}

}
