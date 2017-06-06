package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Page;

/**
 * FerryFlight service.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightService {

	/**
	 * Create a FerryFlight.
	 * 
	 * @param tenantId the tenantId
	 * @param ferryFlight the ferryFlight to be created
	 * @return FerryFlight created
	 */
	@Nonnull
	FerryFlight createFerryFlight(@Nonnull String tenantId, @Nonnull FerryFlight ferryFlight);

	/**
	 * Retrieves the specified ferryFlight.
	 * 
	 * @param ferryFlightId the ferryFlightId
	 * @return the FerryFlight found
	 * @throws AirException if not found
	 */
	@Nonnull
	FerryFlight findFerryFlight(@Nonnull String ferryFlightId);

	/**
	 * Update a FerryFlight.
	 * 
	 * @param ferryFlightId the ferryFlightId
	 * @param newFerryFlight the FerryFlight to be updated
	 * @return FerryFlight created
	 */
	@Nonnull
	FerryFlight updateFerryFlight(@Nonnull String ferryFlightId, @Nonnull FerryFlight newFerryFlight);

	/**
	 * List all FerryFlights by pagination filter by tenantId.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlights or empty
	 */
	@Nonnull
	Page<FerryFlight> listFerryFlights(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List all FerryFlights by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlights or empty
	 */
	@Nonnull
	Page<FerryFlight> listFerryFlights(int page, int pageSize);

	/**
	 * List all FerryFlights by pagination.
	 * 
	 * @param approved the approved status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<FerryFlight> listFerryFlights(boolean approved, int page, int pageSize);

	long countFerryFlights(boolean approved);

	@Nonnull
	Page<FerryFlight> listFerryFlightsByDeparture(@Nonnull String departure, int page, int pageSize);

	@Nonnull
	Page<FerryFlight> listFerryFlightsByArrival(@Nonnull String arrival, int page, int pageSize);

	/**
	 * Search FerryFlights.
	 * 
	 * @param location departure or arrival
	 * @param page the page start
	 * @param pageSize the page size
	 * @return a page of result
	 */
	@Nonnull
	Page<FerryFlight> searchFerryFlightsByLocation(@Nonnull String location, int page, int pageSize);

	@Nonnull
	List<FerryFlight> listTop3FerryFlights(@Nullable String departure);

	/**
	 * Delete a FerryFlight.
	 * 
	 * @param ferryFlightId the ferryFlightId
	 */
	void deleteFerryFlight(@Nonnull String ferryFlightId);

	/**
	 * Delete FerryFlights.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteFerryFlights(@Nonnull String tenantId);

}
