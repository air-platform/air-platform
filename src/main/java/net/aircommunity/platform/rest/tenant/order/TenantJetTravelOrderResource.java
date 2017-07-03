package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.JetTravelOrderService;
import net.aircommunity.platform.service.order.OrderService;

/**
 * Tenant JetTravelOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantJetTravelOrderResource extends TenantBaseOrderResourceSupport<JetTravelOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(TenantJetTravelOrderResource.class);

	@Resource
	private JetTravelOrderService jetTravelOrderService;

	@Override
	protected OrderService<JetTravelOrder> getOrderService() {
		return jetTravelOrderService;
	}

	/**
	 * List
	 */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	// public Page<JetTravelOrder> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status
	// status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// LOG.debug("List all orders with status: {} for tenant: {} ", status, tenantId);
	// return jetTravelOrderService.listTenantJetTravelOrders(tenantId, status, page, pageSize);
	// }

}
