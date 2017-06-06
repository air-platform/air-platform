package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Page;

/**
 * AirTaxi Service
 * 
 * @author guankai
 */
public interface AirTaxiService {

	@Nonnull
	AirTaxi createAirTaxi(@Nonnull String tenantId, @Nonnull AirTaxi airTaxi);

	@Nonnull
	AirTaxi findAirTaxi(@Nonnull String airTaxiId);

	@Nonnull
	AirTaxi updateAirTaxi(@Nonnull String airTaxiId, @Nonnull AirTaxi airTaxi);

	@Nonnull
	Page<AirTaxi> listAirTaxis(int page, int pageSize);

	@Nonnull
	Page<AirTaxi> listAirTaxis(boolean approved, int page, int pageSize);

	long countAirTaxis(boolean approved);

	@Nonnull
	Page<AirTaxi> listAirTaxis(String tenantId, int page, int pageSize);

	@Nonnull
	Page<AirTaxi> listAirTaxisByDeparture(@Nonnull String departure, int page, int pageSize);

	@Nonnull
	Page<AirTaxi> listAirTaxisByArrival(@Nonnull String arrival, int page, int pageSize);

	/**
	 * Search AirTaxis.
	 * 
	 * @param location departure or arrival
	 * @param page the page start
	 * @param pageSize the page size
	 * @return a page of result
	 */
	@Nonnull
	Page<AirTaxi> searchAirTaxisByLocation(@Nonnull String location, int page, int pageSize);

	/**
	 * Delete a AirTaxi.
	 * 
	 * @param airairTaxiId the airairTaxiId
	 */
	void deleteAirTaxi(@Nonnull String airTaxiId);

	/**
	 * Delete AirTaxis.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteAirTaxis(@Nonnull String tenantId);

}
