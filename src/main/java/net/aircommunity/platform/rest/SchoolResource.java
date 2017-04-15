package net.aircommunity.platform.rest;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.School;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.SchoolService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Created by guankai on 12/04/2017. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 */
@RESTful
@Path("schools")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
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
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response listAll(@QueryParam("page") @DefaultValue("1") int page,
			@QueryParam("pageSize") @DefaultValue("10") int pageSize, @Context SecurityContext context) {
		Page<School> result = Page.emptyPage(page, pageSize);
		// redirect to tenant owned
		if (context.isUserInRole(Role.TENANT.name())) {
			result = schoolService.listSchools(context.getUserPrincipal().getName(), page, pageSize);
		}
		else {
			result = schoolService.listSchools(page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{schoolId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public School find(@PathParam("schoolId") String schoolId) {
		return schoolService.findSchool(schoolId);
	}

	// TODO query by xxx etc.

	// ***********************
	// ADMIN/TENANT
	// ***********************

	/**
	 * List all for tenant
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAlForTenant(@PathParam("tenantId") String tenantId,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		Page<School> schoolPage = schoolService.listSchools(tenantId, page, pageSize);
		return Response.ok(schoolPage).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(schoolPage))
				.build();
	}

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid School request,
			@Context SecurityContext context, @Context UriInfo uriInfo) {
		School created = schoolService.createSchool(tenantId, request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created school : {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{schoolId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public School update(@PathParam("schoolId") String schoolId, @NotNull @Valid School school) {
		return schoolService.updateSchool(schoolId, school);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{schoolId}")
	public Response delete(@PathParam("schoolId") String schoolId) {
		schoolService.deleteSchool(schoolId);
		return Response.noContent().build();
	}

}
