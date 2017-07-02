package net.aircommunity.platform.rest.admin.product;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.service.product.JetTravelService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Jet Travel RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminJetTravelResource extends AdminProductResourceSupport<JetTravel> {
	// private static final Logger LOG = LoggerFactory.getLogger(AdminJetTravelResource.class);

	@Resource
	private JetTravelService jetTravelService;

	@Override
	protected StandardProductService<JetTravel> getProductService() {
		return jetTravelService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public Response create(@QueryParam("tenant") String tenantId, @NotNull @Valid JetTravel jetTravel,
	// @Context UriInfo uriInfo) {
	// JetTravel created = jetTravelService.createJetTravel(tenantId, jetTravel);
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
	// public Page<JetTravel> list(@QueryParam("tenant") String tenantId, @QueryParam("status") ReviewStatus
	// reviewStatus,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// if (Strings.isBlank(tenantId)) {
	// return jetTravelService.listAllJetTravels(reviewStatus, page, pageSize);
	// }
	// return jetTravelService.listTenantJetTravels(tenantId, reviewStatus, page, pageSize);
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
	// return buildCountResponse(jetTravelService.countAllJetTravels(reviewStatus));
	// }
	// return buildCountResponse(jetTravelService.countTenantJetTravels(tenantId, reviewStatus));
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{jetTravelId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Admin.class)
	// public JetTravel update(@PathParam("jetTravelId") String jetTravelId, @NotNull @Valid JetTravel newJetTravel) {
	// return jetTravelService.updateJetTravel(jetTravelId, newJetTravel);
	// }

}
