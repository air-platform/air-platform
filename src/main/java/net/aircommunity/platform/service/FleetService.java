package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Page;

/**
 * Fleet service.
 * 
 * @author Bin.Zhang
 */
public interface FleetService {

	/**
	 * Create a fleet.
	 * 
	 * @param tenantId the tenantId
	 * @param fleet the fleet to be created
	 * @return fleet created
	 */
	@Nonnull
	Fleet createFleet(@Nonnull String tenantId, @Nonnull Fleet fleet);

	/**
	 * Retrieves the specified Fleet.
	 * 
	 * @param fleetId the fleetId
	 * @return the Fleet found
	 * @throws AirException if not found
	 */
	@Nonnull
	Fleet findFleet(@Nonnull String fleetId);

	/**
	 * Update a fleet.
	 * 
	 * @param fleetId the fleetId
	 * @param newFleet the fleet to be updated
	 * @return fleet created
	 */
	@Nonnull
	Fleet updateFleet(@Nonnull String fleetId, @Nonnull Fleet newFleet);

	/**
	 * List all fleets by pagination filter by tenantId.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of fleets or empty
	 */
	@Nonnull
	Page<Fleet> listFleets(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List all fleets by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of fleets or empty
	 */
	@Nonnull
	Page<Fleet> listFleets(int page, int pageSize);

	/**
	 * List all fleets by type and pagination.
	 * 
	 * @param type the fleet type
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of fleets or empty
	 */
	@Nonnull
	Page<Fleet> listFleetsByType(String type, int page, int pageSize);

	/**
	 * Delete a fleet.
	 * 
	 * @param fleetId the fleetId
	 */
	void deleteFleet(@Nonnull String fleetId);

	/**
	 * Delete fleets.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteFleets(@Nonnull String tenantId);

}
