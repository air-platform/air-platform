package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.EnrollmentRepository;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.platform.service.EnrollmentService;

/**
 * Created by guankai on 13/04/2017.
 */
@Service
@Transactional
public class EnrollmentServiceImpl extends AbstractVendorAwareOrderService<Enrollment> implements EnrollmentService {
	private static final String CACHE_NAME = "cache.course-enrollment";

	@Resource
	private EnrollmentRepository enrollmentRepository;

	@Resource
	private CourseService courseService;

	@Resource
	private SchoolRepository schoolRepository;

	@Override
	public Enrollment createEnrollment(String userId, Enrollment enrollment) {
		return doCreateOrder(userId, enrollment);
	}

	@Override
	protected void copyProperties(Enrollment src, Enrollment tgt) {
		tgt.setAircraftType(src.getAircraftType());
		tgt.setContact(src.getContact());
		tgt.setLicense(src.getLicense());
		tgt.setLocation(src.getLocation());
		tgt.setNote(src.getNote());
		//
		Course course = courseService.findCourse(src.getCourse().getId());
		tgt.setCourse(course);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Enrollment findEnrollment(String enrollmentId) {
		return doFindOrder(enrollmentId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#enrollmentId")
	@Override
	public Enrollment updateEnrollmentStatus(String enrollmentId, Status status) {
		return doUpdateOrderStatus(enrollmentId, status);
	}

	@Override
	public Page<Enrollment> listEnrollments(Order.Status status, int page, int pageSize) {
		return doListAllOrders(status, page, pageSize);
	}

	@Override
	public Page<Enrollment> listEnrollmentsByCourse(String courseId, int page, int pageSize) {
		return Pages.adapt(enrollmentRepository.findByCourseIdOrderByCreationDateDesc(courseId,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Enrollment> listUserEnrollments(String userId, Order.Status status, int page, int pageSize) {
		return doListAllUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<Enrollment> listEnrollmentsForTenant(String tenantId, Order.Status status, int page, int pageSize) {
		return doListTenantOrders(tenantId, status, page, pageSize);
	}

	@Override
	public Page<Enrollment> listEnrollmentsForTenantByCourse(String tenantId, String courseId, Order.Status status,
			int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(enrollmentRepository.findByVendorIdAndCourseIdAndStatusNotOrderByCreationDateDesc(
					tenantId, courseId, Order.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(enrollmentRepository.findByVendorIdAndCourseIdAndStatusOrderByCreationDateDesc(tenantId,
				courseId, status, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#enrollmentId")
	@Override
	public void deleteEnrollment(String enrollmentId) {
		doDeleteOrder(enrollmentId);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.ENROLLMENT_NOT_FOUND;
	}

	@Override
	protected VendorAwareOrderRepository<Enrollment> getOrderRepository() {
		return enrollmentRepository;
	}

}
