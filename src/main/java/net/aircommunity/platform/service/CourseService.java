package net.aircommunity.platform.service;

import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Page;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

/**
 * Created by guankai on 11/04/2017.
 */
public interface CourseService {
    @Nonnull
    Page<Course> getAllCourses(int page, int pageSize);

    /**
     * get all courses by school
     *
     * @param schoolId
     * @param page
     * @param pageSize
     * @return
     */
    @Nonnull
    Page<Course> getAllCourseBySchool(@Nonnull String schoolId, int page, int pageSize);

    /**
     * get all courses by tenant
     *
     * @param tenantId
     * @param page
     * @param pageSize
     * @return
     */
    @NotNull
    Page<Course> getAllCourseByTenant(@Nonnull String tenantId, int page, int pageSize);

    /**
     * get courses by school the date between startDate and endDate
     *
     * @param schoolId
     * @param page
     * @param pageSize
     * @return
     */
    @Nonnull
    Page<Course> getCourseBySchool(@Nonnull String schoolId, int page, int pageSize);

    /**
     * get course by tenant the date between startDate and endDate
     *
     * @param tenantId
     * @param page
     * @param pageSize
     * @return
     */
    @Nonnull
    Page<Course> getCourseByTenant(@Nonnull String tenantId, int page, int pageSize);

    /**
     * get hot courses
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Nonnull
    Page<Course> getHotCourses(int page, int pageSize);

    /**
     * create a course
     *
     * @param course
     * @param schoolId
     * @param tenantId
     * @return
     */
    @Nonnull
    Course createCourse(@Nonnull Course course, @Nonnull String schoolId, @Nonnull String tenantId);

    /**
     * update a course
     *
     * @param course
     * @param schoolId
     * @return
     */
    @Nonnull
    Course updateCourse(@Nonnull Course course, @Nonnull String schoolId);

    /**
     * delete a course
     *
     * @param courseId
     */
    @Nonnull
    void deleteCourse(@Nonnull String courseId);

    /**
     * get course by id
     *
     * @param courseId
     * @return
     */
    @Nonnull
    Course getCourseById(@Nonnull String courseId);

    @Nonnull
    Page<Course> getCourseByAirType(@Nonnull String airType, int page, int pageSize);


}
