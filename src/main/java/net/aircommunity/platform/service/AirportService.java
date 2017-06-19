package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Airport;

/**
 * Airport service. (for ADMIN only)
 * 
 * @author Bin.Zhang
 */
public interface AirportService {

	/**
	 * Create a Airport.
	 * 
	 * @param airport the airport to be created
	 * @return Airport created
	 */
	@Nonnull
	Airport createAirport(@Nonnull Airport airport);

	/**
	 * Retrieves the specified Airport.
	 * 
	 * @param airportId the airportId
	 * @return the Airport found
	 * @throws AirException if not found
	 */
	@Nonnull
	Airport findAirport(@Nonnull String airportId);

	/**
	 * Retrieves the specified Airport by icao4 or iata3.
	 * 
	 * @param code the code
	 * @return the Airport found
	 * @throws AirException if not found
	 */
	@Nonnull
	Airport findAirportByCode(@Nonnull String code);

	/**
	 * Retrieves the specified Airport.
	 * 
	 * @param iata3 the iata3
	 * @return the Airport found
	 * @throws AirException if not found
	 */
	@Nonnull
	Airport findAirportByIata3(@Nonnull String iata3);

	/**
	 * Retrieves the specified Airport.
	 * 
	 * @param icao4 the icao4
	 * @return the Airport found
	 * @throws AirException if not found
	 */
	@Nonnull
	Airport findAirportByIcao4(@Nonnull String icao4);

	/**
	 * Update a Airport.
	 * 
	 * @param airportId the airportId
	 * @param newAirport the Airport to be updated
	 * @return airport created
	 */
	@Nonnull
	Airport updateAirport(@Nonnull String airportId, @Nonnull Airport newAirport);

	/**
	 * List all Airports by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of airports or empty
	 */
	@Nonnull
	Page<Airport> listAirports(int page, int pageSize);

	/**
	 * List airports by name.
	 * 
	 * @param name the name
	 * @return a list of airports or empty
	 */
	@Nonnull
	List<Airport> listAirportsByName(String name);

	/**
	 * List airports by city.
	 * 
	 * @param city the city
	 * @return a list of airports or empty
	 */
	@Nonnull
	List<Airport> listAirportsByCity(String city);

	/**
	 * List airports by city and name.
	 * 
	 * @param city the city
	 * @param name the name
	 * @return a list of airports or empty
	 */
	@Nonnull
	List<Airport> listAirportsByCityAndName(String city, String name);

	/**
	 * Delete a Airport.
	 * 
	 * @param airportId the airportId
	 * @return Airport deleted or null
	 */
	@Nullable
	Airport deleteAirport(@Nonnull String airportId);

	/**
	 * Delete all Airports.
	 */
	void deleteAllAirports();

}
