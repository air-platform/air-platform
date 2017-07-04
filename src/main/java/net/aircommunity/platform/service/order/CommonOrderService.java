package net.aircommunity.platform.service.order;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;

/**
 * Common order service list/query all {@code Order}s for a {@code User}.
 * 
 * @author Bin.Zhang
 */
public interface CommonOrderService extends OrderService<Order> {

	/**
	 * Test if there is any order for this user (among all order types).
	 * 
	 * @param userId the user id
	 * @return true if exists, false otherwise
	 */
	boolean existsOrderForUser(@Nonnull String userId);

	@Nonnull
	Order updateOrderStatus(@Nonnull Order order, @Nonnull Order.Status status);

	@Nonnull
	Order payOrder(@Nonnull Order order, @Nonnull Payment payment);

	@Nonnull
	Order handleOrderPaymentFailure(@Nonnull Order order, @Nonnull String paymentFailureCause);

	@Nonnull
	Order handleOrderRefundFailure(@Nonnull Order order, @Nonnull String refundFailureCause);

}
