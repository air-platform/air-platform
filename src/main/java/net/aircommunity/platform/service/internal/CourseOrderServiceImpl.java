package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.repository.CourseOrderRepository;
import net.aircommunity.platform.repository.SchoolRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.CourseOrderService;
import net.aircommunity.platform.service.CourseService;

/**
 * CourseOrder service implementation
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CourseOrderServiceImpl extends AbstractVendorAwareOrderService<CourseOrder> implements CourseOrderService {

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.course-courseorder";

	@Resource
	private CourseOrderRepository courseOrderRepository;

	@Resource
	private CourseService courseService;

	@Resource
	private SchoolRepository schoolRepository;

	@Override
	public CourseOrder createCourseOrder(String userId, CourseOrder courseOrder) {
		return doCreateOrder(userId, courseOrder);
	}

	@Override
	protected void copyProperties(CourseOrder src, CourseOrder tgt) {
		tgt.setAircraftType(src.getAircraftType());
		tgt.setLicense(src.getLicense());
		tgt.setLocation(src.getLocation());
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public CourseOrder findCourseOrder(String courseOrderId) {
		return doFindOrder(courseOrderId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#courseOrderId")
	@Override
	public CourseOrder updateCourseOrderStatus(String courseOrderId, Order.Status status) {
		return doUpdateOrderStatus(courseOrderId, status);
	}

	@Override
	public Page<CourseOrder> listCourseOrders(Order.Status status, int page, int pageSize) {
		return doListAllOrders(status, page, pageSize);
	}

	@Override
	public Page<CourseOrder> listCourseOrdersByCourse(String courseId, int page, int pageSize) {
		return Pages.adapt(courseOrderRepository.findByCourseIdOrderByCreationDateDesc(courseId,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<CourseOrder> listUserCourseOrders(String userId, Order.Status status, int page, int pageSize) {
		return doListAllUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<CourseOrder> listCourseOrdersForTenant(String tenantId, Order.Status status, int page, int pageSize) {
		return doListTenantOrders(tenantId, status, page, pageSize);
	}

	@Override
	public Page<CourseOrder> listCourseOrdersForTenantByCourse(String tenantId, String courseId, Order.Status status,
			int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(courseOrderRepository.findByVendorIdAndCourseIdAndStatusNotOrderByCreationDateDesc(
					tenantId, courseId, Order.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(courseOrderRepository.findByVendorIdAndCourseIdAndStatusOrderByCreationDateDesc(tenantId,
				courseId, status, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#courseOrderId")
	@Override
	public void deleteCourseOrder(String courseOrderId) {
		doDeleteOrder(courseOrderId);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.COURSEORDER_NOT_FOUND;
	}

	@Override
	protected VendorAwareOrderRepository<CourseOrder> getOrderRepository() {
		return courseOrderRepository;
	}

}
