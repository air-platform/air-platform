package net.aircommunity.platform.rest.user;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
 * User {@code Course} Enrollment of a {@code School}
 * 
 * Created by guankai on 13/04/2017.
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class UserEnrollmentResource extends BaseOrderResource<Enrollment> {
	private static final Logger LOG = LoggerFactory.getLogger(UserEnrollmentResource.class);

	@Resource
	private EnrollmentService enrollmentService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("userId") String userId, @NotNull @Valid Enrollment request,
			@Context UriInfo uriInfo) {
		LOG.debug("create enrollment {}", request);
		Enrollment created = enrollmentService.createEnrollment(userId, request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Enrollment> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		return enrollmentService.listUserEnrollments(userId, status, page, pageSize);
	}
}
