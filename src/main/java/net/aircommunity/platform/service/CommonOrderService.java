package net.aircommunity.platform.service;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

/**
 * Common order service list/query all {@code Order}s for a {@code User}.
 * 
 * @author Bin.Zhang
 */
public interface CommonOrderService {

	/**
	 * List all user orders with status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the page size
	 * @return
	 */
	Page<Order> listAllUserOrders(String userId, Order.Status status, int page, int pageSize);
}
