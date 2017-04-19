package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.service.SchoolService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * School RESTful API allows list/find/query for ANYONE
 * 
 * Created by guankai on 12/04/2017.
 */
@RESTful
@PermitAll
@Path("schools")
@Api("schools")
public class SchoolResource {
	private static final Logger LOG = LoggerFactory.getLogger(SchoolResource.class);

	@Resource
	private SchoolService schoolService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		LOG.debug("List all schools");
		Page<School> result = schoolService.listSchools(page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@PermitAll
	@Path("{schoolId}")
	@Produces(MediaType.APPLICATION_JSON)
	public School find(@PathParam("schoolId") String schoolId) {
		return schoolService.findSchool(schoolId);
	}
}
