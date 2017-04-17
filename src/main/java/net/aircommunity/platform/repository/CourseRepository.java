package net.aircommunity.platform.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Course;

/**
 * Created by guankai on 12/04/2017.
 */
public interface CourseRepository extends JpaRepository<Course, String> {

	// For ADMIN

	/**
	 * List all courses
	 */
	Page<Course> findAllByOrderByStartDateDesc(Pageable pageable);

	// For TENANT

	/**
	 * List courses for tenant
	 */
	Page<Course> findByVendorIdOrderByStartDateDesc(String tenantId, Pageable pageable);

	// For USER (always findByEndDateGreaterThan)

	/**
	 * Hot Top 10
	 */
	List<Course> findTop10ByEndDateGreaterThanEqualOrderByEnrollNumDesc(Date endDate);

	/**
	 * List courses for user (only valid courses), filter type airType
	 */
	Page<Course> findByEndDateGreaterThanEqualOrderByStartDateDesc(Date endDate, Pageable pageable);

	Page<Course> findByEndDateGreaterThanEqualAndAirTypeContainingOrderByStartDateDesc(Date endDate, String airType,
			Pageable pageable);

	/**
	 * List courses for user (only valid courses) of a school
	 */
	Page<Course> findBySchoolIdAndEndDateGreaterThanEqualOrderByStartDateDesc(String schoolId, Date endDate,
			Pageable pageable);

	long deleteBySchoolId(String schoolId);

	// TODO REMOVE
	// @Query("select t from Course t where t.endDate >= :now and t.school = :sc order by t.startDate desc")
	// Page<Course> findBySchool2(@Param("sc") School school, @Param("now") Date now, Pageable pageable);
	//
	// @Query("select t from Course t where t.endDate >= :now and t.airType like %:airType% order by t.startDate desc")
	// Page<Course> findValidCourses(@Param("airType") String airType, @Param("now") Date now, Pageable pageable);
	//
	// @Query("select t from #{#entityName} t where t.endDate >= :now and t.vendor = :te and t.airType like %:airType%
	// order by t.startDate desc")
	// Page<Course> findTenantValidCourses2(@Param("te") Tenant tenant, @Param("airType") String airType,
	// @Param("now") Date now, Pageable pageable);

}
