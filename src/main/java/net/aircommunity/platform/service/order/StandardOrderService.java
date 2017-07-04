package net.aircommunity.platform.service.order;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;

/**
 * Standard order service shared by all orders for a {@code User} (should be implemented by a concrete order service).
 *
 * @author Bin.Zhang
 */
public interface StandardOrderService<T extends Order> extends OrderService<T> {

	/**
	 * Create a order.
	 * 
	 * @param userId the userId
	 * @param orderId the orderId to be created
	 * @return order created
	 */
	@Nonnull
	T createOrder(@Nonnull String userId, @Nonnull T order);

	/**
	 * Update a order.
	 * 
	 * @param orderId the orderId
	 * @param order the order to be updated
	 * @return order created
	 */
	@Nonnull
	T updateOrder(@Nonnull String orderId, @Nonnull T order);

	/**
	 * Saves a given entity. Use the returned instance for further operations as the save operation might have changed
	 * the entity instance completely. The function accept entity as input and returns the saved entity
	 * 
	 * @return persister function
	 */
	Function<T, T> getOrderPersister();

	/**
	 * List all the orders placed on this tenant (but NOT DELETED). NOTE: it cannot be implemented without concrete
	 * order(because of charter order that may have many vendors as candidates). So, it cannot share this method with
	 * {@code CommonOrderService} (TENANT ONLY)
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of order or empty
	 */
	@Nonnull
	Page<T> listTenantOrders(@Nonnull String tenantId, @Nullable Order.Status status, int page, int pageSize);

	/**
	 * List all the orders placed on this tenant with all status (but NOT DELETED). (TENANT ONLY)
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of order or empty
	 */
	@Nonnull
	default Page<T> listTenantOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantOrders(tenantId, null, page, pageSize);
	}
}
