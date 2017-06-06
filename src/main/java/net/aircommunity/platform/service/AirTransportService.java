package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.ProductFamily;

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
	 * List all AirTransports by pagination filter by tenantId.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAirTransports(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List all AirTransports by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAirTransports(int page, int pageSize);

	/**
	 * List all AirTransports by pagination.
	 * 
	 * @param approved the approved status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAirTransports(boolean approved, int page, int pageSize);

	/**
	 * Count AirTransports by approved status
	 * 
	 * @param approved the approved status
	 * @return count
	 */
	long countAirTransports(boolean approved);

	/**
	 * List all AirTransports by family and pagination.
	 * 
	 * @param familyId the familyId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<AirTransport> listAirTransportsByFamily(@Nonnull String familyId, int page, int pageSize);

	@Nonnull
	Page<AirTransport> listAirTransportsByDeparture(@Nonnull String departure, int page, int pageSize);

	@Nonnull
	Page<AirTransport> listAirTransportsByArrival(@Nonnull String arrival, int page, int pageSize);

	/**
	 * Search AirTransports.
	 * 
	 * @param location departure or arrival
	 * @param page the page start
	 * @param pageSize the page size
	 * @return a page of result
	 */
	@Nonnull
	Page<AirTransport> searchAirTransportsByLocation(@Nonnull String location, int page, int pageSize);

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
