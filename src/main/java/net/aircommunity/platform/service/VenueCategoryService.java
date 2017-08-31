package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.VenueCategory;

/**
 * VenueCategory service (ADMIN ONLY)
 *
 * @author Xiangwen.Kong
 */
public interface VenueCategoryService {

	/**
	 * Create a apron.
	 *
	 * @param venueCategory the venueCategory to be created
	 * @return venueCategory created
	 */
	@Nonnull
	VenueCategory createVenueCategory(@Nonnull VenueCategory venueCategory);

	/**
	 * Retrieves the specified Apron.
	 *
	 * @param venueCategoryId the venueCategoryId
	 * @return the VenueCategory found
	 * @throws AirException if not found
	 */
	@Nonnull
	VenueCategory findVenueCategory(@Nonnull String venueCategoryId);

	/**
	 * Update a Apron.
	 *
	 * @param venueCategoryId  the venueCategoryId
	 * @param newVenueCategory the VenueCategory to be updated
	 * @return VenueCategory created
	 */
	@Nonnull
	VenueCategory updateVenueCategory(@Nonnull String venueCategoryId, @Nonnull VenueCategory newVenueCategory);

	/**
	 * List all aprons by pagination.
	 *
	 * @param page     the page number
	 * @param pageSize the pageSize
	 * @return a page of VenueCategorys or empty
	 */
	@Nonnull
	Page<VenueCategory> listVenueCategorys(int page, int pageSize);


	/**
	 * Delete a VenueCategory.
	 *
	 * @param venueCategoryId the venueCategoryId
	 */
	void deleteVenueCategory(@Nonnull String venueCategoryId);

	/**
	 * Delete All VenueCategorys.
	 */
	void deleteVenueCategorys();
}
