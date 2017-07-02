package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.service.product.FerryFlightService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * FerryFlight RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminFerryFlightResource extends AdminProductResourceSupport<FerryFlight> {
	// private static final Logger LOG = LoggerFactory.getLogger(AdminFerryFlightResource.class);

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
	// @JsonView(JsonViews.Admin.class)
	// public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid FerryFlight ferryFlight,
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
	// @JsonView(JsonViews.Admin.class)
	// public Page<FerryFlight> list(@QueryParam("tenant") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") @DefaultValue("0") int page,
	// @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// if (Strings.isBlank(tenantId)) {
	// return ferryFlightService.listAllFerryFlights(reviewStatus, page, pageSize);
	// }
	// return ferryFlightService.listTenantFerryFlights(tenantId, reviewStatus, page, pageSize);
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
	// return buildCountResponse(ferryFlightService.countAllFerryFlights(reviewStatus));
	// }
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
	// @JsonView(JsonViews.Admin.class)
	// public FerryFlight update(@PathParam("ferryFlightId") String ferryFlightId,
	// @NotNull @Valid FerryFlight newFerryFlight) {
	// return ferryFlightService.updateFerryFlight(ferryFlightId, newFerryFlight);
	// }
}
