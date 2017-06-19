package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.model.domain.Order.Status;

/**
 * Repository interface for {@link CourseOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CourseOrderRepository extends VendorAwareOrderRepository<CourseOrder> {

	// Tenant
	Page<CourseOrder> findByCourseVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	Page<CourseOrder> findByCourseSchoolIdOrderByCreationDateDesc(String schoolId, Pageable pageable);

	Page<CourseOrder> findByCourseIdOrderByCreationDateDesc(String courseId, Pageable pageable);

	Page<CourseOrder> findByVendorIdAndCourseIdAndStatusNotOrderByCreationDateDesc(String tenantId, String courseId,
			Status ignoredStatus, Pageable pageable);

	Page<CourseOrder> findByVendorIdAndCourseIdAndStatusOrderByCreationDateDesc(String tenantId, String courseId,
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
