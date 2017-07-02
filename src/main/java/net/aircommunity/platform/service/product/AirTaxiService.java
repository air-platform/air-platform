package net.aircommunity.platform.service.product;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * AirTaxi Service
 * 
 * @author guankai
 */
public interface AirTaxiService extends StandardProductService<AirTaxi> {

	@Nonnull
	default AirTaxi createAirTaxi(@Nonnull String tenantId, @Nonnull AirTaxi airTaxi) {
		return createProduct(tenantId, airTaxi);
	}

	@Nonnull
	default AirTaxi findAirTaxi(@Nonnull String airTaxiId) {
		return findProduct(airTaxiId);
	}

	@Nonnull
	default AirTaxi updateAirTaxi(@Nonnull String airTaxiId, @Nonnull AirTaxi airTaxi) {
		return updateProduct(airTaxiId, airTaxi);
	}

	/**
	 * ADMIN
	 */
	@Nonnull
	default Page<AirTaxi> listAllAirTaxis(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, page, pageSize);
	}

	default long countAllAirTaxis(@Nullable ReviewStatus reviewStatus) {
		return countAllProducts(reviewStatus);
	}

	/**
	 * TENANT
	 */
	@Nonnull
	default Page<AirTaxi> listTenantAirTaxis(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize) {
		return listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	default long countTenantAirTaxis(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus) {
		return countTenantProducts(tenantId, reviewStatus);
	}

	/**
	 * USER
	 */
	default Page<AirTaxi> listAirTaxis(int page, int pageSize) {
		return listProducts(page, pageSize);
	}

	Set<String> listArrivalsFromDeparture(@Nonnull String departure);

	Set<String> listDeparturesToArrival(@Nonnull String arrival);

	// list a tenant's all AirTaxis filter by departure & arrival
	@Nonnull
	Page<AirTaxi> listAirTaxisWithConditions(@Nullable String departure, @Nullable String arrival,
			@Nullable String tenantId, int page, int pageSize);

	/**
	 * Search AirTaxis.
	 * 
	 * @param location departure or arrival
	 * @param page the page start
	 * @param pageSize the page size
	 * @return a page of result
	 */
	@Nonnull
	Page<AirTaxi> listAirTaxisByFuzzyLocation(@Nonnull String location, int page, int pageSize);

	/**
	 * Delete a AirTaxi.
	 * 
	 * @param airTaxiId the airTaxiId
	 */
	default void deleteAirTaxi(@Nonnull String airTaxiId) {
		deleteProduct(airTaxiId);
	}

	/**
	 * Delete AirTaxis.
	 * 
	 * @param tenantId the tenantId
	 */
	default void deleteAirTaxis(@Nonnull String tenantId) {
		deleteProducts(tenantId);
	}

}
