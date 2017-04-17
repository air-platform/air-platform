package net.aircommunity.platform.service;

import net.aircommunity.platform.model.*;

import javax.annotation.Nonnull;

/**
 * Created by guankai on 13/04/2017.
 */
public interface EnrollmentService {

	@Nonnull
	Enrollment createEnrollment(@Nonnull String userId, @Nonnull String courseId, @Nonnull Enrollment enrollment);

	@Nonnull
	Enrollment findEnrollment(@Nonnull String enrollmentId);

	@Nonnull
	Enrollment cancelEnrollment(@Nonnull String enrollmentId);

	@Nonnull
	Page<Enrollment> listEnrollments(int page, int pageSize);

	@Nonnull
	Page<Enrollment> listUserEnrollments(@Nonnull String userId, int page, int pageSize);

	@Nonnull
	Page<Enrollment> listEnrollmentsForTenant(@Nonnull String tenantId, int page, int pageSize);

	@Nonnull
	Page<Enrollment> listEnrollmentsByCourse(@Nonnull String courseId, int page, int pageSize);

	void deleteEnrollment(@Nonnull String enrollmentId);

}
