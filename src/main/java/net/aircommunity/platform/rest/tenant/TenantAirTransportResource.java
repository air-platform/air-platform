package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.AirTransportService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTransport RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTransportResource extends TenantProductResourceSupport<AirTransport> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantAirTransportResource.class);

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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTransport airTransport,
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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<AirTransport> list(@PathParam("tenantId") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") @DefaultValue("0") int page,
	// @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return airTransportService.listTenantAirTransports(tenantId, reviewStatus, page, pageSize);
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
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public AirTransport update(@PathParam("transportId") String transportId,
	// @NotNull @Valid AirTransport newAirTransport) {
	// return airTransportService.updateAirTransport(transportId, newAirTransport);
	// }

}
