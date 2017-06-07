package net.aircommunity.platform.rest.admin.product;

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

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.service.SchoolService;

/**
 * School RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminSchoolResource {
	private static final Logger LOG = LoggerFactory.getLogger(AdminSchoolResource.class);

	@Resource
	private SchoolService schoolService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid School request,
			@Context UriInfo uriInfo) {
		School created = schoolService.createSchool(tenantId, request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{schoolId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public School find(@PathParam("schoolId") String schoolId) {
		return schoolService.findSchool(schoolId);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<School> list(@QueryParam("tenant") String tenantId, @QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		if (Strings.isBlank(tenantId)) {
			return schoolService.listAllSchools(page, pageSize);
		}
		return schoolService.listSchools(tenantId, page, pageSize);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{schoolId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public School update(@PathParam("schoolId") String schoolId, @NotNull @Valid School school) {
		return schoolService.updateSchool(schoolId, school);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{schoolId}")
	public void delete(@PathParam("schoolId") String schoolId) {
		schoolService.deleteSchool(schoolId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteAll(@QueryParam("tenant") String tenantId) {
		schoolService.deleteSchools(tenantId);
	}

}
