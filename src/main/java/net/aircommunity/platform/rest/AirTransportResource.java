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
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTransportService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * AirTransport RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("transports")
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class AirTransportResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirTransportResource.class);

	@Resource
	private AirTransportService airTransportService;

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
		Page<AirTransport> result = Page.emptyPage(page, pageSize);
		// redirect to tenant owned
		if (context.isUserInRole(Role.TENANT.name())) {
			result = airTransportService.listAirTransports(context.getUserPrincipal().getName(), page, pageSize);
		}
		else {
			result = airTransportService.listAirTransports(page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{transportId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public AirTransport find(@PathParam("transportId") String transportId) {
		return airTransportService.findAirTransport(transportId);
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
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTransport airTransport,
			@Context UriInfo uriInfo) {
		AirTransport created = airTransportService.createAirTransport(tenantId, airTransport);
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
		Page<AirTransport> result = airTransportService.listAirTransports(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{transportId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTransport update(@PathParam("transportId") String transportId,
			@NotNull @Valid AirTransport newAirTransport) {
		return airTransportService.updateAirTransport(transportId, newAirTransport);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{transportId}")
	public Response delete(@PathParam("transportId") String transportId) {
		airTransportService.deleteAirTransport(transportId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("tenantId") String tenantId) {
		airTransportService.deleteAirTransports(tenantId);
		return Response.noContent().build();
	}

}
