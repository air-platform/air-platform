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
import net.aircommunity.platform.model.Aircraft;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AircraftService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Aircraft RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("aircrafts")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class AircraftResource {
	private static final Logger LOG = LoggerFactory.getLogger(AircraftResource.class);

	@Resource
	private AircraftService aircraftService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response listAll(@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize, @Context SecurityContext context) {
		Page<Aircraft> result = Page.emptyPage(page, pageSize);
		// redirect to tenant owned
		if (context.isUserInRole(Role.TENANT.name())) {
			result = aircraftService.listAircrafts(context.getUserPrincipal().getName(), page, pageSize);
		}
		else {
			result = aircraftService.listAircrafts(page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{aircraftId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Aircraft find(@PathParam("aircraftId") String aircraftId) {
		return aircraftService.findAircraft(aircraftId);
	}

	// TODO query by departure/arrival/date/timeSlot

	// ***********************
	// ADMIN/TENANT
	// ***********************

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid Aircraft airTransport,
			@Context UriInfo uriInfo) {
		Aircraft created = aircraftService.createAircraft(tenantId, airTransport);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List all of an tenant
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllForTenant(@PathParam("tenantId") String tenantId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<Aircraft> result = aircraftService.listAircrafts(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{aircraftId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Aircraft update(@PathParam("aircraftId") String aircraftId, @NotNull @Valid Aircraft newAircraft) {
		return aircraftService.updateAircraft(aircraftId, newAircraft);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{aircraftId}")
	public Response delete(@PathParam("aircraftId") String aircraftId) {
		aircraftService.deleteAircraft(aircraftId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("tenantId") String tenantId) {
		aircraftService.deleteAircrafts(tenantId);
		return Response.noContent().build();
	}

}
