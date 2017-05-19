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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.service.SchoolService;

/**
 * School RESTful API allows list/find/query for ANYONE
 * 
 * Created by guankai on 12/04/2017.
 */
@Api
@RESTful
@PermitAll
@Path("schools")
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
	public Page<School> listAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		LOG.debug("List all schools");
		return schoolService.listSchools(page, pageSize);
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
