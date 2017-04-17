package net.aircommunity.platform.service.internal;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.EnrollmentRepository;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.platform.service.EnrollmentService;

/**
 * Created by guankai on 13/04/2017.
 */
@Service
@Transactional
public class EnrollmentServiceImpl extends AbstractServiceSupport implements EnrollmentService {
	private static final String CACHE_NAME = "cache.course-enrollment";

	@Resource
	private EnrollmentRepository enrollmentRepository;

	@Resource
	private CourseService courseService;

	@Resource
	private SchoolRepository schoolRepository;

	@Override
	public Enrollment createEnrollment(String userId, String courseId, Enrollment enrollment) {
		User user = findAccount(userId, User.class);
		Course course = courseService.findCourse(courseId);

		Enrollment newEnrollment = new Enrollment();
		copyProperties(enrollment, newEnrollment);
		newEnrollment.setCreationDate(new Date());
		newEnrollment.setCommented(false);
		newEnrollment.setStatus(Order.Status.PENDING);
		//
		newEnrollment.setOwner(user);
		newEnrollment.setCourse(course);
		return enrollmentRepository.save(newEnrollment);
	}

	private void copyProperties(Enrollment src, Enrollment tgt) {
		tgt.setAirType(src.getAirType());
		tgt.setIdentity(src.getIdentity());
		tgt.setLicense(src.getLicense());
		tgt.setLocation(src.getLocation());
		tgt.setNote(src.getNote());
		tgt.setPerson(src.getPerson());
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Enrollment findEnrollment(String enrollmentId) {
		Enrollment enrollment = enrollmentRepository.findOne(enrollmentId);
		if (enrollment == null) {
			throw new AirException(Codes.ENROLLMENT_NOT_FOUND, String.format("Enrollment %s not found", enrollmentId));
		}
		return enrollment;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#enrollmentId")
	@Override
	public Enrollment cancelEnrollment(String enrollmentId) {
		Enrollment enrollment = findEnrollment(enrollmentId);
		enrollment.setStatus(Order.Status.CANCELLED);
		return enrollmentRepository.save(enrollment);
	}

	@Override
	public Page<Enrollment> listEnrollments(int page, int pageSize) {
		return Pages
				.adapt(enrollmentRepository.findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Enrollment> listUserEnrollments(String userId, int page, int pageSize) {
		return Pages.adapt(enrollmentRepository.findByOwnerIdOrderByCreationDateDesc(userId,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Enrollment> listEnrollmentsForTenant(String tenantId, int page, int pageSize) {
		return Pages.adapt(enrollmentRepository.findByCourseVendorIdOrderByCreationDateDesc(tenantId,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Enrollment> listEnrollmentsByCourse(String courseId, int page, int pageSize) {
		return Pages.adapt(enrollmentRepository.findByCourseIdOrderByCreationDateDesc(courseId,
				Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#enrollmentId")
	@Override
	public void deleteEnrollment(String enrollmentId) {
		enrollmentRepository.delete(enrollmentId);
	}

}
