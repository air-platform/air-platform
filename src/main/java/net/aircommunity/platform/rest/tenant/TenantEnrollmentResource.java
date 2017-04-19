package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
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
public class TenantEnrollmentResource {
	private static final Logger LOG = LoggerFactory.getLogger(TenantEnrollmentResource.class);

	@Resource
	private EnrollmentService enrollmentService;

	/**
	 * Find
	 */
	@GET
	@Path("{enrollmentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Enrollment find(@PathParam("enrollmentId") String enrollmentId) {
		return enrollmentService.findEnrollment(enrollmentId);
	}

	/**
	 * List enrollments of a course or all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("tenantId") String tenantId, @QueryParam("course") String courseId,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		LOG.debug("List all enrollments tenantId: {}, course: {}", tenantId, courseId);
		Page<Enrollment> result = Page.emptyPage(page, pageSize);
		if (courseId == null) {
			result = enrollmentService.listEnrollmentsByCourse(courseId, page, pageSize);
		}
		else {
			enrollmentService.listEnrollmentsForTenant(tenantId, page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

}
