package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Aircraft;

/**
 * Aircraft service.
 * 
 * @author Bin.Zhang
 */
public interface AircraftService {

	/**
	 * Create a Aircraft.
	 * 
	 * @param tenantId the tenantId
	 * @param aircraft the aircraft to be created
	 * @return Aircraft created
	 */
	@Nonnull
	Aircraft createAircraft(@Nonnull String tenantId, @Nonnull Aircraft aircraft);

	/**
	 * Retrieves the specified Aircraft.
	 * 
	 * @param aircraftId the aircraftId
	 * @return the Aircraft found
	 * @throws AirException if not found
	 */
	@Nonnull
	Aircraft findAircraft(@Nonnull String aircraftId);

	/**
	 * Update a Aircraft.
	 * 
	 * @param aircraftId the aircraftId
	 * @param newAircraft the Aircraft to be updated
	 * @return Aircraft created
	 */
	@Nonnull
	Aircraft updateAircraft(@Nonnull String aircraftId, @Nonnull Aircraft newAircraft);

	/**
	 * List all Aircrafts by pagination filter by tenantId.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Aircrafts or empty
	 */
	@Nonnull
	Page<Aircraft> listAircrafts(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List all Aircrafts by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Aircrafts or empty
	 */
	@Nonnull
	Page<Aircraft> listAircrafts(int page, int pageSize);

	/**
	 * Delete a Aircraft.
	 * 
	 * @param aircraftId the aircraftId
	 */
	void deleteAircraft(@Nonnull String aircraftId);

	/**
	 * Delete Aircrafts.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteAircrafts(@Nonnull String tenantId);

}
