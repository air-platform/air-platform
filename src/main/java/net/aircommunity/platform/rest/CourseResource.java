package net.aircommunity.platform.rest;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.service.product.CourseService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Course RESTful API for anybody.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("courses")
public class CourseResource extends ProductResourceSupport<Course> {
	private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

	@Resource
	private CourseService courseService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<Course> list(
			// 1) query by school
			@QueryParam("school") String school,
			// 2) query by combined conditions
			@QueryParam("location") String location, @QueryParam("aircraftType") String aircraftType,
			@QueryParam("license") String license,
			// pagination
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("list all courses with school: {}, location: {}, license: {}, aircraftType: {}", school, location,
				license, aircraftType);
		if (Strings.isNotBlank(school)) {
			return courseService.listCoursesBySchool(school, page, pageSize);
		}
		return courseService.listCoursesWithConditions(location, aircraftType, license, page, pageSize);
	}

	/**
	 * Aircraft types
	 */
	@GET
	@Path("aircraft-types")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> listAircraftTypes() {
		return courseService.listAircraftTypes();
	}

	/**
	 * Aircraft licenses
	 */
	@GET
	@Path("aircraft-licenses")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> listAircraftLicenses() {
		return courseService.listAircraftLicenses();
	}

	/**
	 * Locations
	 */
	@GET
	@Path("locations")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> listCourseLocations() {
		return courseService.listCourseLocations();
	}

	/**
	 * Top10 Hot courses
	 */
	@GET
	@Path("hot")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public List<Course> top10Hot() {
		return courseService.listTop10HotCourses();
	}

	/**
	 * List courses of a school
	 * @deprecated use instead {@see #list(String, String, String, String, int, int)}
	 */
	@GET
	@Path("school")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<Course> listBySchool(@NotNull @QueryParam("id") String schoolId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return courseService.listCoursesBySchool(schoolId, page, pageSize);
	}

	@Override
	protected StandardProductService<Course> getProductService() {
		return courseService;
	}
}