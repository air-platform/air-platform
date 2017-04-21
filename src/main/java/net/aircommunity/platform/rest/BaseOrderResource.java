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
import javax.ws.rs.core.Response;

import net.aircommunity.platform.common.net.HttpHeaders;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
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
	 * Find
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public T find(@PathParam("orderId") String orderId) {
		String s = orderId;
		System.out.println(s);
		return (T) commonOrderService.findOrder(orderId);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public Response cancel(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CANCELLED);
		return Response.noContent().build();
	}

	/**
	 * Mark order as Paid
	 */
	@POST
	@Path("{orderId}/pay")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public Response payOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.PAID);
		return Response.noContent().build();
	}

	/**
	 * Finish order
	 */
	@POST
	@Path("{orderId}/finish")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT })
	public Response finishOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.FINISHED);
		return Response.noContent().build();
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public Response delete(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.DELETED);
		return Response.noContent().build();
	}

	// **************
	// ADMIN ONLY
	// **************

	/**
	 * Delete
	 */
	@DELETE
	@Path("{orderId}")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response forceDelete(@PathParam("orderId") String orderId) {
		commonOrderService.deleteOrder(orderId);
		return Response.noContent().build();
	}

	/**
	 * Delete all
	 */
	@DELETE
	@RolesAllowed(Roles.ROLE_ADMIN)
	public Response deleteAll(@PathParam("userId") String userId) {
		commonOrderService.deleteOrders(userId);
		return Response.noContent().build();
	}

	protected Response buildPageResponse(Page<T> result) {
		return Response.ok(result).header(HttpHeaders.HEADER_PAGINATION, HttpHeaders.pagination(result)).build();
	}

}
