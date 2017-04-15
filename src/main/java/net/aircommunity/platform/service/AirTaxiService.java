package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Page;

/**
 * Created by guankai on 14/04/2017.
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
	Page<AirTaxi> listAirTaxis(String tenantId, int page, int pageSize);

	@Nonnull
	Page<AirTaxi> listAirTaxisByDeparture(String departure, int page, int pageSize);

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
