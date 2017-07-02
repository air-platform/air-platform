package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.service.product.FleetService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Fleet RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminFleetResource extends AdminProductResourceSupport<Fleet> {
	// private static final Logger LOG = LoggerFactory.getLogger(AdminFleetResource.class);

	@Resource
	private FleetService fleetService;

	@Override
	protected StandardProductService<Fleet> getProductService() {
		return fleetService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid Fleet fleet,
	// @Context UriInfo uriInfo) {
	// Fleet created = fleetService.createFleet(tenantId, fleet);
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List TODO query by flightNo, status etc.
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Page<Fleet> list(@QueryParam("tenant") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// if (Strings.isBlank(tenantId)) {
	// return fleetService.listAllFleets(reviewStatus, page, pageSize);
	// }
	// return fleetService.listTenantFleets(tenantId, reviewStatus, page, pageSize);
	// }
	//
	// /**
	// * Count to be reviewed
	// */
	// @GET
	// @Path("review/count")
	// @Produces(MediaType.APPLICATION_JSON)
	// public JsonObject listToBeApproved(@QueryParam("tenant") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus) {
	// if (Strings.isBlank(tenantId)) {
	// return buildCountResponse(fleetService.countAllFleets(reviewStatus));
	// }
	// return buildCountResponse(fleetService.countTenantFleets(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{fleetId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Fleet update(@PathParam("fleetId") String fleetId, @NotNull @Valid Fleet newFleet) {
	// return fleetService.updateFleet(fleetId, newFleet);
	// }
}
