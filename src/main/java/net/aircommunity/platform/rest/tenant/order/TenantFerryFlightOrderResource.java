package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlightOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.FerryFlightOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * Tenant FerryFlightOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantFerryFlightOrderResource extends TenantBaseOrderResourceSupport<FerryFlightOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantFerryFlightOrderResource.class);

	@Resource
	private FerryFlightOrderService ferryFlightOrderService;

	@Override
	protected OrderService<FerryFlightOrder> getOrderService() {
		return ferryFlightOrderService;
	}

	/**
	 * List
	 */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<FerryFlightOrder> list(@PathParam("tenantId") String tenantId,
	// @QueryParam("status") Order.Status status, @QueryParam("page") @DefaultValue("0") int page,
	// @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// LOG.debug("List all orders with status: {} for tenant: {} ", status, tenantId);
	// return ferryFlightOrderService.listTenantFerryFlightOrders(tenantId, status, page, pageSize);
	// }
}
