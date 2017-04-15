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
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.AirTourService;
import net.aircommunity.rest.annotation.RESTful;

/**
 * Created by guankai on 15/04/2017. NOTE: <b>all permission</b> for ADMIN/TENANT and <b>list/find/query</b> for ANYONE
 */
@RESTful
@Path("tours")
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class AirTourResourse {
	private static final Logger LOG = LoggerFactory.getLogger(AirTourResourse.class);

	@Resource
	private AirTourService airTourService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response listALl(@QueryParam("city") String city, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		LOG.debug("get all air tours start..");
		Page<AirTour> result = Page.emptyPage(page, pageSize);
		if (city == null) {
			result = airTourService.listAirTours(page, pageSize);
		}
		else {
			result = airTourService.listAirToursByCity(city, page, pageSize);
		}
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{airTourId}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public AirTour find(@PathParam("airTourId") String airTourId) {
		return airTourService.findAirTour(airTourId);
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
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTour request,
			@Context UriInfo uriInfo) {
		AirTour created = airTourService.createAirTour(tenantId, request);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airTourId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTour update(@PathParam("airTourId") @NotNull String airTourId, @NotNull @Valid AirTour request) {
		return airTourService.updateAirTour(airTourId, request);
	}

	/**
	 * List all for tenant
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllForTenant(@PathParam("tenantId") String tenantId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<AirTour> airTours = airTourService.listAirTours(tenantId, page, pageSize);
		return Response.ok(airTours).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(airTours)).build();
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{airTourId}")
	public Response delete(@PathParam("airTourId") String airTourId) {
		airTourService.deleteAirTour(airTourId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	public Response deleteAll(@PathParam("tenantId") String tenantId) {
		airTourService.deleteAirTours(tenantId);
		return Response.noContent().build();
	}
}
