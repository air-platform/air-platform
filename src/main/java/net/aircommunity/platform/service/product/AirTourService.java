package net.aircommunity.platform.service.product;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * AirTour Service.
 * 
 * @author guankai
 */
public interface AirTourService extends StandardProductService<AirTour> {

	@NotNull
	default AirTour createAirTour(@Nonnull String tenantId, @Nonnull AirTour airTour) {
		return createProduct(tenantId, airTour);
	}

	@NotNull
	default AirTour findAirTour(@Nonnull String airTourId) {
		return findProduct(airTourId);
	}

	@Nonnull
	default AirTour updateAirTour(@Nonnull String airTourId, @Nonnull AirTour newAirTour) {
		return updateProduct(airTourId, newAirTour);
	}

	/**
	 * List all tour cities.
	 * 
	 * @return a list of cities
	 */
	@Nonnull
	Set<String> listAirTourCities();

	/**
	 * ADMIN
	 */
	@Nonnull
	default Page<AirTour> listAllAirTours(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, page, pageSize);
	}

	default long countAllAirTours(@Nullable ReviewStatus reviewStatus) {
		return countAllProducts(reviewStatus);
	}

	/**
	 * TENANT
	 */
	@Nonnull
	default Page<AirTour> listTenantAirTours(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize) {
		return listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	default long countTenantAirTours(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus) {
		return countTenantProducts(tenantId, reviewStatus);
	}

	/**
	 * USER
	 */
	@Nonnull
	default Page<AirTour> listAirTours(int page, int pageSize) {
		return listProducts(page, pageSize);
	}

	@Nonnull
	Page<AirTour> listAirToursByCity(@Nonnull String city, int page, int pageSize);

	@Nonnull
	Page<AirTour> listAirToursByFuzzyCity(@Nonnull String city, int page, int pageSize);

	/**
	 * Delete a AirTour.
	 * 
	 * @param airTourId the airTourId
	 */
	default void deleteAirTour(@Nonnull String airTourId) {
		deleteProduct(airTourId);
	}

	/**
	 * Delete AirTours.
	 * 
	 * @param tenantId the tenantId
	 */
	default void deleteAirTours(@Nonnull String tenantId) {
		deleteProducts(tenantId);
	}
}
