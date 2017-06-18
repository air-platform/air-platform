package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.CourseOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

/**
 * Course order (CourseOrder) Service
 * 
 * @author guankai
 */
public interface CourseOrderService {

	@Nonnull
	CourseOrder createCourseOrder(@Nonnull String userId, @Nonnull CourseOrder courseOrder);

	@Nonnull
	CourseOrder findCourseOrder(@Nonnull String courseOrderId);

	@Nonnull
	CourseOrder updateCourseOrderStatus(@Nonnull String courseOrderId, @Nonnull Order.Status status);

	// ADMIN
	@Nonnull
	Page<CourseOrder> listCourseOrders(Order.Status status, int page, int pageSize);

	// ADMIN
	@Nonnull
	Page<CourseOrder> listCourseOrdersByCourse(@Nonnull String courseId, int page, int pageSize);

	// USER
	@Nonnull
	Page<CourseOrder> listUserCourseOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize);

	// TENANT
	@Nonnull
	Page<CourseOrder> listCourseOrdersForTenant(@Nonnull String tenantId, @Nullable Order.Status status, int page,
			int pageSize);

	// TENANT
	Page<CourseOrder> listCourseOrdersForTenantByCourse(@Nonnull String tenantId, @Nonnull String courseId,
			@Nullable Order.Status status, int page, int pageSize);

	// ADMIN
	void deleteCourseOrder(@Nonnull String courseOrderId);

}
