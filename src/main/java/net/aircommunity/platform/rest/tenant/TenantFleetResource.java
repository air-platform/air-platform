package net.aircommunity.platform.rest.tenant;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.FleetService;

/**
 * Fleet RESTful API. NOTE: <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
public class TenantFleetResource extends TenantProductResourceSupport<Fleet> {
	private static final Logger LOG = LoggerFactory.getLogger(TenantFleetResource.class);

	@Resource
	private FleetService fleetService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid Fleet fleet,
			@Context UriInfo uriInfo) {
		Fleet created = fleetService.createFleet(tenantId, fleet);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List TODO query by flightNo, status etc.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<Fleet> list(@PathParam("tenantId") String tenantId, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return fleetService.listFleets(tenantId, page, pageSize);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{fleetId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Fleet update(@PathParam("fleetId") String fleetId, @NotNull @Valid Fleet newFleet) {
		return fleetService.updateFleet(fleetId, newFleet);
	}
}
