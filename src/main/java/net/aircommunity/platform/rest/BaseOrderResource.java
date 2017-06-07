package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Base Tenant order RESTful API. <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
public abstract class BaseOrderResource<T extends Order> {

	@Resource
	private CommonOrderService commonOrderService;

	/**
	 * Find order
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public T find(@PathParam("orderId") String orderId) {
		return (T) commonOrderService.findOrder(orderId);
	}

	// *****************
	// ADMIN & USER
	// *****************

	/**
	 * Mark order as refund
	 */
	@POST
	@Path("{orderId}/refund")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_USER })
	public void refundOrder(@PathParam("orderId") String orderId) {
		// and update to Order.Status.REFUNDED once trade completed
		commonOrderService.updateOrderStatus(orderId, Order.Status.REFUNDING);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_USER })
	public void cancel(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CANCELLED);
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_USER })
	public void delete(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.DELETED);
	}

	// *****************
	// ADMIN & TENANT
	// *****************

	/**
	 * Update order price
	 */
	@POST
	@Path("{orderId}/price")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void updateOrderPrice(@PathParam("orderId") String orderId, double newPrice) {
		commonOrderService.updateOrderPrice(orderId, newPrice);
	}

	/**
	 * Mark order as confirmed
	 */
	@POST
	@Path("{orderId}/confirm")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void confirmOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CONFIRMED);
	}

	/**
	 * Make order as contract signed
	 */
	@POST
	@Path("{orderId}/sign-contract")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void signContractOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CONTRACT_SIGNED);
	}

	/**
	 * Mark order as paid (manually update pay status)
	 */
	@POST
	@Path("{orderId}/pay")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void payOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.PAID);
	}

	/**
	 * Mark order as release-ticket
	 */
	@POST
	@Path("{orderId}/release-ticket")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void releaseTicketOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.TICKET_RELEASED);
	}

	/**
	 * Finish order
	 */
	@POST
	@Path("{orderId}/finish")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void finishOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.FINISHED);
	}

	// **************
	// ADMIN ONLY
	// **************

	/**
	 * Delete
	 */
	@DELETE
	@Path("{orderId}/force")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void forceDelete(@PathParam("orderId") String orderId) {
		commonOrderService.deleteOrder(orderId);
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void deleteAll(@PathParam("userId") String userId) {
		commonOrderService.deleteOrders(userId);
	}

}
