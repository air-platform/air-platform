package net.aircommunity.platform.service.order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTaxiOrder;
import net.aircommunity.platform.model.domain.Order;

/**
 * AirTaxiOrder service.
 * 
 * @author Bin.Zhang
 */
public interface AirTaxiOrderService extends StandardOrderService<AirTaxiOrder> {

	/**
	 * Create a AirTaxiOrder.
	 * 
	 * @param userId the userId
	 * @param airTaxiOrder the airTaxiOrder to be created
	 * @return AirTaxiOrder created
	 */
	@Nonnull
	default AirTaxiOrder createAirTaxiOrder(@Nonnull String userId, @Nonnull AirTaxiOrder airTaxiOrder) {
		return createOrder(userId, airTaxiOrder);
	}

	/**
	 * Retrieves the specified AirTaxiOrder.
	 * 
	 * @param airTaxiOrderId the airTaxiOrderId
	 * @return the AirTaxiOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	default AirTaxiOrder findAirTaxiOrder(@Nonnull String airTaxiOrderId) {
		return findOrder(airTaxiOrderId);
	}

	/**
	 * Update a AirTaxiOrder.
	 * 
	 * @param airTaxiOrderId the airTaxiOrderId
	 * @param newAirTaxiOrder the AirTaxiOrder to be updated
	 * @return AirTaxiOrder created
	 */
	@Nonnull
	default AirTaxiOrder updateAirTaxiOrder(@Nonnull String airTaxiOrderId, @Nonnull AirTaxiOrder newAirTaxiOrder) {
		return updateOrder(airTaxiOrderId, newAirTaxiOrder);
	}

	/**
	 * Update AirTaxiOrder status
	 * 
	 * @param airTaxiOrderId the airTaxiOrderId
	 * @param status the AirTaxiOrder
	 * @return updated AirTaxiOrder
	 */
	@Nonnull
	default AirTaxiOrder updateAirTaxiOrderStatus(@Nonnull String airTaxiOrderId, @Nonnull Order.Status status) {
		return updateOrderStatus(airTaxiOrderId, status);
	}

	/**
	 * List all AirTaxiOrders by pagination filtered by userId and order status. (USER)
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	@Nonnull
	default Page<AirTaxiOrder> listUserAirTaxiOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	/**
	 * List all AirTaxiOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	@Nonnull
	default Page<AirTaxiOrder> listUserAirTaxiOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrders(userId, page, pageSize);
	}

	/**
	 * List all the AirTaxiOrders placed on this tenant. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	default Page<AirTaxiOrder> listTenantAirTaxiOrders(@Nonnull String tenantId, @Nullable Order.Status status,
			int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	/**
	 * List all the AirTaxiOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	default Page<AirTaxiOrder> listTenantAirTaxiOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantOrders(tenantId, page, pageSize);
	}

	/**
	 * List all AirTaxiOrders by pagination filtered by order status. (ADMIN)
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	@Nonnull
	default Page<AirTaxiOrder> listAirTaxiOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	/**
	 * List all AirTaxiOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	default @Nonnull Page<AirTaxiOrder> listAirTaxiOrders(int page, int pageSize) {
		return listAirTaxiOrders(null, page, pageSize);
	}

	/**
	 * Delete a AirTaxiOrder.
	 * 
	 * @param airTaxiOrderId the airTaxiOrderId
	 */
	default void deleteAirTaxiOrder(@Nonnull String airTaxiOrderId) {
		deleteOrder(airTaxiOrderId);
	}

	/**
	 * Delete AirTaxiOrders.
	 * 
	 * @param userId the userId
	 */
	default void deleteAirTaxiOrders(@Nonnull String userId) {
		deleteOrders(userId);
	}
}
