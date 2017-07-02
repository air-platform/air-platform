package net.aircommunity.platform.rest.tenant;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.product.AirTaxiService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTour RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTaxiResource extends TenantProductResourceSupport<AirTaxi> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantAirTaxiResource.class);

	@Resource
	private AirTaxiService airTaxiService;

	@Override
	protected StandardProductService<AirTaxi> getProductService() {
		return airTaxiService;
	}

	/**
	 * Create
	 */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Response create(@PathParam("tenantId") String tenantId, @NotNull @Valid AirTaxi airTaxi,
	// @Context UriInfo uriInfo) {
	// AirTaxi created = airTaxiService.createAirTaxi(tenantId, airTaxi);
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created: {}", uri);
	// return Response.created(uri).build();
	// }

	/**
	 * List
	 */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<AirTaxi> list(@PathParam("tenantId") String tenantId, @QueryParam("status") ReviewStatus
	// reviewStatus,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return airTaxiService.listTenantAirTaxis(tenantId, reviewStatus, page, pageSize);
	// }

	/**
	 * Count to be reviewed
	 */
	// @GET
	// @Path("review/count")
	// @Produces(MediaType.APPLICATION_JSON)
	// public JsonObject listToBeApproved(@PathParam("tenantId") String tenantId,
	// @QueryParam("status") ReviewStatus reviewStatus) {
	// return buildCountResponse(airTaxiService.countTenantAirTaxis(tenantId, reviewStatus));
	// }

	/**
	 * Update
	 */
	// @PUT
	// @Path("{airTaxiId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public AirTaxi update(@PathParam("airTaxiId") String airTaxiId, @NotNull @Valid AirTaxi newAirTaxi) {
	// return airTaxiService.updateAirTaxi(airTaxiId, newAirTaxi);
	// }

}
