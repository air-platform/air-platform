package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.JetTravelService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * Jet Travel RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantJetTravelResource extends TenantProductResourceSupport<JetTravel> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantJetTravelResource.class);

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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid JetTravel jetTravel,
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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<JetTravel> list(@PathParam("tenantId") String tenantId, @QueryParam("status") ReviewStatus
	// reviewStatus,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return jetTravelService.listTenantJetTravels(tenantId, reviewStatus, page, pageSize);
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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public JetTravel update(@PathParam("jetTravelId") String jetTravelId, @NotNull @Valid JetTravel newJetTravel) {
	// return jetTravelService.updateJetTravel(jetTravelId, newJetTravel);
	// }

}
