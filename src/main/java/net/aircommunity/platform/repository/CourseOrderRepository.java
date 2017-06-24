package net.aircommunity.platform.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.model.domain.Order.Status;

/**
 * TODO index from this and AirTransportRepository
 * 
 * Repository interface for {@link CourseOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CourseOrderRepository extends VendorAwareOrderRepository<CourseOrder> {

	// ADMIN (Using index condition; Using where; Using filesort)
	Page<CourseOrder> findByCourseIdOrderByCreationDateDesc(String courseId, Pageable pageable);

	// Tenant (Using index condition; Using where; Using filesort)
	Page<CourseOrder> findByCourseVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	// will need join school table
	Page<CourseOrder> findByCourseSchoolIdOrderByCreationDateDesc(String schoolId, Pageable pageable);

	// find all orders of statuses of the given course
	Page<CourseOrder> findByCourseIdAndStatusInOrderByCreationDateDesc(String courseId, Collection<Status> statuses,
			Pageable pageable);

	Page<CourseOrder> findByCourseIdAndStatusOrderByCreationDateDesc(String courseId, Status status, Pageable pageable);

}
