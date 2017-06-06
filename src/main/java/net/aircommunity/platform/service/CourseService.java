package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;

/**
 * Course Service.
 * 
 * @author guankai
 */
public interface CourseService {

	@Nonnull
	Course createCourse(@Nonnull String schoolId, @Nonnull Course course);

	@Nonnull
	Course findCourse(@Nonnull String courseId);

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
	Page<Course> listAllCourses(@Nullable ReviewStatus reviewStatus, int page, int pageSize);

	long countAllCourses(@Nullable ReviewStatus reviewStatus);

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
	Page<Course> listTenantCourses(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page,
			int pageSize);

	long countTenantCourses(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus);

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
	Page<Course> listCoursesByAircraftType(@Nonnull String aircraftType, int page, int pageSize);

	@Nonnull
	Page<Course> listCoursesByLocation(@Nonnull String location, int page, int pageSize);

	@Nonnull
	Page<Course> listCoursesWithConditions(@Nullable String location, @Nullable String license,
			@Nullable String aircraftType, int page, int pageSize);

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
