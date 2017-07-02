package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.FerryFlightService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * FerryFlight RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantFerryFlightResource extends TenantProductResourceSupport<FerryFlight> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantFerryFlightResource.class);

	@Resource
	private FerryFlightService ferryFlightService;

	@Override
	protected StandardProductService<FerryFlight> getProductService() {
		return ferryFlightService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid FerryFlight ferryFlight,
	// @Context UriInfo uriInfo) {
	// FerryFlight created = ferryFlightService.createFerryFlight(tenantId, ferryFlight);
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List TODO query by departure/arrival/date/timeSlot
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<FerryFlight> list(@PathParam("tenantId") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") @DefaultValue("0") int page,
	// @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return ferryFlightService.listTenantFerryFlights(tenantId, reviewStatus, page, pageSize);
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
	// return buildCountResponse(ferryFlightService.countTenantFerryFlights(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{ferryFlightId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public FerryFlight update(@PathParam("ferryFlightId") String ferryFlightId,
	// @NotNull @Valid FerryFlight newFerryFlight) {
	// return ferryFlightService.updateFerryFlight(ferryFlightId, newFerryFlight);
	// }

}
