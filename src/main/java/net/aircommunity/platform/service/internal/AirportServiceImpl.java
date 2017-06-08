package net.aircommunity.platform.service.internal;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micro.common.Strings;
import io.micro.common.io.MoreFiles;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Airport;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AirportRepository;
import net.aircommunity.platform.service.AirportService;

/**
 * Airport service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirportServiceImpl extends AbstractServiceSupport implements AirportService {
	private static final Logger LOG = LoggerFactory.getLogger(AirportServiceImpl.class);

	private static final String CACHE_NAME = "cache.airport";
	private static final String CACHE_NAME_ICAO4 = "cache.airport-icao4";
	private static final String CACHE_NAME_IATA3 = "cache.airport-iata3";

	private static final String AIRPORTS_INFO = "data/airports.json";

	@Resource
	private AirportRepository airportRepository;

	@Resource
	private ObjectMapper objectMapper;

	@PostConstruct
	private void init() {
		try {
			String json = MoreFiles.toString(AIRPORTS_INFO);
			if (json == null) {
				LOG.warn("Not airport information provided, skip importing airports info to DB");
				return;
			}
			List<Airport> airports = objectMapper.readValue(json, new TypeReference<List<Airport>>() {
			});
			int imported = 0;
			for (Airport airport : airports) {
				try {
					Airport found = airportRepository.findByIcao4IgnoreCase(airport.getIcao4());
					if (found == null) {
						Airport iata3 = airportRepository.findByIata3IgnoreCase(airport.getIata3());
						if (iata3 == null) {
							createAirport(airport);
							imported++;
						}
						else {
							LOG.warn("Icao4 available, but iata3 already exists, airport {} ignored", airport);
						}
					}
					else {
						updateAirport(found.getId(), airport);
					}
				}
				catch (Exception e) {
					LOG.warn("Failed to import airport: " + airport, e);
				}
			}
			LOG.info("Imported {}/{} airports information. ", imported, airports.size());
		}
		catch (Exception e) {
			LOG.warn("Failed to initialize airport information, casue: " + e.getMessage(), e);
		}
	}

	@Override
	public Airport createAirport(Airport airport) {
		checkExistence(airport);
		Airport newAirport = new Airport();
		copyProperties(airport, newAirport);
		return safeExecute(() -> airportRepository.save(newAirport), "Create airport %s failed", airport);
	}

	private void checkExistence(Airport airport) {
		Airport airportExisting = airportRepository.findByIcao4IgnoreCase(airport.getIcao4());
		if (airportExisting != null) {
			LOG.error("airport ICAO4 exist:{}", airport);
			throw new AirException(Codes.AIRPORT_ALREADY_EXISTS,
					M.msg(M.AIRPORT_ICAO_ALREADY_EXISTS, airport.getIcao4()));
		}
		if (Strings.isNotBlank(airport.getIata3())) {
			airportExisting = airportRepository.findByIata3IgnoreCase(airport.getIata3());
			if (airportExisting != null) {
				LOG.error("airport IATA3 exist:{}", airport);
				throw new AirException(Codes.AIRPORT_ALREADY_EXISTS,
						M.msg(M.AIRPORT_IATA_ALREADY_EXISTS, airport.getIata3()));
			}
		}
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Airport findAirport(String airportId) {
		Airport airport = airportRepository.findOne(airportId);
		if (airport == null) {
			throw new AirException(Codes.AIRPORT_NOT_FOUND, M.msg(M.AIRPORT_NOT_FOUND, airportId));
		}
		return airport;
	}

	@Override
	public Airport findAirportByCode(String code) {
		if (code.length() == 3) {
			return findAirportByIata3(code);
		}
		if (code.length() == 4) {
			return findAirportByIcao4(code);
		}
		throw new AirException(Codes.AIRPORT_INVALID_CODE, M.msg(M.AIRPORT_INVALID_CODE, code));
	}

	@Cacheable(cacheNames = CACHE_NAME_IATA3)
	@Override
	public Airport findAirportByIata3(String iata3) {
		Airport airport = airportRepository.findByIata3IgnoreCase(iata3);
		if (airport == null) {
			throw new AirException(Codes.AIRPORT_NOT_FOUND, M.msg(M.AIRPORT_NOT_FOUND, iata3));
		}
		return airport;
	}

	@Cacheable(cacheNames = CACHE_NAME_ICAO4)
	@Override
	public Airport findAirportByIcao4(String icao4) {
		Airport airport = airportRepository.findByIcao4IgnoreCase(icao4);
		if (airport == null) {
			throw new AirException(Codes.AIRPORT_NOT_FOUND, M.msg(M.AIRPORT_NOT_FOUND, icao4));
		}
		return airport;
	}

	@Caching(put = { @CachePut(value = CACHE_NAME, key = "#airportId"),
			@CachePut(value = CACHE_NAME_IATA3, key = "#result.iata3"),
			@CachePut(value = CACHE_NAME_ICAO4, key = "#result.icao4") })
	@Override
	public Airport updateAirport(String airportId, Airport newAirport) {
		Airport airportExisting = airportRepository.findByIcao4IgnoreCase(newAirport.getIcao4());
		if (airportExisting != null && !airportExisting.getId().equals(airportId)) {
			throw new AirException(Codes.AIRPORT_ALREADY_EXISTS,
					M.msg(M.AIRPORT_ICAO_ALREADY_EXISTS, newAirport.getIcao4()));
		}
		if (Strings.isNotBlank(newAirport.getIata3())) {
			airportExisting = airportRepository.findByIata3IgnoreCase(newAirport.getIata3());
			if (airportExisting != null && !airportExisting.getId().equals(airportId)) {
				throw new AirException(Codes.AIRPORT_ALREADY_EXISTS,
						M.msg(M.AIRPORT_IATA_ALREADY_EXISTS, newAirport.getIata3()));
			}
		}
		Airport airport = findAirport(airportId);
		copyProperties(newAirport, airport);
		return safeExecute(() -> airportRepository.save(airport), "Update airport %s to %s failed", airportId,
				newAirport);
	}

	private void copyProperties(Airport src, Airport tgt) {
		tgt.setCity(src.getCity());
		tgt.setCountry(src.getCountry());
		tgt.setIata3(src.getIata3());
		tgt.setIcao4(src.getIcao4());
		tgt.setLatitude(src.getLatitude());
		tgt.setLongitude(src.getLongitude());
		tgt.setName(src.getName());
		tgt.setTimezone(src.getTimezone());
	}

	@Override
	public List<Airport> listAirportsByName(String name) {
		return airportRepository.findByNameContaining(name);
	}

	@Override
	public List<Airport> listAirportsByCity(String city) {
		return airportRepository.findByCityContaining(city);
	}

	@Override
	public List<Airport> listAirportsByCityAndName(String city, String name) {
		return airportRepository.findByCityContainingAndNameContaining(city, name);
	}

	@Override
	public Page<Airport> listAirports(int page, int pageSize) {
		return Pages.adapt(airportRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Caching(evict = { @CacheEvict(value = CACHE_NAME, key = "#airportId"),
			@CacheEvict(value = CACHE_NAME_IATA3, key = "#result.iata3"),
			@CacheEvict(value = CACHE_NAME_ICAO4, key = "#result.icao4") })
	@Override
	public Airport deleteAirport(String airportId) {
		Airport airport = airportRepository.findOne(airportId);
		safeExecute(() -> airportRepository.delete(airportId), "Delete airport %s failed", airportId);
		return airport;
	}

	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_IATA3, CACHE_NAME_ICAO4 }, allEntries = true)
	@Override
	public void deleteAllAirports() {
		safeExecute(() -> airportRepository.deleteAll(), "Delete all airports failed");
	}

}
