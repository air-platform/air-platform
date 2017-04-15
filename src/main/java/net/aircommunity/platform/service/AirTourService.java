package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;

/**
 * Created by guankai on 14/04/2017.
 */
public interface AirTourService {

	@NotNull
	AirTour createAirTour(@Nonnull String tenantId, @Nonnull AirTour airTour);

	@NotNull
	AirTour findAirTour(@Nonnull String airTourId);

	@Nonnull
	AirTour updateAirTour(@Nonnull String airTourId, @Nonnull AirTour newAirTour);

	@Nonnull
	Page<AirTour> listAirTours(int page, int pageSize);

	@Nonnull
	Page<AirTour> listAirTours(@Nonnull String tenantId, int page, int pageSize);

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
