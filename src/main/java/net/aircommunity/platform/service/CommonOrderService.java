package net.aircommunity.platform.service;

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
	 * Find order
	 * 
	 * @param orderId the orderId
	 * @return order found
	 */
	@Nonnull
	Order findOrder(@Nonnull String orderId);

	/**
	 * List all user orders with status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the page size
	 * @return
	 */
	@Nonnull
	Page<Order> listAllUserOrders(@Nonnull String userId, @Nullable Order.Status status, int page, int pageSize);

	/**
	 * Mark status as cancelled.
	 * 
	 * @param orderId the orderId
	 * @return order status updated
	 */
	@Nonnull
	Order cancelOrder(@Nonnull String orderId);

	/**
	 * Mark status as deleted.
	 * 
	 * @param orderId the orderId
	 * @return order status updated
	 */
	@Nonnull
	Order deleteOrder(@Nonnull String orderId);

}
