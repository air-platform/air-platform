package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
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

import io.swagger.annotations.Api;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.EnrollmentService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Created by guankai on 13/04/2017. (TODO useful?)
 */
@RESTful
@PermitAll
@Path("enrollments")
@Api("enrollments")
public class EnrollmentResource {
	private static final Logger LOG = LoggerFactory.getLogger(EnrollmentResource.class);

	@Resource
	private EnrollmentService enrollmentService;

	/**
	 * List
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		LOG.debug("list all enrollments...");
		Page<Enrollment> result = enrollmentService.listEnrollments(page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{enrollmentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Enrollment find(@PathParam("enrollmentId") String enrollmentId) {
		return enrollmentService.findEnrollment(enrollmentId);
	}

}
