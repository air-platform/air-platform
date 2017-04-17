package net.aircommunity.platform.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
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

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Created by guankai on 13/04/2017.
 */
@RESTful
@PermitAll
@Path("courses")
public class CourseResource {
	private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

	@Resource
	private CourseService courseService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List by air type
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response list(@QueryParam("airType") String airType, @QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("page") @DefaultValue("10") int pageSize) {
		LOG.debug("list all courses with airType: {}", airType);
		Page<Course> courses = courseService.listCoursesByAirType(airType, page, pageSize);
		return Response.ok(courses).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(courses)).build();
	}

	/**
	 * Top10 Hot courses
	 */
	@GET
	@Path("hot")
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response top10Hot(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("page") @DefaultValue("10") int pageSize) {
		List<Course> courses = courseService.listTop10HotCourses();
		return Response.ok(courses).build();
	}

	/**
	 * List courses of a school
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("school")
	@PermitAll
	public Response listBySchool(@NotNull @QueryParam("id") String schoolId,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("page") @DefaultValue("10") int pageSize) {
		Page<Course> coursePage = courseService.listCoursesBySchool(schoolId, page, pageSize);
		return Response.ok(coursePage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(coursePage))
				.build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{courseId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Course find(@PathParam("courseId") String courseId) {
		return courseService.findCourse(courseId);
	}

}