package net.aircommunity.platform.rest.tenant.order;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.order.OrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * Base Tenant order RESTful API for ADMIN and TENANT (basic shared query for list)
 * 
 * @author Bin.Zhang
 */
public abstract class TenantBaseOrderResourceSupport<T extends Order> extends TenantOrderResourceSupport<T> {

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	public Page<T> list(@PathParam("tenantId") String tenantId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all orders with status: {} for tenant: {} ", status, tenantId);
		OrderService<T> service = getOrderService();
		if (!StandardOrderService.class.isAssignableFrom(service.getClass())) {
			LOG.error("Cannot listTenantOrders, expecting {}, but was {}", StandardOrderService.class,
					service.getClass());
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
		return ((StandardOrderService<T>) getOrderService()).listTenantOrders(tenantId, status, page, pageSize);
	}

}
