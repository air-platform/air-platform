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
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AirTransportService;

/**
 * AirTransport RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantAirTransportResource {
	private static final Logger LOG = LoggerFactory.getLogger(TenantAirTransportResource.class);

	@Resource
	private AirTransportService airTransportService;

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
	 * Find
	 */
	@GET
	@Path("{transportId}")
	@Produces(MediaType.APPLICATION_JSON)
	public AirTransport find(@PathParam("transportId") String transportId) {
		return airTransportService.findAirTransport(transportId);
	}

	/**
	 * List (TODO more query)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@PathParam("tenantId") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
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
