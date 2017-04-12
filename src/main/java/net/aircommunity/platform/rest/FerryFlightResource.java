package net.aircommunity.platform.rest;

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

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.FerryFlightService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * FerryFlight RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class FerryFlightResource {
	private static final Logger LOG = LoggerFactory.getLogger(FerryFlightResource.class);

	@Resource
	private FerryFlightService ferryFlightService;

	/**
	 * Create a ferryFlight
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid FerryFlight ferryFlight,
			@Context UriInfo uriInfo) {
		FerryFlight created = ferryFlightService.createFerryFlight(tenantId, ferryFlight);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List all ferryFlight of an tenant
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAll(@PathParam("tenantId") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<FerryFlight> result = ferryFlightService.listFerryFlights(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Get a ferryFlight
	 */
	@GET
	@Path("{ferryFlightId}")
	@Produces(MediaType.APPLICATION_JSON)
	public FerryFlight find(@PathParam("ferryFlightId") String ferryFlightId) {
		return ferryFlightService.findFerryFlight(ferryFlightId);
	}

	/**
	 * Update a ferryFlight
	 */
	@PUT
	@Path("{ferryFlightId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public FerryFlight update(@PathParam("ferryFlightId") String ferryFlightId,
			@NotNull @Valid FerryFlight newFerryFlight) {
		return ferryFlightService.updateFerryFlight(ferryFlightId, newFerryFlight);
	}

	/**
	 * Delete a ferryFlight
	 */
	@DELETE
	@Path("{FerryFlightId}")
	public Response delete(@PathParam("ferryFlightId") String ferryFlightId) {
		ferryFlightService.deleteFerryFlight(ferryFlightId);
		return Response.noContent().build();
	}

	/**
	 * Delete all ferryFlights for a tenant
	 */
	@DELETE
	public Response deleteAll(@PathParam("tenantId") String tenantId) {
		ferryFlightService.deleteFerryFlights(tenantId);
		return Response.noContent().build();
	}

}
