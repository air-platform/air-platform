package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.FleetService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Fleet RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantFleetResource extends TenantProductResourceSupport<Fleet> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantFleetResource.class);

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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid Fleet fleet,
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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<Fleet> list(@PathParam("tenantId") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return fleetService.listTenantFleets(tenantId, reviewStatus, page, pageSize);
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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Fleet update(@PathParam("fleetId") String fleetId, @NotNull @Valid Fleet newFleet) {
	// return fleetService.updateFleet(fleetId, newFleet);
	// }

}
