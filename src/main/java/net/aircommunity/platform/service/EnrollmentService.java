package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

/**
 * Enrollment Service
 * 
 * @author guankai
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
	Page<Enrollment> listUserEnrollments(@Nonnull String userId, @Nullable Order.Status status, int page, int pageSize);

	// TENANT
	@Nonnull
	Page<Enrollment> listEnrollmentsForTenant(@Nonnull String tenantId, @Nullable Order.Status status, int page,
			int pageSize);

	// TENANT
	Page<Enrollment> listEnrollmentsForTenantByCourse(@Nonnull String tenantId, @Nonnull String courseId,
			@Nullable Order.Status status, int page, int pageSize);

	// ADMIN
	void deleteEnrollment(@Nonnull String enrollmentId);

}
