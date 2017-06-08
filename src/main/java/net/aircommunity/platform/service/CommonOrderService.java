package net.aircommunity.platform.service;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

/**
 * Common order service list/query all {@code Order}s for a {@code User}.
 * 
 * @author Bin.Zhang
 */
public interface CommonOrderService {

	/**
	 * Find order (DELETED status considered as NOT FOUND)
	 * 
	 * @param orderId the orderId
	 * @return order found
	 */
	@Nonnull
	Order findOrder(@Nonnull String orderId);

	/**
	 * Save/Update order (NOTE: userId MUST BE already set in the order), XXX use this API with caution.
	 * 
	 * @param order the order to be saved
	 * @return order updated
	 */
	@Nonnull
	Order saveOrder(@Nonnull Order order);

	/**
	 * Update order status
	 * 
	 * @param orderId the orderId
	 * @param status the order status
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderStatus(@Nonnull String orderId, @Nonnull Order.Status status);

	/**
	 * Update order total price
	 * 
	 * @param orderId the orderId
	 * @param price the order price
	 * @return order updated
	 */
	@Nonnull
	Order updateOrderPrice(@Nonnull String orderId, double newPrice);

	/**
	 * Hard delete from DB
	 * 
	 * @param orderId the orderId
	 */
	void deleteOrder(@Nonnull String orderId);

	/**
	 * Hard delete from DB
	 * 
	 * @param userId the userId
	 */
	void deleteOrders(@Nonnull String userId);

	/**
	 * List all user orders with status. (for ADMIN)
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the page size
	 * @return a page of orders
	 */
	@Nonnull
	Page<Order> listAllOrders(@Nullable Order.Status status, int page, int pageSize);

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
	Page<Order> listAllUserOrders(@Nonnull String userId, @Nullable Order.Status status, int page, int pageSize);

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
