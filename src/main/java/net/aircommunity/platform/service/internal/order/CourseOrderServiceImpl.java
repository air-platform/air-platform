package net.aircommunity.platform.service.internal.order;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.repository.CourseOrderRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CourseOrderService;

/**
 * CourseOrder service implementation
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class CourseOrderServiceImpl extends AbstractVendorAwareOrderService<CourseOrder> implements CourseOrderService {

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.course-courseorder";

	@Resource
	private CourseOrderRepository courseOrderRepository;

	@Override
	protected void copyProperties(CourseOrder src, CourseOrder tgt) {
		tgt.setAircraftType(src.getAircraftType());
		tgt.setLicense(src.getLicense());
		tgt.setLocation(src.getLocation());
	}

	@Override
	public Page<CourseOrder> listCourseOrdersByCourse(String courseId, int page, int pageSize) {
		return Pages.adapt(courseOrderRepository.findByCourseIdOrderByCreationDateDesc(courseId,
				Pages.createPageRequest(page, pageSize)));
	}

	// less important (for TENANT)
	@Override
	public Page<CourseOrder> listCourseOrdersOfCourse(String courseId, Order.Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(courseOrderRepository.findByCourseIdAndStatusInOrderByCreationDateDesc(courseId,
					Order.Status.VISIBLE_STATUSES, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(courseOrderRepository.findByCourseIdAndStatusOrderByCreationDateDesc(courseId, status,
				Pages.createPageRequest(page, pageSize))); // OK
	}

	// @Override
	// public CourseOrder createCourseOrder(String userId, CourseOrder courseOrder) {
	// return doCreateOrder(userId, courseOrder);
	// }
	//
	// @Cacheable(cacheNames = CACHE_NAME)
	// @Override
	// public CourseOrder findCourseOrder(String courseOrderId) {
	// return doFindOrder(courseOrderId);
	// }
	//
	// @CachePut(cacheNames = CACHE_NAME, key = "#courseOrderId")
	// @Override
	// public CourseOrder updateCourseOrderStatus(String courseOrderId, Order.Status status) {
	// return doUpdateOrderStatus(courseOrderId, status);
	// }
	//
	// @Override
	// public Page<CourseOrder> listCourseOrders(Order.Status status, int page, int pageSize) {
	// return doListAllOrders(status, page, pageSize); // OK, full table scan for ADMIN
	// }

	// @Override
	// public Page<CourseOrder> listUserCourseOrders(String userId, Order.Status status, int page, int pageSize) {
	// return doListAllUserOrders(userId, status, page, pageSize); // OK
	// }
	//
	// @Override
	// public Page<CourseOrder> listTenantCourseOrders(String tenantId, Order.Status status, int page, int pageSize) {
	// return doListTenantOrders(tenantId, status, page, pageSize); // OK
	// }

	// @CacheEvict(cacheNames = CACHE_NAME, key = "#courseOrderId")
	// @Override
	// public void deleteCourseOrder(String courseOrderId) {
	// doDeleteOrder(courseOrderId);
	// }

	@Override
	protected Code orderNotFoundCode() {
		return Codes.COURSEORDER_NOT_FOUND;
	}

	@Override
	protected VendorAwareOrderRepository<CourseOrder> getOrderRepository() {
		return courseOrderRepository;
	}

}
