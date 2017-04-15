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
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.FleetService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Fleet RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class FleetResource {
	private static final Logger LOG = LoggerFactory.getLogger(FleetResource.class);

	@Resource
	private FleetService fleetService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all Fleets
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize, @Context SecurityContext context) {
		Page<Fleet> result = Page.emptyPage(page, pageSize);
		// redirect to tenant owned
		if (context.isUserInRole(Role.TENANT.name())) {
			result = fleetService.listFleets(context.getUserPrincipal().getName(), page, pageSize);
		}
		else {
			result = fleetService.listFleets(page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Get a Fleet
	 */
	@GET
	@Path("{fleetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Fleet find(@PathParam("fleetId") String fleetId) {
		return fleetService.findFleet(fleetId);
	}

	// TODO query by flightNo, status etc.

	// ***********************
	// ADMIN/TENANT
	// ***********************

	/**
	 * List all Fleets of an tenant
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllForTenant(@PathParam("tenantId") String tenantId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<Fleet> result = fleetService.listFleets(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Create a Fleet
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid Fleet fleet,
			@Context UriInfo uriInfo) {
		Fleet created = fleetService.createFleet(tenantId, fleet);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update a Fleet
	 */
	@PUT
	@Path("{fleetId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Fleet update(@PathParam("fleetId") String fleetId, @NotNull @Valid Fleet newFleet) {
		return fleetService.updateFleet(fleetId, newFleet);
	}

	/**
	 * Delete a Fleet
	 */
	@DELETE
	@Path("{fleetId}")
	public Response delete(@PathParam("fleetId") String fleetId) {
		fleetService.deleteFleet(fleetId);
		return Response.noContent().build();
	}

	/**
	 * Delete all Fleets for a tenant
	 */
	@DELETE
	public Response deleteAll(@PathParam("tenantId") String tenantId) {
		fleetService.deleteFleets(tenantId);
		return Response.noContent().build();
	}

}
