package net.aircommunity.platform.rest.admin.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.rest.tenant.order.TenantBaseOrderResource;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Order RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminOrderResource extends TenantBaseOrderResource<Order> {

	@Resource
	private CommonOrderService commonOrderService;

	/**
	 * List All
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Order> listAllOrders(@QueryParam("user") String userId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("1") int page, @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
		if (Strings.isBlank(userId)) {
			return commonOrderService.listAllOrders(status, page, pageSize);
		}
		return commonOrderService.listAllUserOrders(userId, status, page, pageSize);
	}

	/**
	 * Update order status (use it with caution) XXX with force (with state transition check)?
	 */
	// @POST
	// @Path("{orderId}/status")
	// @Produces(MediaType.APPLICATION_JSON)
	// public void updateOrderStatus(@PathParam("orderId") String orderId,
	// @NotNull @QueryParam("status") Order.Status status) {
	// commonOrderService.updateOrderStatus(orderId, status);
	// }

}
