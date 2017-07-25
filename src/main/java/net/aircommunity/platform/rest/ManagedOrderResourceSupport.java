package net.aircommunity.platform.rest;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.constraint.NotEmpty;
import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.AircraftCandidate;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.OrderItemCandidate;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.order.CandidateOrderService;
import net.aircommunity.platform.service.order.CharterOrderService;
import net.aircommunity.platform.service.order.OrderService;
import net.aircommunity.platform.service.order.QuickFlightOrderService;

/**
 * Base order RESTful API for ADMIN and TENANT
 * 
 * @author Bin.Zhang
 */
public abstract class ManagedOrderResourceSupport<T extends Order> extends BaseResourceSupport {
	protected static final Logger LOG = LoggerFactory.getLogger(ManagedOrderResourceSupport.class);

	@Resource
	protected CharterOrderService charterOrderService;

	@Resource
	protected QuickFlightOrderService quickFlightOrderService;

	// ************************
	// QuickFlightOrder ONLY
	// ************************

	/**
	 * Update order to initiate candidates (TENANT or ADMIN)
	 */
	@POST
	@Path("{orderId}/candidates/initiate")
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_TENANT, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_ADMIN })
	public void initiateCandidates(@PathParam("orderId") String orderId,
			@Valid @NotEmpty Set<AircraftCandidate> candidates) {
		quickFlightOrderService.initiateOrderCandidates(orderId, candidates);
	}

	/**
	 * Update order to promote candidates (ADMIN)
	 */
	@POST
	@Path("{orderId}/candidates/promote")
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void promoteCandidates(@PathParam("orderId") String orderId, @Valid @NotEmpty Set<String> candidateIds) {
		quickFlightOrderService.promoteOrderCandidates(orderId, candidateIds);
	}

	// ************************
	// Generic
	// ************************

	/**
	 * Update order price
	 */
	@POST
	@Path("{orderId}/price")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateOrderPrice(@PathParam("orderId") String orderId, @NotNull JsonObject request) {
		BigDecimal totalAmount = getAmount(request);
		Order order = getOrderService().findOrder(orderId);
		// specific case for orders that have candidates(update price and also offer a candidate)
		if (order.hasCandidates()) {
			updateCandidateOrderPrice(order, totalAmount, request);
		}
		else {
			getOrderService().updateOrderTotalAmount(orderId, totalAmount);
		}
	}

	@SuppressWarnings("rawtypes")
	private void updateCandidateOrderPrice(Order order, BigDecimal totalAmount, JsonObject request) {
		String candidateId = getOrderItemCandidate(request);
		String status = getStatus(request);
		String orderId = order.getId();
		if (Strings.isBlank(candidateId)) {
			getOrderService().updateOrderTotalAmount(orderId, totalAmount);
			return;
		}
		CandidateOrderService service = commonOrderService.adaptOrderService(order.getType());
		// offer or refuse
		OrderItemCandidate.Status targetStatus = OrderItemCandidate.Status.fromString(status);
		if (targetStatus == null) {
			throw new AirException(Codes.ORDER_CANNOT_UPDATE_PRICE, M.msg(M.ORDER_INVALID_PRICING_ARGS));
		}
		switch (targetStatus) {
		case CANDIDATE:
			service.refuseOrderCandidate(orderId, candidateId);
			break;

		case OFFERED:
			service.offerOrderCandidate(orderId, candidateId, totalAmount);
			break;

		default:
			throw new AirException(Codes.ORDER_CANNOT_UPDATE_PRICE, M.msg(M.ORDER_INVALID_PRICING_ARGS));
		}
	}

	/**
	 * Mark order as confirmed
	 */
	@POST
	@Path("{orderId}/confirm")
	public void confirmOrder(@PathParam("orderId") String orderId) {
		getOrderService().updateOrderStatus(orderId, Order.Status.CONFIRMED);
	}

	/**
	 * Make order as contract signed (for offline)
	 */
	@POST
	@Path("{orderId}/sign-contract")
	public void signContractOrder(@PathParam("orderId") String orderId) {
		getOrderService().updateOrderStatus(orderId, Order.Status.CONTRACT_SIGNED);
	}

	/**
	 * Mark order as paid (manually update pay status -> for offline payment)
	 */
	@POST
	@Path("{orderId}/pay")
	public void payOrder(@PathParam("orderId") String orderId) {
		getOrderService().updateOrderStatus(orderId, Order.Status.PAID);
	}

	/**
	 * Refund started from TENANT/ADMIN without user request
	 */
	@POST
	@Path("{orderId}/refund/initiate")
	@Consumes(MediaType.APPLICATION_JSON)
	public void initiateOrderRefund(@PathParam("orderId") String orderId, @NotNull JsonObject request) {
		LOG.debug("Initiate refund request: {}", request);
		BigDecimal refundAmount = getAmount(request);
		String refundReason = getReason(request);
		LOG.debug("Refund amount original: {}", refundAmount);
		Order order = getOrderService().initiateOrderRefund(orderId, refundAmount, refundReason);
		if (order.getStatus() == Order.Status.REFUND_FAILED) {
			throw new AirException(Codes.ORDER_REFUND_FAILURE, M.msg(M.ORDER_REFUND_FAILURE, order.getOrderNo()));
		}
	}

	/**
	 * Mark order as refunding (accepted user refund request)
	 */
	@POST
	@Path("{orderId}/refund/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public void acceptOrderRefund(@PathParam("orderId") String orderId, JsonObject request) {
		LOG.debug("Refund request: {}", request);
		BigDecimal amount = getAmount(request);
		LOG.debug("Refund amount original: {}", amount);
		Order order = getOrderService().acceptOrderRefund(orderId, amount);
		if (order.getStatus() == Order.Status.REFUND_FAILED) {
			throw new AirException(Codes.ORDER_REFUND_FAILURE, M.msg(M.ORDER_REFUND_FAILURE, order.getOrderNo()));
		}
	}

	/**
	 * Mark order as refunding (reject user refund request)
	 */
	@POST
	@Path("{orderId}/refund/reject")
	@Consumes(MediaType.APPLICATION_JSON)
	public void rejectOrderRefund(@PathParam("orderId") String orderId, JsonObject rejectedReason) {
		String reason = getReason(rejectedReason);
		getOrderService().rejectOrderRefund(orderId, reason);
	}

	/**
	 * Mark order as release-ticket (once payment is done)
	 */
	@POST
	@Path("{orderId}/release-ticket")
	public void releaseTicketOrder(@PathParam("orderId") String orderId) {
		getOrderService().updateOrderStatus(orderId, Order.Status.TICKET_RELEASED);
	}

	/**
	 * Mark order as finished
	 */
	@POST
	@Path("{orderId}/finish")
	public void finishOrder(@PathParam("orderId") String orderId) {
		getOrderService().updateOrderStatus(orderId, Order.Status.FINISHED);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	public void cancel(@PathParam("orderId") String orderId, JsonObject cancelReason) {
		String reason = getReason(cancelReason);
		getOrderService().cancelOrder(orderId, reason);
	}

	/**
	 * Mark order as closed
	 */
	@POST
	@Path("{orderId}/close")
	@Consumes(MediaType.APPLICATION_JSON)
	public void closeOrder(@PathParam("orderId") String orderId, JsonObject closeReason) {
		String reason = getReason(closeReason);
		getOrderService().closeOrder(orderId, reason);
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	public void delete(@PathParam("orderId") String orderId) {
		getOrderService().softDeleteOrder(orderId);
	}

	protected abstract OrderService<T> getOrderService();
}
