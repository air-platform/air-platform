package net.aircommunity.platform.rest.tenant;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
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
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
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
public class TenantAirTransportResource extends TenantProductResourceSupport<AirTransport> {
	private static final Logger LOG = LoggerFactory.getLogger(TenantAirTransportResource.class);

	@Resource
	private AirTransportService airTransportService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTransport airTransport,
			@Context UriInfo uriInfo) {
		AirTransport created = airTransportService.createAirTransport(tenantId, airTransport);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List (TODO more query)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<AirTransport> list(@PathParam("tenantId") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return airTransportService.listTenantAirTransports(tenantId, reviewStatus, page, pageSize);
	}

	/**
	 * Count to be reviewed
	 */
	@GET
	@Path("review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listToBeApproved(@PathParam("tenantId") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(airTransportService.countTenantAirTransports(tenantId, reviewStatus));
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{transportId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public AirTransport update(@PathParam("transportId") String transportId,
			@NotNull @Valid AirTransport newAirTransport) {
		return airTransportService.updateAirTransport(transportId, newAirTransport);
	}
}
