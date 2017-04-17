package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Enrollment;

/**
 * Created by guankai on 12/04/2017.
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

	// All
	Page<Enrollment> findAllByOrderByCreationDateDesc(Pageable pageable);

	// Tenant
	Page<Enrollment> findByCourseVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	// User
	Page<Enrollment> findByOwnerIdOrderByCreationDateDesc(String userId, Pageable pageable);

	Page<Enrollment> findByCourseSchoolIdOrderByCreationDateDesc(String schoolId, Pageable pageable);

	Page<Enrollment> findByCourseIdOrderByCreationDateDesc(String courseId, Pageable pageable);

	// @Query("select t from Enrollment t where t.course.vendor = :te")
	// Page<Enrollment> findByTenant(@Param("te") Tenant tenant, Pageable pageable);

	// @Query("select t from Enrollment t where t.course.school = :sc")
	// Page<Enrollment> findBySchool(@Param("sc") School school, Pageable pageable);

}
