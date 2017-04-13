package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

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
