package net.aircommunity.platform.service;

import java.util.List;
import javax.annotation.Nonnull;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CustomLandingPoint;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * CustomLandingPoint service (ADMIN ONLY)
 *
 * @author Xiangwen.Kong
 */
public interface CustomLandingPointService {

	/**
	 * Create a apron.
	 *
	 * @param customLandingPoint the customLandingPoint to be created
	 * @return customLandingPoint created
	 */
	@Nonnull
	CustomLandingPoint createCustomLandingPoint(@Nonnull CustomLandingPoint customLandingPoint, String userName);

	/**
	 * Retrieves the specified Apron.
	 *
	 * @param customLandingPointId the customLandingPointId
	 * @return the CustomLandingPoint found
	 * @throws AirException if not found
	 */
	@Nonnull
	CustomLandingPoint findCustomLandingPoint(@Nonnull String customLandingPointId);

	/**
	 * Update a Apron.
	 *
	 * @param customLandingPointId  the customLandingPointId
	 * @param newCustomLandingPoint the CustomLandingPoint to be updated
	 * @return CustomLandingPoint created
	 */
	@Nonnull
	CustomLandingPoint updateCustomLandingPoint(@Nonnull String customLandingPointId, @Nonnull CustomLandingPoint newCustomLandingPoint);

	/**
	 * List all aprons by pagination.
	 *
	 * @param page     the page number
	 * @param pageSize the pageSize
	 * @return a page of CustomLandingPoints or empty
	 */
	@Nonnull
	Page<CustomLandingPoint> listCustomLandingPoints(int page, int pageSize);

	@Nonnull
	Page<CustomLandingPoint>  listUserCustomLandingPoints(String userName, int page, int pageSize);
	/**
	 * Delete a CustomLandingPoint.
	 *
	 * @param customLandingPointId the customLandingPointId
	 */
	void deleteCustomLandingPoint(@Nonnull String customLandingPointId);

	/**
	 * Delete All CustomLandingPoints.
	 */
	void deleteCustomLandingPoints();


	CustomLandingPoint approve(String customLandingPointId, ReviewStatus reviewStatus);

	CustomLandingPoint disapprove(String customLandingPointId, ReviewStatus reviewStatus, String reason);
}
