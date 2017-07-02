package net.aircommunity.platform.service.product;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.FleetProvider;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * Fleet service.
 * 
 * @author Bin.Zhang
 */
public interface FleetService extends StandardProductService<Fleet> {

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
	default Fleet findFleet(@Nonnull String fleetId) {
		return findProduct(fleetId);
	}

	@Nonnull
	Fleet findFleetByFlightNo(@Nonnull String flightNo);

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
	 * List all fleets by pagination filter by reviewStatus. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of fleets or empty
	 */
	@Nonnull
	default Page<Fleet> listAllFleets(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, page, pageSize);
	}

	default long countAllFleets(@Nullable ReviewStatus reviewStatus) {
		return countAllProducts(reviewStatus);
	}

	/**
	 * List tenant fleets by pagination filter by reviewStatus. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of fleets or empty
	 */
	@Nonnull
	default Page<Fleet> listTenantFleets(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize) {
		return listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	default long countTenantFleets(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus) {
		return countTenantProducts(tenantId, reviewStatus);
	}

	/**
	 * List all fleets by pagination. (USER)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of fleets or empty
	 */
	@Nonnull
	default Page<Fleet> listFleets(int page, int pageSize) {
		return listProducts(page, pageSize);
	}

	@Nonnull
	default List<Fleet> listFleets() {
		return listFleets(1, Integer.MAX_VALUE).getContent();
	}

	/**
	 * List all fleets by type and pagination.
	 * 
	 * @param aircraftType the fleet aircraftType
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of fleets or empty
	 */
	@Nonnull
	Page<Fleet> listFleetsByType(@Nonnull String aircraftType, int page, int pageSize);

	@Nonnull
	Page<Fleet> listFleetsByProvider(@Nonnull String provider, int page, int pageSize);

	@Nonnull
	Page<Fleet> listFleets(@Nonnull String aircraftType, @Nonnull String provider, int page, int pageSize);

	List<FleetProvider> listFleetProviders();

	/**
	 * Delete a fleet.
	 * 
	 * @param fleetId the fleetId
	 */
	default void deleteFleet(@Nonnull String fleetId) {
		deleteProduct(fleetId);
	}

	/**
	 * Delete fleets.
	 * 
	 * @param tenantId the tenantId
	 */
	default void deleteFleets(@Nonnull String tenantId) {
		deleteProducts(tenantId);
	}

}
