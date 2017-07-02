package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.service.product.AirTransportService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTransport RESTful for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminAirTransportResource extends AdminProductResourceSupport<AirTransport> {
	// private static final Logger LOG = LoggerFactory.getLogger(AdminAirTransportResource.class);

	@Resource
	private AirTransportService airTransportService;

	@Override
	protected StandardProductService<AirTransport> getProductService() {
		return airTransportService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid AirTransport airTransport,
	// @Context UriInfo uriInfo) {
	// AirTransport created = airTransportService.createAirTransport(tenantId, airTransport);
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Page<AirTransport> list(@QueryParam("tenant") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") @DefaultValue("0") int page,
	// @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// if (Strings.isBlank(tenantId)) {
	// return airTransportService.listAllAirTransports(reviewStatus, page, pageSize);
	// }
	// return airTransportService.listTenantAirTransports(tenantId, reviewStatus, page, pageSize);
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
	// return buildCountResponse(airTransportService.countAllAirTransports(reviewStatus));
	// }
	// return buildCountResponse(airTransportService.countTenantAirTransports(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{transportId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public AirTransport update(@PathParam("transportId") String transportId,
	// @NotNull @Valid AirTransport newAirTransport) {
	// return airTransportService.updateAirTransport(transportId, newAirTransport);
	// }

}
