package net.aircommunity.platform.service.spi;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;

/**
 * Order processor SPI.
 * 
 * @author Bin.Zhang
 * @param <T> the type of order
 */
public interface OrderProcessor<T extends Order> {

	/**
	 * Find an order by order id
	 * 
	 * @param orderId the order id
	 * @return the order found or null if none
	 */
	@Nonnull
	T findByOrderId(@Nonnull String orderId);

	/**
	 * Search by orderNo with fuzzy match.
	 * 
	 * @param orderNo the orderNo
	 * @return a list of orders or empty if none
	 */
	@Nonnull
	List<T> searchOrder(@Nonnull String orderNo);

	/**
	 * Find orders by order ids
	 * 
	 * @param orderIds the order id list
	 * @return the order list or empty if none
	 */
	@Nonnull
	List<T> findOrders(@Nonnull List<String> orderIds);

	/**
	 * Find an order by order number
	 * 
	 * @param orderId the order number
	 * @return the order found or null if none
	 */
	@Nonnull
	T findByOrderNo(@Nonnull String orderNo);

	/**
	 * Save or update an order.
	 * 
	 * @param order the order
	 * @return order updated
	 */
	@Nonnull
	T saveOrder(@Nonnull T order);

	/**
	 * List all order with status for a user except the orders in {@code Order.Status.DELETED} status. (USER)
	 * 
	 * @param userId the user id
	 * @param page the start page (from 1)
	 * @param pageSize the page size
	 * @return a page of user order or empty if none
	 */
	@Nonnull
	Page<T> listUserOrders(@Nonnull String userId, @Nonnull Order.Status status, int page, int pageSize);

	/**
	 * List all order in requested statuses for a user except the orders in {@code Order.Status.DELETED} status. (USER)
	 * 
	 * @param userId the user id
	 * @param statuses the order statuses
	 * @param page the start page (from 1)
	 * @param pageSize the page size
	 * @return a page of user order or empty if none
	 */
	@Nonnull
	Page<T> listUserOrders(@Nonnull String userId, @Nonnull Set<Order.Status> statuses, int page, int pageSize);

	/**
	 * List all order in all statuses for a user except the orders in {@code Order.Status.DELETED} status within recent
	 * {@code days}. (USER)
	 * 
	 * @param userId the user id
	 * @param days the days
	 * @param page the start page (from 1)
	 * @param pageSize the page size
	 * @return a page of user order or empty if none
	 */
	@Nonnull
	Page<T> listUserOrdersOfRecentDays(@Nonnull String userId, int days, int page, int pageSize);

	/**
	 * List all order in the status for a user for the given type included the orders in {@code Order.Status.DELETED}
	 * status (ADMIN)
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param type the order type
	 * @param page the start page (from 1)
	 * @param pageSize the page size
	 * @return a page of user order or empty if none
	 */
	@Nonnull
	Page<T> listAllUserOrders(@Nonnull String userId, @Nullable Order.Status status, @Nullable Product.Type type,
			int page, int pageSize);

	/**
	 * List all order in the status for a user for the given type included the orders in {@code Order.Status.DELETED}
	 * status. (ADMIN)
	 * 
	 * @param type the order type
	 * @param page the start page (from 1)
	 * @param pageSize the page size
	 * @return a page of user order or empty if none
	 */
	@Nonnull
	Page<T> listAllOrders(@Nullable Order.Status status, @Nullable Product.Type type, int page, int pageSize);

}
