package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Base User order RESTful API. <b>all permission</b> for ADMIN/USER
 * 
 * @author Bin.Zhang
 */
public abstract class UserBaseOrderResource<T extends Order> {

	@Resource
	private CommonOrderService commonOrderService;

	/**
	 * Find order
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	@JsonView(JsonViews.User.class)
	public T find(@PathParam("orderId") String orderId) {
		return (T) commonOrderService.findOrder(orderId);
	}

	// *****************
	// ADMIN & USER
	// *****************

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public void cancel(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CANCELLED);
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
	public void delete(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.DELETED);
	}

}
