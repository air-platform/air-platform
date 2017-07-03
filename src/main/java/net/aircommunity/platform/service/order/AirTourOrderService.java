package net.aircommunity.platform.service.order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTourOrder;
import net.aircommunity.platform.model.domain.Order;

/**
 * AirTourOrder service.
 * 
 * @author Bin.Zhang
 */
public interface AirTourOrderService extends StandardOrderService<AirTourOrder> {

	/**
	 * Create a AirTourOrder.
	 * 
	 * @param userId the userId
	 * @param airTourOrder the airTourOrder to be created
	 * @return AirTourOrder created
	 */
	@Nonnull
	default AirTourOrder createAirTourOrder(@Nonnull String userId, @Nonnull AirTourOrder airTourOrder) {
		return createOrder(userId, airTourOrder);
	}

	/**
	 * Retrieves the specified AirTourOrder.
	 * 
	 * @param airTourOrderId the airTourOrderId
	 * @return the AirTourOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	default AirTourOrder findAirTourOrder(@Nonnull String airTourOrderId) {
		return findOrder(airTourOrderId);
	}

	/**
	 * Update a AirTourOrder.
	 * 
	 * @param airTourOrderId the airTourOrderId
	 * @param newAirTourOrder the AirTourOrder to be updated
	 * @return AirTourOrder created
	 */
	@Nonnull
	default AirTourOrder updateAirTourOrder(@Nonnull String airTourOrderId, @Nonnull AirTourOrder newAirTourOrder) {
		return updateOrder(airTourOrderId, newAirTourOrder);
	}

	/**
	 * Update AirTourOrder status
	 * 
	 * @param airTourOrderId the airTourOrderId
	 * @param status the AirTourOrder
	 * @return updated AirTourOrder
	 */
	@Nonnull
	default AirTourOrder updateAirTourOrderStatus(@Nonnull String airTourOrderId, @Nonnull Order.Status status) {
		return this.updateAirTourOrderStatus(airTourOrderId, status);
	}

	/**
	 * List all AirTourOrders by pagination filtered by userId and order status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	@Nonnull
	default Page<AirTourOrder> listUserAirTourOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize) {
		return this.listUserOrders(userId, status, page, pageSize);
	}

	/**
	 * List all AirTourOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	@Nonnull
	default Page<AirTourOrder> listUserAirTourOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrders(userId, page, pageSize);
	}

	/**
	 * List all the AirTourOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	default Page<AirTourOrder> listTenantAirTourOrders(@Nonnull String tenantId, @Nullable Order.Status status,
			int page, int pageSize) {
		return this.listTenantOrders(tenantId, status, page, pageSize);
	}

	/**
	 * List all the AirTourOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	default Page<AirTourOrder> listTenantAirTourOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantOrders(tenantId, page, pageSize);
	}

	/**
	 * List all AirTourOrders by pagination filtered by order status.
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	@Nonnull
	default Page<AirTourOrder> listAirTourOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	/**
	 * List all AirTourOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	default @Nonnull Page<AirTourOrder> listAirTourOrders(int page, int pageSize) {
		return listAllOrders(page, pageSize);
	}

	/**
	 * Delete a AirTourOrder.
	 * 
	 * @param airTourOrderId the airTourOrderId
	 */
	default void deleteAirTourOrder(@Nonnull String airTourOrderId) {
		deleteOrder(airTourOrderId);
	}

	/**
	 * Delete AirTourOrders.
	 * 
	 * @param userId the userId
	 */
	default void deleteAirTourOrders(@Nonnull String userId) {
		deleteOrders(userId);
	}
}
