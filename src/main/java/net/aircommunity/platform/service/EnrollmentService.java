package net.aircommunity.platform.service;

import net.aircommunity.platform.model.*;

import javax.annotation.Nonnull;

/**
 * Created by guankai on 13/04/2017.
 */
public interface EnrollmentService {
    @Nonnull
    Page<Enrollment> getAllEnrollment(int page, int pageSize);

    @Nonnull
    Page<Enrollment> getEnrollmentByUser(String userId, int page, int pageSize);

    @Nonnull
    Page<Enrollment> getEnrollmentByTenant(String tenantId, int page, int pageSize);

    @Nonnull
    Page<Enrollment> getEnrollmentByCourse(String courseId, int page, int pageSize);

    @Nonnull
    Enrollment createEnrollment(Enrollment enrollment, String courseId, String userId);

//    @Nonnull
//    Enrollment updateEnrollment(Enrollment enrollment, String courseId, String userId);

    void deleteEnrollment(String enrollmentId);

    void cancelEnrollment(String enrollmentId);

}
