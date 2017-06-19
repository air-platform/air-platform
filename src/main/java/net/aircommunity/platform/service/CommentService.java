package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Comment;
import net.aircommunity.platform.model.domain.Comment.Source;

/**
 * Comment service.
 * 
 * @author Bin.Zhang
 */
public interface CommentService {

	/**
	 * Get total comments count overall
	 * 
	 * @return total comments count
	 */
	long getTotalCommentsCount();

	/**
	 * Check if the account can make comment on the product.
	 * 
	 * @param accountId the accountId
	 * @param orderId the orderId
	 * @return true if the account can comment on the product, false otherwise
	 */
	boolean isCommentAllowed(@Nonnull String accountId, @Nonnull String orderId);

	/**
	 * Create a Comment from a source, and rate is ignored if source is product.
	 * 
	 * @param accountId the accountId
	 * @param source the source
	 * @param sourceId either orderId or productId
	 * @param comment the comment to be created
	 * @return Comment created
	 */
	@Nonnull
	Comment createComment(@Nonnull String accountId, @Nonnull Source source, @Nonnull String sourceId,
			@Nonnull Comment comment);

	/**
	 * Retrieves the specified Comment.
	 * 
	 * @param commentId the commentId
	 * @return the Comment found
	 * @throws AirException if not found
	 */
	@Nonnull
	Comment findComment(@Nonnull String commentId);

	/**
	 * List all Comments by pagination filter by productId.
	 * 
	 * @param productId the productId
	 * @param source the source
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Comments or empty
	 */
	@Nonnull
	Page<Comment> listComments(@Nonnull String productId, @Nullable Source source, int page, int pageSize);

	/**
	 * Delete a Comment.
	 * 
	 * @param commentId the commentId
	 */
	void deleteComment(@Nonnull String commentId);

	/**
	 * Delete all comments of a product.
	 * 
	 * @param productId the productId
	 */
	void deleteComments(@Nonnull String productId);

	/**
	 * Delete all comments of a account.
	 * 
	 * @param accountId the accountId
	 */
	void deleteCommentsOfAccount(@Nonnull String accountId);

}
