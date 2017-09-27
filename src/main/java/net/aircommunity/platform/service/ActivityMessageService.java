package net.aircommunity.platform.service;

import java.util.List;
import javax.annotation.Nonnull;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.ActivityMessage;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * ActivityMessage service (ADMIN ONLY)
 *
 * @author Xiangwen.Kong
 */
public interface ActivityMessageService {

	/**
	 * Create a apron.
	 *
	 * @param activityMessage the activityMessage to be created
	 * @return activityMessage created
	 */
	@Nonnull
	ActivityMessage createActivityMessage(@Nonnull ActivityMessage activityMessage, String userName);

	/**
	 * Retrieves the specified Apron.
	 *
	 * @param activityMessageId the activityMessageId
	 * @return the ActivityMessage found
	 * @throws AirException if not found
	 */
	@Nonnull
	ActivityMessage findActivityMessage(@Nonnull String activityMessageId);

	/**
	 * Update a Apron.
	 *
	 * @param activityMessageId  the activityMessageId
	 * @param newActivityMessage the ActivityMessage to be updated
	 * @return ActivityMessage created
	 */
	@Nonnull
	ActivityMessage updateActivityMessage(@Nonnull String activityMessageId, @Nonnull ActivityMessage newActivityMessage);

	/**
	 * List all aprons by pagination.
	 *
	 * @param page     the page number
	 * @param pageSize the pageSize
	 * @return a page of ActivityMessages or empty
	 */
	@Nonnull
	Page<ActivityMessage> listActivityMessages(int page, int pageSize);

	@Nonnull
	List<ActivityMessage> listUserActivityMessages(String userName);

	/**
	 * Delete a ActivityMessage.
	 *
	 * @param activityMessageId the activityMessageId
	 */
	void deleteActivityMessage(@Nonnull String activityMessageId);

	/**
	 * Delete All ActivityMessages.
	 */
	void deleteActivityMessages();

	ActivityMessage publish(String activityMessageId, boolean isPublish);

	ActivityMessage approve(String activityMessageId, ReviewStatus reviewStatus);

	ActivityMessage disapprove(String activityMessageId, ReviewStatus reviewStatus, String reason);
}
