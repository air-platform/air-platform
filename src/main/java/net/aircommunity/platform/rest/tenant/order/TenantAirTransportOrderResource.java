package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.AirTransportOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * AirTransportOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantAirTransportOrderResource extends TenantBaseOrderResourceSupport<AirTransportOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantAirTransportOrderResource.class);

	@Resource
	private AirTransportOrderService airTransportOrderService;

	@Override
	protected OrderService<AirTransportOrder> getOrderService() {
		return airTransportOrderService;
	}

	/**
	 * List //
	 */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<AirTransportOrder> list(@PathParam("tenantId") String tenantId,
	// @QueryParam("status") Order.Status status, @QueryParam("page") @DefaultValue("0") int page,
	// @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// LOG.debug("List all orders with status: {} for tenant: {} ", status, tenantId);
	// return airTransportOrderService.listTenantAirTransportOrders(tenantId, status, page, pageSize);
	// }

}
