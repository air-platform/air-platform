package net.aircommunity.platform.service.order;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Refund;

/**
 * Common order service list/query all {@code Order}s for a {@code User}.
 * 
 * @author Bin.Zhang
 */
public interface CommonOrderService extends OrderService<Order> {

	/**
	 * Test if there is any order for this user (among all order types), it can be used for first order price-off.
	 * 
	 * @param userId the user id
	 * @return true if exists, false otherwise
	 */
	boolean existsOrderForUser(@Nonnull String userId);

	/**
	 * Update order status.
	 * 
	 * @param order the order to be updated
	 * @param status the new status
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderStatus(@Nonnull Order order, @Nonnull Order.Status status);

	/**
	 * Accept payment for order.
	 * 
	 * @param order the order to pay
	 * @param payment the payment
	 * @return order updated
	 */
	@Nonnull
	Order acceptOrderPayment(@Nonnull Order order, @Nonnull Payment payment);

	/**
	 * Accept refund for order.
	 * 
	 * @param order the order to refund
	 * @param refund the refund
	 * @return order updated
	 */
	@Nonnull
	Order acceptOrderRefund(@Nonnull Order order, @Nonnull Refund refund);

	/**
	 * Handle payment failure for order.
	 * 
	 * @param order the order to refund
	 * @param paymentFailureCause the payment failure cause
	 * @return order updated
	 */
	@Nonnull
	Order handleOrderPaymentFailure(@Nonnull Order order, @Nonnull String paymentFailureCause);

	/**
	 * Handle refund failure for order.
	 * 
	 * @param order the order to refund
	 * @param refundFailureCause the refund failure cause
	 * @return order updated
	 */
	@Nonnull
	Order handleOrderRefundFailure(@Nonnull Order order, @Nonnull String refundFailureCause);

}
