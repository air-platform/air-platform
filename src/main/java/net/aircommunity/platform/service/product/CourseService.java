package net.aircommunity.platform.service.product;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * Course Service.
 * 
 * @author Bin.Zhang
 */
public interface CourseService extends StandardProductService<Course> {

	@Nonnull
	Set<String> listAircraftTypes();

	@Nonnull
	Set<String> listAircraftLicenses();

	@Nonnull
	Set<String> listCourseLocations();

	@Nonnull
	Course createCourse(@Nonnull String schoolId, @Nonnull Course course);

	@Nonnull
	default Course findCourse(@Nonnull String courseId) {
		return findProduct(courseId);
	}

	@Nonnull
	Course updateCourse(@Nonnull String courseId, @Nonnull Course newCourse);

	/**
	 * List all courses by pagination. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	default Page<Course> listAllCourses(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, page, pageSize);
	}

	default long countAllCourses(@Nullable ReviewStatus reviewStatus) {
		return countAllProducts(reviewStatus);
	}

	/**
	 * List tenant courses by pagination. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	default Page<Course> listTenantCourses(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize) {
		return listTenantProducts(tenantId, reviewStatus, page, pageSize);
	}

	default long countTenantCourses(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus) {
		return countTenantProducts(tenantId, reviewStatus);
	}

	/**
	 * List courses by pagination. (USER)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<Course> listCourses(int page, int pageSize);

	/**
	 * List hot courses (top10 valid courses) (USER)
	 */
	@Nonnull
	List<Course> listTop10HotCourses();

	@Nonnull
	Page<Course> listCoursesBySchool(@Nonnull String schoolId, int page, int pageSize);

	@Nonnull
	Page<Course> listCoursesByLocation(@Nonnull String location, int page, int pageSize);

	@Nonnull
	Page<Course> listCoursesWithConditions(@Nullable String location, @Nullable String aircraftType,
			@Nullable String license, int page, int pageSize);

	/**
	 * delete a course
	 *
	 * @param courseId
	 */
	void deleteCourse(@Nonnull String courseId);

	/**
	 * delete all courses of a school
	 *
	 * @param schoolId
	 */
	void deleteCourses(@Nonnull String schoolId);

}
