package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTaxiOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTaxiOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * AirTaxiOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTaxiOrderResource extends TenantBaseOrderResourceSupport<AirTaxiOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantAirTaxiOrderResource.class);

	@Resource
	private AirTaxiOrderService airTaxiOrderService;

	@Override
	protected OrderService<AirTaxiOrder> getOrderService() {
		return airTaxiOrderService;
	}

	// /**
	// * List
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<AirTaxiOrder> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// LOG.debug("List all orders with status: {} for tenant: {} ", status, tenantId);
	// return airTaxiOrderService.listTenantAirTaxiOrders(tenantId, status, page, pageSize);
	// }

}
