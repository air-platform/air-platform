package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.service.product.AirTaxiService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTour RESTful API for ADMIN
 */
@RESTful
@RolesAllowed({ Roles.ROLE_ADMIN })
public class AdminAirTaxiResource extends AdminProductResourceSupport<AirTaxi> {
	// private static final Logger LOG = LoggerFactory.getLogger(AdminAirTaxiResource.class);

	@Resource
	private AirTaxiService airTaxiService;

	@Override
	protected StandardProductService<AirTaxi> getProductService() {
		return airTaxiService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid AirTaxi airTaxi,
	// @Context UriInfo uriInfo) {
	// AirTaxi created = airTaxiService.createAirTaxi(tenantId, airTaxi);
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
	// @JsonView(JsonViews.Admin.class)
	// public Page<AirTaxi> list(@QueryParam("tenant") String tenantId, @QueryParam("status") ReviewStatus reviewStatus,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// if (Strings.isBlank(tenantId)) {
	// return airTaxiService.listAllAirTaxis(reviewStatus, page, pageSize);
	// }
	// return airTaxiService.listTenantAirTaxis(tenantId, reviewStatus, page, pageSize);
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
	// return buildCountResponse(airTaxiService.countAllAirTaxis(reviewStatus));
	// }
	// return buildCountResponse(airTaxiService.countTenantAirTaxis(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{airTaxiId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public AirTaxi update(@PathParam("airTaxiId") String airTaxiId, @NotNull AirTaxi newAirTaxi) {
	// return airTaxiService.updateAirTaxi(airTaxiId, newAirTaxi);
	// }

}
