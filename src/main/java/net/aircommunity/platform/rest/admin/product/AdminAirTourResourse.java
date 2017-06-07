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
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.tenant.TenantProductResourceSupport;
import net.aircommunity.platform.service.AirTourService;

/**
 * AirTour RESTful API for ADMIN ONLY
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminAirTourResourse extends TenantProductResourceSupport<AirTour> {
	private static final Logger LOG = LoggerFactory.getLogger(AdminAirTourResourse.class);

	@Resource
	private AirTourService airTourService;

	/**
	 * Create
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid AirTour airTour,
			@Context UriInfo uriInfo) {
		AirTour created = airTourService.createAirTour(tenantId, airTour);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<AirTour> list(@QueryParam("tenant") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		if (Strings.isBlank(tenantId)) {
			return airTourService.listAllAirTours(reviewStatus, page, pageSize);
		}
		return airTourService.listTenantAirTours(tenantId, reviewStatus, page, pageSize);
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
			return buildCountResponse(airTourService.countAllAirTours(reviewStatus));
		}
		return buildCountResponse(airTourService.countTenantAirTours(tenantId, reviewStatus));
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{airTourId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public AirTour update(@PathParam("airTourId") String airTourId, @NotNull @Valid AirTour newAirTour) {
		return airTourService.updateAirTour(airTourId, newAirTour);
	}

}
