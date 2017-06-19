package net.aircommunity.platform.rest.tenant.order;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Base Tenant order RESTful API. <b>all permission</b> for ADMIN/TENANT
 * 
 * @author Bin.Zhang
 */
@SuppressWarnings("unchecked")
public abstract class TenantBaseOrderResource<T extends Order> extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(TenantBaseOrderResource.class);

	@Resource
	private CommonOrderService commonOrderService;

	// *****************
	// ADMIN & TENANT
	// *****************

	/**
	 * Find order
	 */
	@GET
	@Path("{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public T find(@PathParam("orderId") String orderId) {
		return (T) commonOrderService.findOrder(orderId);
	}

	/**
	 * Update order price
	 */
	@POST
	@Path("{orderId}/price")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void updateOrderPrice(@PathParam("orderId") String orderId, @NotNull JsonObject price) {
		double newPrice = price.getJsonNumber("price").doubleValue();
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
	 * Make order as contract signed (for offline)
	 */
	@POST
	@Path("{orderId}/sign-contract")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void signContractOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CONTRACT_SIGNED);
	}

	/**
	 * Mark order as paid (manually update pay status -> for offline payment)
	 */
	@POST
	@Path("{orderId}/pay")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void payOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.PAID);
	}

	/**
	 * Mark order as refunding (accepted user refund request)
	 */
	@POST
	@Path("{orderId}/refund/accept")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void acceptOrderRefund(@PathParam("orderId") String orderId, JsonObject request) {
		LOG.debug("Refund request: {}", request);
		BigDecimal amount = getAmount(request);
		LOG.debug("Refund amount original: {}", amount);
		Order order = commonOrderService.acceptOrderRefund(orderId, amount);
		if (order.getStatus() == Order.Status.REFUND_FAILED) {
			throw new AirException(Codes.ORDER_REFUND_FAILURE, M.msg(M.ORDER_REFUND_FAILURE, order.getOrderNo()));
		}
	}

	/**
	 * Mark order as refunding (reject user refund request)
	 */
	@POST
	@Path("{orderId}/refund/reject")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void rejectOrderRefund(@PathParam("orderId") String orderId, JsonObject rejectedReason) {
		String reason = getRejectedReason(rejectedReason);
		commonOrderService.rejectOrderRefund(orderId, reason);
	}

	/**
	 * Mark order as release-ticket (once payment is done)
	 */
	@POST
	@Path("{orderId}/release-ticket")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void releaseTicketOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.TICKET_RELEASED);
	}

	/**
	 * Mark order as finished
	 */
	@POST
	@Path("{orderId}/finish")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void finishOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.FINISHED);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void cancel(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CANCELLED);
	}

	/**
	 * Mark order as closed
	 */
	@POST
	@Path("{orderId}/close")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void closeOrder(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.CLOSED);
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE })
	public void delete(@PathParam("orderId") String orderId) {
		commonOrderService.updateOrderStatus(orderId, Order.Status.DELETED);
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
