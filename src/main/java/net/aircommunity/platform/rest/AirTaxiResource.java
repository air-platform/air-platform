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
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Created by guankai on 15/04/2017. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 */
@RESTful
@Path("taxis")
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class AirTaxiResource {
	private static final Logger LOG = LoggerFactory.getLogger(AirTaxiResource.class);

	@Resource
	private AirTaxiService airTaxiService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response listAll(@QueryParam("departure") String departure, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize, @Context SecurityContext context) {
		LOG.debug("list all air taxi with departure: {}", departure);
		Page<AirTaxi> result = Page.emptyPage(page, pageSize);
		// redirect to tenant owned
		if (context.isUserInRole(Role.TENANT.name())) {
			result = airTaxiService.listAirTaxis(context.getUserPrincipal().getName(), page, pageSize);
		}
		else {
			if (departure == null) {
				result = airTaxiService.listAirTaxis(page, pageSize);
			}
			else {
				result = airTaxiService.listAirTaxisByDeparture(departure, page, pageSize);
			}
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{airTaxiId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public AirTaxi find(@PathParam("airTaxiId") String airTaxiId) {
		return airTaxiService.findAirTaxi(airTaxiId);
	}

	// TODO query by xxx

	// ***********************
	// ADMIN/TENANT
	// ***********************

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTaxi request,
			@Context UriInfo uriInfo) {
		AirTaxi created = airTaxiService.createAirTaxi(tenantId, request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airTaxiId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTaxi update(@PathParam("airTaxiId") String airTaxiId, @NotNull AirTaxi request) {
		return airTaxiService.updateAirTaxi(airTaxiId, request);
	}

	/**
	 * List all of an tenant
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllForTenant(@PathParam("tenantId") String tenantId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<AirTaxi> result = airTaxiService.listAirTaxis(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airTaxiId}")
	public Response delete(@PathParam("airTaxiId") String airTaxiId) {
		airTaxiService.deleteAirTaxi(airTaxiId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("tenantId") String tenantId) {
		airTaxiService.deleteAirTaxis(tenantId);
		return Response.noContent().build();
	}

}
