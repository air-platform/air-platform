package net.aircommunity.platform.service.order;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PaymentRequest;
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

	/**
	 * Find the order payment info by tradeNo.
	 * 
	 * @param paymentMethod the payment method
	 * @param tradeNo the tradeNo of a payment system
	 * @return payment
	 */
	@Nonnull
	Optional<Payment> findPaymentByTradeNo(@Nonnull Payment.Method paymentMethod, @Nonnull String tradeNo);

	/**
	 * Find order (DELETED status considered as NOT FOUND)
	 * 
	 * @param orderNo the orderNo
	 * @return order found
	 */
	@Nonnull
	Order findByOrderNo(String orderNo);

	/**
	 * Find order (DELETED status considered as NOT FOUND) will not throw exception if not found.
	 * 
	 * @param orderNo the orderNo
	 * @return order present or absent
	 */
	@Nonnull
	Optional<Order> lookupByOrderNo(String orderNo);

	@Nonnull
	Order updateOrderStatus(@Nonnull Order order, @Nonnull Order.Status status);

	@Nonnull
	PaymentRequest requestOrderPayment(@Nonnull String orderId, @Nonnull Payment.Method paymentMethod);

	@Nonnull
	Order payOrder(@Nonnull String orderId, @Nonnull Payment payment);

	@Nonnull
	Order payOrder(@Nonnull Order order, @Nonnull Payment payment);

	@Nonnull
	Order handleOrderPaymentFailure(@Nonnull Order order, @Nonnull String paymentFailureCause);

	@Nonnull
	Order requestOrderRefund(@Nonnull String orderId, @Nonnull String refundReason);

	@Nonnull
	Order handleOrderRefundFailure(@Nonnull String orderId, @Nonnull String refundFailureCause);

	@Nonnull
	Order handleOrderRefundFailure(@Nonnull Order order, @Nonnull String refundFailureCause);

	/**
	 * Hard delete user orders from DB, and also user comments on some products, included everything related to this
	 * user.
	 * 
	 * @param userId the userId
	 */
	void purgeOrders(@Nonnull String userId);

	// order within 6 months (days <= 6*30)
	Page<Order> listUserOrders(@Nonnull String userId, int days, int page, int pageSize);

	@Nonnull
	Page<Order> listUserOrdersInStatuses(@Nonnull String userId, @Nonnull Set<Order.Status> statuses, int page,
			int pageSize);

	/**
	 * Orders Pending NOT IN (REFUNDED, FINISHED, CLOSED, CANCELLED)
	 */
	@Nonnull
	default Page<Order> listUserPendingOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrdersInStatuses(userId, Order.PENDING_STATUSES, page, pageSize);
	}

	/**
	 * Orders Finished (REFUNDED, FINISHED, CLOSED, CANCELLED)
	 */
	@Nonnull
	default Page<Order> listUserFinishedOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrdersInStatuses(userId, Order.COMPLETED_STATUSES, page, pageSize);
	}

	/**
	 * Orders Refund (REFUND_REQUESTED, REFUNDING)
	 */
	@Nonnull
	default Page<Order> listUserRefundOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrdersInStatuses(userId, Order.REFUND_STATUSES, page, pageSize);
	}

	@Nonnull
	Page<Order> listUserOrdersNotInStatuses(@Nonnull String userId, @Nonnull Set<Order.Status> statuses, int page,
			int pageSize);

}
