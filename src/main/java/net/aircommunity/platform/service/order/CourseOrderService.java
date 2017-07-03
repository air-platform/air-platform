package net.aircommunity.platform.service.order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.model.domain.Order;

/**
 * Course order service.
 * 
 * @author guankai
 */
public interface CourseOrderService extends StandardOrderService<CourseOrder> {

	@Nonnull
	default CourseOrder createCourseOrder(@Nonnull String userId, @Nonnull CourseOrder courseOrder) {
		return createOrder(userId, courseOrder);
	}

	@Nonnull
	default CourseOrder findCourseOrder(@Nonnull String courseOrderId) {
		return findCourseOrder(courseOrderId);
	}

	@Nonnull
	default CourseOrder updateCourseOrderStatus(@Nonnull String courseOrderId, @Nonnull Order.Status status) {
		return updateCourseOrderStatus(courseOrderId, status);
	}

	// USER
	@Nonnull
	default Page<CourseOrder> listUserCourseOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	// TENANT
	@Nonnull
	default Page<CourseOrder> listTenantCourseOrders(@Nonnull String tenantId, @Nullable Order.Status status, int page,
			int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	// ADMIN
	@Nonnull
	default Page<CourseOrder> listCourseOrders(Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	// ADMIN
	@Nonnull
	Page<CourseOrder> listCourseOrdersByCourse(@Nonnull String courseId, int page, int pageSize);

	// TENANT or ANONYMOUS
	// List all orders placed on the given course (a course should belong to a tenant)
	Page<CourseOrder> listCourseOrdersOfCourse(@Nonnull String courseId, @Nullable Order.Status status, int page,
			int pageSize);

	// ADMIN
	default void deleteCourseOrder(@Nonnull String courseOrderId) {
		deleteOrder(courseOrderId);
	}

	// ADMIN
	default void deleteCourseOrders(@Nonnull String userId) {
		deleteOrders(userId);
	}

}
