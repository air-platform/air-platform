package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.CourseOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * Tenant can view the {@code Course} CourseOrder of a {@code User} for ADMIN and TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantCourseOrderResource extends TenantOrderResourceSupport<CourseOrder> {

	@Resource
	private CourseOrderService courseOrderService;

	@Override
	protected OrderService<CourseOrder> getOrderService() {
		return courseOrderService;
	}

	/**
	 * List (customized query)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<CourseOrder> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
			@QueryParam("course") String courseId, @QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		LOG.debug("List all course orders tenantId: {}, course: {}", tenantId, courseId);
		if (Strings.isNotBlank(courseId)) {
			// TODO just check the courseId belongs to this tenant
			return courseOrderService.listCourseOrdersOfCourse(courseId, status, page, pageSize);
		}
		return courseOrderService.listTenantCourseOrders(tenantId, status, page, pageSize);
	}

}
