package net.aircommunity.platform.service;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * AirTransport service.
 * 
 * @author Bin.Zhang
 */
public interface AirTransportService {

	/**
	 * Create a AirTransport.
	 * 
	 * @param tenantId the tenantId
	 * @param airTransport the airTransport to be created
	 * @return AirTransport created
	 */
	@Nonnull
	AirTransport createAirTransport(@Nonnull String tenantId, @Nonnull AirTransport airTransport);

	/**
	 * Retrieves the specified AirTransport.
	 * 
	 * @param airTransportId the airTransportId
	 * @return the AirTransport found
	 * @throws AirException if not found
	 */
	@Nonnull
	AirTransport findAirTransport(@Nonnull String airTransportId);

	/**
	 * Update a AirTransport.
	 * 
	 * @param airTransportId the airTransportId
	 * @param newAirTransport the AirTransport to be updated
	 * @return AirTransport created
	 */
	@Nonnull
	AirTransport updateAirTransport(@Nonnull String airTransportId, @Nonnull AirTransport newAirTransport);

	/**
	 * List all transport families.
	 * 
	 * @return a list of families
	 */
	@Nonnull
	List<ProductFamily> listAirTransportFamilies();

	/**
	 * List all AirTransports by pagination. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAllAirTransports(@Nullable ReviewStatus reviewStatus, int page, int pageSize);

	/**
	 * Count AirTransports by reviewStatus (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @return count
	 */
	long countAllAirTransports(@Nullable ReviewStatus reviewStatus);

	/**
	 * List all AirTransports by pagination filter by tenantId. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listTenantAirTransports(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize);

	/**
	 * Count AirTransports by reviewStatus for tenant (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @return count
	 */
	long countTenantAirTransports(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus);

	/**
	 * List all AirTransports by pagination. (USER)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAirTransports(int page, int pageSize);

	Set<String> listArrivalsFromDeparture(@Nonnull String familyId, @Nonnull String departure);

	Set<String> listDeparturesToArrival(@Nonnull String familyId, @Nonnull String arrival);

	/**
	 * List all AirTransports filtering by family,departure,arrival and tenantId with pagination support.
	 * 
	 * @param familyId the familyId
	 * @param departure the departure
	 * @param arrival the arrival
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAirTransportsWithConditions(@Nullable String familyId, @Nullable String departure,
			@Nullable String arrival, @Nullable String tenantId, int page, int pageSize);

	/**
	 * List all AirTransports by family and pagination. It can be replaced by
	 * {@see #listAirTransportsWithConditions(String, String, String, String, int, int)}
	 * 
	 * @param familyId the familyId ignored if null
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAirTransportsByFamily(@Nullable String familyId, int page, int pageSize);

	@Nonnull
	default Page<AirTransport> listAirTransportsByDeparture(@Nonnull String departure, int page, int pageSize) {
		return listAirTransportsWithConditions(null, departure, null, null, page, pageSize);
	}

	@Nonnull
	default Page<AirTransport> listAirTransportsByArrival(@Nonnull String arrival, int page, int pageSize) {
		return listAirTransportsWithConditions(null, null, arrival, null, page, pageSize);
	}

	/**
	 * List all AirTransports by fuzzy location.
	 * 
	 * @param location departure or arrival
	 * @param page the page start
	 * @param pageSize the page size
	 * @return a page of result
	 */
	@Nonnull
	Page<AirTransport> listAirTransportsByFuzzyLocation(@Nonnull String location, int page, int pageSize);

	/**
	 * Delete a AirTransport.
	 * 
	 * @param airTransportId the airTransportId
	 */
	void deleteAirTransport(@Nonnull String airTransportId);

	/**
	 * Delete AirTransports.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteAirTransports(@Nonnull String tenantId);

}
