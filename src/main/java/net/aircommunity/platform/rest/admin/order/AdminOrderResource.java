package net.aircommunity.platform.rest.admin.order;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Order RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminOrderResource {

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
	 * Find
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Order findOrder(@PathParam("orderId") String orderId) {
		return commonOrderService.findOrder(orderId);
	}

	/**
	 * Update order status
	 */
	@POST
	@Path("{orderId}/status")
	@Produces(MediaType.APPLICATION_JSON)
	public void updateOrderStatus(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("status") Order.Status status) {
		commonOrderService.updateOrderStatus(orderId, status);
	}

	/**
	 * Hard deletion from DB
	 */
	@DELETE
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteOrder(@PathParam("orderId") String orderId) {
		commonOrderService.deleteOrder(orderId);
	}

}
