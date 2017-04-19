package net.aircommunity.platform.rest.tenant;

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

import io.micro.annotation.RESTful;
import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTourService;

/**
 * AirTour RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * Created by guankai on 15/04/2017.
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantAirTourResourse {
	private static final Logger LOG = LoggerFactory.getLogger(TenantAirTourResourse.class);

	@Resource
	private AirTourService airTourService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTour airTour,
			@Context UriInfo uriInfo) {
		AirTour created = airTourService.createAirTour(tenantId, airTour);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{airTourId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTour find(@PathParam("airTourId") String airTourId) {
		return airTourService.findAirTour(airTourId);
	}

	/**
	 * List TODO query
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("tenantId") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		Page<AirTour> result = airTourService.listAirTours(tenantId, page, pageSize);
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airTourId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AirTour update(@PathParam("airTourId") String airTourId, @NotNull @Valid AirTour newAirTour) {
		return airTourService.updateAirTour(airTourId, newAirTour);
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
