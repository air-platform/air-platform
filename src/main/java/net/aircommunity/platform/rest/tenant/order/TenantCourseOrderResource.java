package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Tenant can view the {@code Course} CourseOrder of a {@code User}
 * 
 * @author guankai
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantCourseOrderResource extends TenantBaseOrderResource<CourseOrder> {
	private static final Logger LOG = LoggerFactory.getLogger(TenantCourseOrderResource.class);

	@Resource
	private CourseOrderService courseOrderService;

	/**
	 * List CourseOrders of a course or all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<CourseOrder> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
			@QueryParam("course") String courseId, @QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		LOG.debug("List all CourseOrders tenantId: {}, course: {}", tenantId, courseId);
		if (Strings.isNotBlank(courseId)) {
			// TODO just check the courseId belongs to this tenant
			return courseOrderService.listCourseOrdersOfCourse(courseId, status, page, pageSize);
		}
		return courseOrderService.listCourseOrdersForTenant(tenantId, status, page, pageSize);
	}

}
