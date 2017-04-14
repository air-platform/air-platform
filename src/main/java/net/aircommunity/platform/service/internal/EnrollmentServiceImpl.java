package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.*;
import net.aircommunity.platform.repository.CourseRepository;
import net.aircommunity.platform.repository.EnrollmentRepository;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.service.EnrollmentService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by guankai on 13/04/2017.
 */
@Service
@Transactional
public class EnrollmentServiceImpl extends AbstractServiceSupport implements EnrollmentService {

    @Resource
    private EnrollmentRepository enrollmentRepository;
    @Resource
    private CourseRepository courseRepository;
    @Resource
    private SchoolRepository schoolRepository;

    @Nonnull
    @Override
    public Page<Enrollment> getAllEnrollment(int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "creationDate");
        return Pages.adapt(enrollmentRepository.findAll(Pages.createPageRequest(page, pageSize, sort)));
    }

    @Nonnull
    @Override
    public Page<Enrollment> getEnrollmentByUser(String userId, int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "creationDate");
        return Pages.adapt(enrollmentRepository.findByOwnerId(userId, Pages.createPageRequest(page, pageSize, sort)));
    }

    @Nonnull
    @Override
    public Page<Enrollment> getEnrollmentByTenant(String tenantId, int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "creationDate");
        Tenant tenant = findAccount(tenantId, Tenant.class);
        return Pages.adapt(enrollmentRepository.findByTenant(tenant, Pages.createPageRequest(page, pageSize, sort)));
    }

    @Nonnull
    @Override
    public Page<Enrollment> getEnrollmentByCourse(String courseId, int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "creationDate");
        Course course = courseRepository.findOne(courseId);
        if (course == null) {
            throw new AirException(Codes.COURSE_NOT_FOUND, String.format("course %s not found", courseId));
        }
        return Pages.adapt(enrollmentRepository.findByCourse(course, Pages.createPageRequest(page, pageSize, sort)));
    }

    @Nonnull
    @Override
    public Enrollment createEnrollment(Enrollment enrollment, String courseId, String userId) {
        User user = findAccount(userId, User.class);
        Course course = courseRepository.findOne(courseId);
        if (course == null) {
            throw new AirException(Codes.COURSE_NOT_FOUND, String.format("course %s not found", courseId));
        }
        Date now = new Date();
        enrollment.setCreationDate(now);
        enrollment.setStatus(Order.Status.PUBLISHED);
        enrollment.setOwner(user);
        enrollment.setCourse(course);
        Enrollment enrollCreated = enrollmentRepository.save(enrollment);
        return enrollCreated;
    }

//    @Nonnull
//    @Override
//    public Enrollment updateEnrollment(Enrollment enrollment, String courseId, String userId) {
//        User user = findAccount(userId, User.class);
//        Course course = courseRepository.findOne(courseId);
//        if (course == null) {
//            throw new AirException(Codes.COURSE_NOT_FOUND, String.format("course %s not found", courseId));
//        }
//        enrollment.setOwner(user);
//        enrollment.setCourse(course);
//        Enrollment enrollUpdated = enrollmentRepository.save(enrollment);
//        return enrollUpdated;
//    }

    @Override
    public void deleteEnrollment(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findOne(enrollmentId);
        if (enrollment != null) {
            enrollmentRepository.delete(enrollment);
        }
    }

    @Override
    public void cancelEnrollment(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findOne(enrollmentId);
        if (enrollment == null) {
            throw new AirException(Codes.ENROLLMENT_NOT_FOUND, String.format("enrollment %s not found", enrollmentId));
        }
        enrollment.setStatus(Order.Status.CANCELLED);
        enrollmentRepository.save(enrollment);
    }
}
