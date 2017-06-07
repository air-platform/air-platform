package net.aircommunity.platform.rest.admin.product;

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
import io.micro.common.Strings;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.tenant.TenantProductResourceSupport;
import net.aircommunity.platform.service.FerryFlightService;

/**
 * FerryFlight RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminFerryFlightResource extends TenantProductResourceSupport<FerryFlight> {
	private static final Logger LOG = LoggerFactory.getLogger(AdminFerryFlightResource.class);

	@Resource
	private FerryFlightService ferryFlightService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid FerryFlight ferryFlight,
			@Context UriInfo uriInfo) {
		FerryFlight created = ferryFlightService.createFerryFlight(tenantId, ferryFlight);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List TODO query by departure/arrival/date/timeSlot
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<FerryFlight> list(@QueryParam("tenant") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		if (Strings.isBlank(tenantId)) {
			return ferryFlightService.listAllFerryFlights(reviewStatus, page, pageSize);
		}
		return ferryFlightService.listTenantFerryFlights(tenantId, reviewStatus, page, pageSize);
	}

	/**
	 * Count to be reviewed
	 */
	@GET
	@Path("review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listToBeApproved(@QueryParam("tenant") String tenantId,
			@QueryParam("status") ReviewStatus reviewStatus) {
		if (Strings.isBlank(tenantId)) {
			return buildCountResponse(ferryFlightService.countAllFerryFlights(reviewStatus));
		}
		return buildCountResponse(ferryFlightService.countTenantFerryFlights(tenantId, reviewStatus));
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{ferryFlightId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public FerryFlight update(@PathParam("ferryFlightId") String ferryFlightId,
			@NotNull @Valid FerryFlight newFerryFlight) {
		return ferryFlightService.updateFerryFlight(ferryFlightId, newFerryFlight);
	}
}
