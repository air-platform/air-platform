package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.AirTourService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTour RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTourResourse extends TenantProductResourceSupport<AirTour> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantAirTourResourse.class);

	@Resource
	private AirTourService airTourService;

	@Override
	protected StandardProductService<AirTour> getProductService() {
		return airTourService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTour airTour,
	// @Context UriInfo uriInfo) {
	// AirTour created = airTourService.createAirTour(tenantId, airTour);
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created: {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<AirTour> list(@PathParam("tenantId") String tenantId, @QueryParam("status") ReviewStatus
	// reviewStatus,
	// @QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
	// return airTourService.listTenantAirTours(tenantId, reviewStatus, page, pageSize);
	// }
	//
	// /**
	// * Count to be reviewed
	// */
	// @GET
	// @Path("review/count")
	// @Produces(MediaType.APPLICATION_JSON)
	// public JsonObject listToBeApproved(@PathParam("tenantId") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus) {
	// return buildCountResponse(airTourService.countTenantAirTours(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{airTourId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public AirTour update(@PathParam("airTourId") String airTourId, @NotNull @Valid AirTour newAirTour) {
	// return airTourService.updateAirTour(airTourId, newAirTour);
	// }

}
