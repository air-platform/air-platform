package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.VenueInfo;

/**
 * VenueInfo service (ADMIN ONLY)
 *
 * @author Xiangwen.Kong
 */
public interface VenueInfoService {

	/**
	 * Create a apron.
	 *
	 * @param venueInfo the venueInfo to be created
	 * @return venueInfo created
	 */
	@Nonnull
	VenueInfo createVenueInfo(@Nonnull VenueInfo venueInfo);

	/**
	 * Retrieves the specified Apron.
	 *
	 * @param venueInfoId the venueInfoId
	 * @return the VenueInfo found
	 * @throws AirException if not found
	 */
	@Nonnull
	VenueInfo findVenueInfo(@Nonnull String venueInfoId);

	/**
	 * Update a Apron.
	 *
	 * @param venueInfoId  the venueInfoId
	 * @param newVenueInfo the VenueInfo to be updated
	 * @return VenueInfo created
	 */
	@Nonnull
	VenueInfo updateVenueInfo(@Nonnull String venueInfoId, @Nonnull VenueInfo newVenueInfo);

	/**
	 * List all aprons by pagination.
	 *
	 * @param page     the page number
	 * @param pageSize the pageSize
	 * @return a page of VenueInfos or empty
	 */
	@Nonnull
	Page<VenueInfo> listVenueInfos(int page, int pageSize);


	/**
	 * Delete a VenueInfo.
	 *
	 * @param venueInfoId the venueInfoId
	 */
	void deleteVenueInfo(@Nonnull String venueInfoId);

	/**
	 * Delete All VenueInfos.
	 */
	void deleteVenueInfos();
}
