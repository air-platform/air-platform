package net.aircommunity.platform.service.product;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AircraftComment;

/**
 * Aircraft comment service.
 * 
 * @author Bin.Zhang
 */
public interface AircraftCommentService {

	/**
	 * Get total comments count overall
	 * 
	 * @return total comments count
	 */
	long getTotalCommentsCount();

	/**
	 * Create a AircraftComment from a source, and rate is ignored if source is aircraft.
	 * 
	 * @param accountId the accountId
	 * @param orderId either orderId
	 * @param comment the comment to be created
	 * @return AircraftComment created
	 */
	@Nonnull
	AircraftComment createComment(@Nonnull String accountId, @Nonnull String orderId, @Nonnull AircraftComment comment);

	/**
	 * Retrieves the specified AircraftComment.
	 * 
	 * @param commentId the commentId
	 * @return the AircraftComment found
	 * @throws AirException if not found
	 */
	@Nonnull
	AircraftComment findComment(@Nonnull String commentId);

	/**
	 * List all comments by pagination filter by aircraftId.
	 * 
	 * @param aircraftId the aircraftId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of comments or empty
	 */
	@Nonnull
	Page<AircraftComment> listComments(@Nonnull String aircraftId, int page, int pageSize);

	/**
	 * Delete a AircraftComment.
	 * 
	 * @param commentId the commentId
	 */
	void deleteComment(@Nonnull String commentId);

	/**
	 * Delete all comments of a aircraft.
	 * 
	 * @param aircraftId the aircraftId
	 */
	void deleteComments(@Nonnull String aircraftId);

	/**
	 * Delete all comments of a account.
	 * 
	 * @param accountId the accountId
	 */
	void deleteCommentsOfAccount(@Nonnull String accountId);

}
