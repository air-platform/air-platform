package net.aircommunity.platform.service;

import java.util.List;
import javax.annotation.Nonnull;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.VenueTemplate;

/**
 * VenueTemplate service (ADMIN ONLY)
 *
 * @author Xiangwen.Kong
 */
public interface VenueTemplateService {

	/**
	 * Create a apron.
	 *
	 * @param venueTemplate the venueTemplate to be created
	 * @return venueTemplate created
	 */
	@Nonnull
	VenueTemplate createVenueTemplate(@Nonnull VenueTemplate venueTemplate);

	/**
	 * Retrieves the specified Apron.
	 *
	 * @param venueTemplateId the venueTemplateId
	 * @return the VenueTemplate found
	 * @throws AirException if not found
	 */
	@Nonnull
	VenueTemplate findVenueTemplate(@Nonnull String venueTemplateId);


	@Nonnull
	VenueTemplate publish(@Nonnull String venueTemplateId, boolean isPublish);

	/**
	 * Update a Apron.
	 *
	 * @param venueTemplateId  the venueTemplateId
	 * @param newVenueTemplate the VenueTemplate to be updated
	 * @return VenueTemplate created
	 */
	@Nonnull
	VenueTemplate updateVenueTemplate(@Nonnull String venueTemplateId, @Nonnull VenueTemplate newVenueTemplate);

	/**
	 * List all aprons by pagination.
	 *
	 * @param page     the page number
	 * @param pageSize the pageSize
	 * @return a page of VenueTemplates or empty
	 */
	@Nonnull
	Page<VenueTemplate> listVenueTemplates(int page, int pageSize);

	@Nonnull
	List<VenueTemplate> listPublicVenueTemplates();
	/**
	 * Delete a VenueTemplate.
	 *
	 * @param venueTemplateId the venueTemplateId
	 */
	void deleteVenueTemplate(@Nonnull String venueTemplateId);

	/**
	 * Delete All VenueTemplates.
	 */
	void deleteVenueTemplates();
}
