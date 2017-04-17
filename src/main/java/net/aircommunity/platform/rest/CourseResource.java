package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Created by guankai on 13/04/2017.
 */
@RESTful
@Path("courses")
public class CourseResource {

	private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);
	@Resource
	private CourseService courseService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// @RolesAllowed(Roles.ROLE_ADMIN)
	@PermitAll
	public Response getAllCourses(@QueryParam("airType") String airType,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("page") @DefaultValue("10") int pageSize) {
		LOG.debug("get all courses start...");
		Page<Course> courses;
		if (airType == null) {
			courses = courseService.getAllCourses(page, pageSize);
		}
		else {
			courses = courseService.getCourseByAirType(airType, page, pageSize);
		}
		return Response.ok(courses).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(courses)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("hot")
	@PermitAll
	public Response getHotCourses(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("page") @DefaultValue("10") int pageSize) {
		LOG.debug("get hot courses start...");
		Page<Course> coursePages = courseService.getHotCourses(page, pageSize);
		return Response.ok(coursePages).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(coursePages))
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("user/school/{schoolId}")
	@PermitAll
	public Response getCourseBySchoolTimelimit(@PathParam("schoolId") String schoolId,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("page") @DefaultValue("10") int pageSize) {
		LOG.debug("user get courses by school..");
		Page<Course> coursePage = courseService.getCourseBySchool(schoolId, page, pageSize);
		return Response.ok(coursePage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(coursePage))
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/school/{schoolId}")
	@RolesAllowed(Roles.ROLE_TENANT)
	public Response getCourseBySchool(@PathParam("schoolId") String schoolId,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("page") @DefaultValue("10") int pageSize) {
		LOG.debug("get courses by school..");
		Page<Course> coursePage = courseService.getAllCourseBySchool(schoolId, page, pageSize);
		return Response.ok(coursePage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(coursePage))
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/tenant")
	@RolesAllowed(Roles.ROLE_TENANT)
	public Response getCourseByTenant(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("page") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		LOG.debug("get courses by tenant..");
		String tenantId = context.getUserPrincipal().getName();
		Page<Course> coursePage = courseService.getAllCourseByTenant(tenantId, page, pageSize);
		return Response.ok(coursePage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(coursePage))
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("create/{schoolId}")
	@RolesAllowed(Roles.ROLE_TENANT)
	public Response createCourse(@NotNull Course request, @PathParam("schoolId") String schoolId,
			@Context SecurityContext context, @Context UriInfo uriInfo) {
		LOG.debug("create course...");
		String tenantId = context.getUserPrincipal().getName();
		Course course = courseService.createCourse(request, schoolId, tenantId);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(course.getId()).build();
		LOG.debug("Created course : {}", uri);
		return Response.created(uri).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("update/{schoolId}")
	@RolesAllowed(Roles.ROLE_TENANT)
	public Response updateCourse(@NotNull Course request, @PathParam("schoolId") String schoolId) {
		LOG.debug("update course....");
		Course course = courseService.updateCourse(request, schoolId);
		return Response.noContent().build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{courseId}")
	@PermitAll
	public Response getCourseById(@PathParam("courseId") String courseId) {
		LOG.debug("get course by Id" + courseId);
		Course course = courseService.getCourseById(courseId);
		return Response.ok(course).build();
	}

}