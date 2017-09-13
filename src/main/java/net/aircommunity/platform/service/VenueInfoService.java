package net.aircommunity.platform.service;

import java.util.List;
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
	 * Create a venue info.
	 *
	 * @param venueInfo the venueInfo to be created
	 * @return venueInfo created
	 */
	@Nonnull
	VenueInfo createVenueInfo(@Nonnull VenueInfo venueInfo);

	/**
	 * Retrieves the specified venue info.
	 *
	 * @param venueInfoId the venueInfoId
	 * @return the VenueInfo found
	 * @throws AirException if not found
	 */
	@Nonnull
	VenueInfo findVenueInfo(@Nonnull String venueInfoId);

	/**
	 * Update a venue info.
	 *
	 * @param venueInfoId  the venueInfoId
	 * @param newVenueInfo the VenueInfo to be updated
	 * @return VenueInfo created
	 */
	@Nonnull
	VenueInfo updateVenueInfo(@Nonnull String venueInfoId, @Nonnull VenueInfo newVenueInfo);

	/**
	 * List all venue info by pagination.
	 *
	 * @param page     the page number
	 * @param pageSize the pageSize
	 * @return a page of VenueInfos or empty
	 */
	@Nonnull
	Page<VenueInfo> listVenueInfos(int page, int pageSize);


	/**
	 * List all venue info of venueTemplateId by pagination.
	 *
	 * @return a page of VenueInfos or empty
	 */
	@Nonnull
	List<VenueInfo> listVenueInfosByVenueTemplate(String venueTemplateId);


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
