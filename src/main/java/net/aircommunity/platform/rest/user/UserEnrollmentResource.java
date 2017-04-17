package net.aircommunity.platform.rest.user;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.EnrollmentService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * User {@code Course} Enrollment of a {@code School}
 * 
 * Created by guankai on 13/04/2017.
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class UserEnrollmentResource {
	private static final Logger LOG = LoggerFactory.getLogger(UserEnrollmentResource.class);

	@Resource
	private EnrollmentService enrollmentService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @QueryParam("course") String courseId,
			@NotNull @Valid Enrollment request, @Context UriInfo uriInfo) {
		LOG.debug("create enrollment {}", request);
		Enrollment created = enrollmentService.createEnrollment(userId, courseId, request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

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
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("userId") String userId, @QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		Page<Enrollment> result = enrollmentService.listUserEnrollments(userId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	@POST
	@Path("{enrollmentId}/cancel")
	public Response cancelEnrollment(@PathParam("enrollmentId") String enrollmentId) {
		enrollmentService.cancelEnrollment(enrollmentId);
		return Response.noContent().build();
	}

	// ADMIN ONLY

	/**
	 * Delete
	 */
	@DELETE
	@Path("{enrollmentId}/force")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response forceDelete(@PathParam("enrollmentId") String enrollmentId) {
		enrollmentService.deleteEnrollment(enrollmentId);
		return Response.noContent().build();
	}

}
