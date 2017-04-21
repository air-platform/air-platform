package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

/**
 * Created by guankai on 13/04/2017.
 */
public interface EnrollmentService {

	@Nonnull
	Enrollment createEnrollment(@Nonnull String userId, @Nonnull Enrollment enrollment);

	@Nonnull
	Enrollment findEnrollment(@Nonnull String enrollmentId);

	@Nonnull
	Enrollment updateEnrollmentStatus(@Nonnull String enrollmentId, @Nonnull Order.Status status);

	// ADMIN
	@Nonnull
	Page<Enrollment> listEnrollments(Order.Status status, int page, int pageSize);

	// ADMIN
	@Nonnull
	Page<Enrollment> listEnrollmentsByCourse(@Nonnull String courseId, int page, int pageSize);

	// USER
	@Nonnull
	Page<Enrollment> listUserEnrollments(@Nonnull String userId, Order.Status status, int page, int pageSize);

	// TENANT
	@Nonnull
	Page<Enrollment> listEnrollmentsForTenant(@Nonnull String tenantId, Order.Status status, int page, int pageSize);

	// TENANT
	Page<Enrollment> listEnrollmentsForTenantByCourse(String tenantId, String courseId, Order.Status status, int page,
			int pageSize);

	// ADMIN
	void deleteEnrollment(@Nonnull String enrollmentId);

}
