package net.aircommunity.platform.service;

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
public interface AirTaxiService {

	@Nonnull
	AirTaxi createAirTaxi(@Nonnull String tenantId, @Nonnull AirTaxi airTaxi);

	@Nonnull
	AirTaxi findAirTaxi(@Nonnull String airTaxiId);

	@Nonnull
	AirTaxi updateAirTaxi(@Nonnull String airTaxiId, @Nonnull AirTaxi airTaxi);

	/**
	 * ADMIN
	 */
	@Nonnull
	Page<AirTaxi> listAllAirTaxis(@Nullable ReviewStatus reviewStatus, int page, int pageSize);

	long countAllAirTaxis(@Nullable ReviewStatus reviewStatus);

	/**
	 * TENANT
	 */
	@Nonnull
	Page<AirTaxi> listTenantAirTaxis(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize);

	long countTenantAirTaxis(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus);

	/**
	 * USER
	 */
	Page<AirTaxi> listAirTaxis(int page, int pageSize);

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
