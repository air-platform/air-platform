package net.aircommunity.platform.service.order;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;

/**
 * Basic order service shared by all orders for a {@code User} (can be implemented as common order service not necessary
 * a concrete order).
 *
 * @author Bin.Zhang
 */
public interface OrderService<T extends Order> {

	/**
	 * Find order (DELETED status considered as NOT FOUND)
	 * 
	 * @param orderId the orderId
	 * @return order found
	 */
	@Nonnull
	T findOrder(@Nonnull String orderId);

	/**
	 * Update order status.
	 * 
	 * @param orderId the orderId
	 * @param status the status to be updated to
	 * @return order updated
	 */
	@Nonnull
	T updateOrderStatus(@Nonnull String orderId, @Nonnull Order.Status status);

	/**
	 * Update order total price
	 * 
	 * @param orderId the orderId
	 * @param newTotalAmount the order totalAmount
	 * @return order updated
	 */
	@Nonnull
	T updateOrderTotalAmount(@Nonnull String orderId, BigDecimal newTotalAmount);

	/**
	 * Update order as commented.
	 * 
	 * @param orderId the orderId
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderCommented(@Nonnull String orderId);

	// Refund

	@Nonnull
	Order initiateOrderRefund(@Nonnull String orderId, @Nonnull BigDecimal refundAmount, @Nonnull String refundReason);

	@Nonnull
	Order acceptOrderRefund(@Nonnull String orderId, @Nonnull BigDecimal refundAmount);

	@Nonnull
	Order acceptOrderRefund(@Nonnull String orderId, @Nonnull Refund refund);

	@Nonnull
	Order rejectOrderRefund(@Nonnull String orderId, @Nonnull String rejectReason);

	//

	@Nonnull
	Order cancelOrder(@Nonnull String orderId, @Nonnull String cancelReason);

	@Nonnull
	Order closeOrder(@Nonnull String orderId, @Nonnull String closeReason);

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
	 * List all user orders with status. (for ADMIN)
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of orders
	 */
	@Nonnull
	default Page<T> listAllOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, null, page, pageSize);
	}

	@Nonnull
	default Page<T> listAllOrders(int page, int pageSize) {
		return listAllOrders(null, page, pageSize);
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
	Page<T> listAllOrders(@Nullable Order.Status status, @Nullable Product.Type type, int page, int pageSize);

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
	default Page<T> listAllUserOrders(@Nonnull String userId, @Nullable Order.Status status, int page, int pageSize) {
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
	Page<T> listAllUserOrders(@Nonnull String userId, @Nullable Order.Status status, @Nullable Product.Type type,
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
	Page<T> listUserOrders(@Nonnull String userId, @Nullable Order.Status status, int page, int pageSize);

	@Nonnull
	default Page<T> listUserOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrders(userId, null, page, pageSize);
	}

	/**
	 * Soft delete just marked as DELETED
	 * 
	 * @param orderId the orderId
	 * @return the order deleted
	 */
	@Nullable
	T softDeleteOrder(@Nonnull String orderId);

	/**
	 * Hard delete from DB
	 * 
	 * @param orderId the orderId
	 * @return the order deleted
	 */
	@Nullable
	T deleteOrder(@Nonnull String orderId);

	/**
	 * Hard delete user orders from DB (deletion will fail, if user is commented on some products)
	 * 
	 * @param userId the userId
	 */
	void deleteOrders(@Nonnull String userId);

}
