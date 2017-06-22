package net.aircommunity.platform.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.RefundRequest;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Refund;

/**
 * Common order service list/query all {@code Order}s for a {@code User}.
 * 
 * @author Bin.Zhang
 */
public interface CommonOrderService {

	/**
	 * Test if there is any order for this user.
	 * 
	 * @param userId the user id
	 * @return true if exists, false othewise
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

	/**
	 * Find order (DELETED status considered as NOT FOUND)
	 * 
	 * @param orderId the orderId
	 * @return order found
	 */
	@Nonnull
	Order findOrder(@Nonnull String orderId);

	/**
	 * Update order as commented.
	 * 
	 * @param orderId the orderId
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderCommented(@Nonnull String orderId);

	/**
	 * Update order status
	 * 
	 * @param orderId the orderId
	 * @param status the order status
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderStatus(@Nonnull String orderId, @Nonnull Order.Status status);

	@Nonnull
	PaymentRequest requestOrderPayment(@Nonnull String orderId, @Nonnull Payment.Method paymentMethod);

	@Nonnull
	Order payOrder(@Nonnull String orderId, @Nonnull Payment payment);

	@Nonnull
	Order requestOrderRefund(@Nonnull String orderId, @Nonnull RefundRequest request);

	@Nonnull
	Order acceptOrderRefund(@Nonnull String orderId, @Nonnull BigDecimal refundAmount);

	@Nonnull
	Order acceptOrderRefund(@Nonnull String orderId, @Nonnull Refund refund);

	@Nonnull
	Order rejectOrderRefund(@Nonnull String orderId, @Nonnull String rejectReason);

	@Nonnull
	Order handleOrderRefundFailure(@Nonnull String orderId, @Nonnull String refundFailureCause);

	@Nonnull
	Order cancelOrder(@Nonnull String orderId, @Nonnull String cancelReason);

	/**
	 * Update order status
	 * 
	 * @param orderNo the orderNo
	 * @param status the order status
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderStatusByOrderNo(@Nonnull String orderNo, @Nonnull Order.Status status);

	/**
	 * Update order total price
	 * 
	 * @param orderId the orderId
	 * @param newTotalAmount the order totalAmount
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderTotalAmount(@Nonnull String orderId, BigDecimal newTotalAmount);

	/**
	 * Hard delete from DB
	 * 
	 * @param orderId the orderId
	 */
	void deleteOrder(@Nonnull String orderId);

	/**
	 * Hard delete user orders from DB (deletion will fail, if user is commented on some products)
	 * 
	 * @param userId the userId
	 */
	void deleteOrders(@Nonnull String userId);

	/**
	 * Hard delete user orders from DB, and also user comments on some products, included everything related to this
	 * user.
	 * 
	 * @param userId the userId
	 */
	void purgeOrders(@Nonnull String userId);

	/**
	 * List all user orders with status. (for ADMIN)
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of orders
	 */
	@Nonnull
	default Page<Order> listAllOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, null, page, pageSize);
	}

	/**
	 * List all user orders with status and type. (for ADMIN)
	 * 
	 * @param status the order status
	 * @param type the order type
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of orders
	 */
	@Nonnull
	Page<Order> listAllOrders(@Nullable Order.Status status, @Nullable Order.Type type, int page, int pageSize);

	/**
	 * List all orders of a user with all status. (for ADMIN)
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of orders
	 */
	@Nonnull
	default Page<Order> listAllUserOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize) {
		return listAllUserOrders(userId, status, null, page, pageSize);
	}

	/**
	 * List all orders of a user with all status and type. (for ADMIN)
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param type the order type
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of orders
	 */
	@Nonnull
	Page<Order> listAllUserOrders(@Nonnull String userId, @Nullable Order.Status status, @Nullable Order.Type type,
			int page, int pageSize);

	/**
	 * List all orders of a user without status in DELETED. (for USER)
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of orders
	 */
	@Nonnull
	Page<Order> listUserOrders(@Nonnull String userId, @Nullable Order.Status status, int page, int pageSize);

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
