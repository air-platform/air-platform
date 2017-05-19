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

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.BaseOrderResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.EnrollmentService;

/**
 * Tenant can view the {@code Course} Enrollment of a {@code User}
 * 
 * Created by guankai on 13/04/2017.
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantEnrollmentResource extends BaseOrderResource<Enrollment> {
	private static final Logger LOG = LoggerFactory.getLogger(TenantEnrollmentResource.class);

	@Resource
	private EnrollmentService enrollmentService;

	/**
	 * List enrollments of a course or all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Enrollment> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
			@QueryParam("course") String courseId, @QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		LOG.debug("List all enrollments tenantId: {}, course: {}", tenantId, courseId);
		if (courseId != null) {
			return enrollmentService.listEnrollmentsForTenantByCourse(tenantId, courseId, status, page, pageSize);
		}
		return enrollmentService.listEnrollmentsForTenant(tenantId, status, page, pageSize);
	}

}
