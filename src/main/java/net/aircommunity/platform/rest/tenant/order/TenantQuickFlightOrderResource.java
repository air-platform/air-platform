package net.aircommunity.platform.rest.tenant.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.QuickFlightOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.OrderService;
import net.aircommunity.platform.service.order.QuickFlightOrderService;

/**
 * Tenant QuickFlightOrder RESTful API for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
public class TenantQuickFlightOrderResource extends TenantOrderResourceSupport<QuickFlightOrder> {

	@Resource
	private QuickFlightOrderService quickFlightOrderService;

	@Override
	protected OrderService<QuickFlightOrder> getOrderService() {
		return quickFlightOrderService;
	}

	/**
	 * List (customized query)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<QuickFlightOrder> list(@PathParam("tenantId") String tenantId,
			@QueryParam("status") Order.Status status, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all quickflight orders with status: {} for tenant: {} ", status, tenantId);
		return quickFlightOrderService.listTenantQuickFlightOrders(tenantId, status, page, pageSize);
	}

	/**
	 * List (orders in CREATED)
	 */
	@GET
	@Path("unconfirmed")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<QuickFlightOrder> listUnconfirmed(@PathParam("tenantId") String tenantId,
			@QueryParam("status") Order.Status status, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all unconfirmed quickflight orders with for tenant: {} ", tenantId);
		return quickFlightOrderService.listTenantUnconfirmedOrders(tenantId, page, pageSize);
	}

}
