package net.aircommunity.platform.rest.tenant;

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
import javax.ws.rs.PUT;
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

import io.micro.annotation.RESTful;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.CourseService;

/**
 * Course RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * Created by guankai on 13/04/2017.
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantCourseResource {
	private static final Logger LOG = LoggerFactory.getLogger(TenantCourseResource.class);

	@Resource
	private CourseService courseService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@NotNull @Valid Course request, @Context UriInfo uriInfo) {
		Course created = courseService.createCourse(request.getSchool().getId(), request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{courseId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Course find(@PathParam("courseId") String courseId) {
		return courseService.findCourse(courseId);
	}

	/**
	 * List TODO more query?
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("tenantId") String tenantId, @QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		Page<Course> coursePage = courseService.listCourses(tenantId, page, pageSize);
		return Response.ok(coursePage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(coursePage))
				.build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{courseId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Course updateCourse(@PathParam("courseId") String courseId, @NotNull @Valid Course request) {
		return courseService.updateCourse(courseId, request);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{courseId}")
	public Response delete(@PathParam("courseId") String courseId) {
		courseService.deleteCourse(courseId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("schoolId") String schoolId) {
		courseService.deleteCourses(schoolId);
		return Response.noContent().build();
	}

}