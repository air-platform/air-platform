package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Order.Status;

/**
 * Created by guankai on 12/04/2017.
 */
public interface EnrollmentRepository extends VendorAwareOrderRepository<Enrollment> {

	// Tenant
	Page<Enrollment> findByCourseVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	Page<Enrollment> findByCourseSchoolIdOrderByCreationDateDesc(String schoolId, Pageable pageable);

	Page<Enrollment> findByCourseIdOrderByCreationDateDesc(String courseId, Pageable pageable);

	Page<Enrollment> findByVendorIdAndCourseIdAndStatusNotOrderByCreationDateDesc(String tenantId, String courseId,
			Status ignoredStatus, Pageable pageable);

	Page<Enrollment> findByVendorIdAndCourseIdAndStatusOrderByCreationDateDesc(String tenantId, String courseId,
			Status status, Pageable pageable);

	// All
	// Page<Enrollment> findAllByOrderByCreationDateDesc(Pageable pageable);

	// User
	// Page<Enrollment> findByOwnerIdOrderByCreationDateDesc(String userId, Pageable pageable);

	// @Query("select t from Enrollment t where t.course.vendor = :te")
	// Page<Enrollment> findByTenant(@Param("te") Tenant tenant, Pageable pageable);

	// @Query("select t from Enrollment t where t.course.school = :sc")
	// Page<Enrollment> findBySchool(@Param("sc") School school, Pageable pageable);

}
