package net.aircommunity.platform.service;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;

/**
 * AirTour Service.
 * 
 * @author guankai
 */
public interface AirTourService {

	@NotNull
	AirTour createAirTour(@Nonnull String tenantId, @Nonnull AirTour airTour);

	@NotNull
	AirTour findAirTour(@Nonnull String airTourId);

	@Nonnull
	AirTour updateAirTour(@Nonnull String airTourId, @Nonnull AirTour newAirTour);

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
	Page<AirTour> listAllAirTours(@Nullable ReviewStatus reviewStatus, int page, int pageSize);

	long countAllAirTours(@Nullable ReviewStatus reviewStatus);

	/**
	 * TENANT
	 */
	@Nonnull
	Page<AirTour> listTenantAirTours(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize);

	long countTenantAirTours(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus);

	/**
	 * USER
	 */
	@Nonnull
	Page<AirTour> listAirTours(int page, int pageSize);

	@Nonnull
	Page<AirTour> listAirToursByCity(@Nonnull String city, int page, int pageSize);

	/**
	 * Delete a AirTour.
	 * 
	 * @param airTourId the airTourId
	 */
	void deleteAirTour(@Nonnull String airTourId);

	/**
	 * Delete AirTours.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteAirTours(@Nonnull String tenantId);
}
