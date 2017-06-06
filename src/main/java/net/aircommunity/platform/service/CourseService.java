package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Page;

/**
 * Created by guankai on 11/04/2017.
 */
public interface CourseService {

	@Nonnull
	Course createCourse(@Nonnull String schoolId, @Nonnull Course course);

	@Nonnull
	Course findCourse(@Nonnull String courseId);

	@Nonnull
	Course updateCourse(@Nonnull String courseId, @Nonnull Course newCourse);

	/**
	 * List all courses (of all tenants) for ADMIN
	 */
	@Nonnull
	Page<Course> listCourses(int page, int pageSize);

	/**
	 * List all courses by pagination.
	 * 
	 * @param approved the approved status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransports or empty
	 */
	@Nonnull
	Page<Course> listCourses(boolean approved, int page, int pageSize);

	long countCourses(boolean approved);

	/**
	 * List all courses for TENANT
	 */
	@Nonnull
	Page<Course> listCourses(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List hot courses (top10 valid courses) for USER
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
