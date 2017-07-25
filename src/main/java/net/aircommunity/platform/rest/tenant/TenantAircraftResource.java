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

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Aircraft;
import net.aircommunity.platform.rest.AircraftCommentResource;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.AircraftService;

/**
 * Aircraft RESTful API for TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAircraftResource {
	private static final Logger LOG = LoggerFactory.getLogger(TenantAircraftResource.class);

	@Resource
	private AircraftService aircraftService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid Aircraft aircraft,
			@Context UriInfo uriInfo) {
		Aircraft created = aircraftService.createAircraft(tenantId, aircraft);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{aircraftId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Aircraft find(@PathParam("aircraftId") String aircraftId) {
		return aircraftService.findAircraft(aircraftId);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<Aircraft> list(@PathParam("tenantId") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return aircraftService.listAircrafts(tenantId, page, pageSize);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{aircraftId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Aircraft update(@PathParam("aircraftId") String aircraftId, @NotNull @Valid Aircraft newAircraft) {
		return aircraftService.updateAircraft(aircraftId, newAircraft);
	}

	/**
	 * Delete
	 */
	@DELETE
	@Path("{aircraftId}")
	public void delete(@PathParam("aircraftId") String aircraftId) {
		aircraftService.deleteAircraft(aircraftId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	public void deleteAll(@PathParam("tenantId") String tenantId) {
		aircraftService.deleteAircrafts(tenantId);
	}

	// ***********************
	// Comments
	// ***********************

	@Resource
	private AircraftCommentResource aircraftCommentResource;

	@Path("{aircraftId}/comments")
	public AircraftCommentResource comments(@PathParam("aircraftId") String aircraftId) {
		return aircraftCommentResource;
	}

}
