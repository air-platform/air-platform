package net.aircommunity.platform.service.product;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * FerryFlight service.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightService extends StandardProductService<FerryFlight> {

	/**
	 * Create a FerryFlight.
	 * 
	 * @param tenantId the tenantId
	 * @param ferryFlight the ferryFlight to be created
	 * @return FerryFlight created
	 */
	@Nonnull
	default FerryFlight createFerryFlight(@Nonnull String tenantId, @Nonnull FerryFlight ferryFlight) {
		return createProduct(tenantId, ferryFlight);
	}

	/**
	 * Retrieves the specified ferryFlight.
	 * 
	 * @param ferryFlightId the ferryFlightId
	 * @return the FerryFlight found
	 * @throws AirException if not found
	 */
	@Nonnull
	default FerryFlight findFerryFlight(@Nonnull String ferryFlightId) {
		return findProduct(ferryFlightId);
	}

	/**
	 * Update a FerryFlight.
	 * 
	 * @param ferryFlightId the ferryFlightId
	 * @param newFerryFlight the FerryFlight to be updated
	 * @return FerryFlight created
	 */
	@Nonnull
	default FerryFlight updateFerryFlight(@Nonnull String ferryFlightId, @Nonnull FerryFlight newFerryFlight) {
		return updateProduct(ferryFlightId, newFerryFlight);
	}

	/**
	 * List all FerryFlights by pagination filter by reviewStatus. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlights or empty
	 */
	@Nonnull
	default Page<FerryFlight> listAllFerryFlights(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, page, pageSize);
	}

	default long countAllFerryFlights(@Nullable ReviewStatus reviewStatus) {
		return countAllProducts(reviewStatus);
	}

	/**
	 * List all FerryFlights by pagination filter by tenantId. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlights or empty
	 */
	@Nonnull
	default Page<FerryFlight> listTenantFerryFlights(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus,
			int page, int pageSize) {
		return listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	default long countTenantFerryFlights(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus) {
		return countTenantProducts(tenantId, reviewStatus);
	}

	/**
	 * List all FerryFlights by pagination. (USER)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlights or empty
	 */
	@Nonnull
	Page<FerryFlight> listFerryFlights(int page, int pageSize);

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
	Page<FerryFlight> listFerryFlightsByFuzzyLocation(@Nonnull String location, int page, int pageSize);

	@Nonnull
	List<FerryFlight> listTop3FerryFlights(@Nullable String departure);

	/**
	 * Delete a FerryFlight.
	 * 
	 * @param ferryFlightId the ferryFlightId
	 */
	default void deleteFerryFlight(@Nonnull String ferryFlightId) {
		deleteProduct(ferryFlightId);
	}

	/**
	 * Delete FerryFlights.
	 * 
	 * @param tenantId the tenantId
	 */
	default void deleteFerryFlights(@Nonnull String tenantId) {
		deleteProducts(tenantId);
	}

}
