package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Comment;
import net.aircommunity.platform.model.Page;

/**
 * Comment service.
 * 
 * @author Bin.Zhang
 */
public interface CommentService {

	/**
	 * Check if the account can make comment on the product.
	 * 
	 * @param accountId the accountId
	 * @param orderId the orderId
	 * @return true if the account can comment on the product, false otherwise
	 */
	boolean isCommentAllowed(@Nonnull String accountId, @Nonnull String orderId);

	/**
	 * Create a Comment.
	 * 
	 * @param accountId the accountId
	 * @param productId the productId
	 * @param comment the comment to be created
	 * @return Comment created
	 */
	@Nonnull
	Comment createComment(@Nonnull String accountId, @Nonnull String productId, @Nonnull Comment comment);

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
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Comments or empty
	 */
	@Nonnull
	Page<Comment> listComments(@Nonnull String productId, int page, int pageSize);

	/**
	 * Delete a Comment.
	 * 
	 * @param commentId the commentId
	 */
	void deleteComment(@Nonnull String commentId);

	/**
	 * Delete Comments.
	 * 
	 * @param productId the productId
	 */
	void deleteComments(@Nonnull String productId);

}
