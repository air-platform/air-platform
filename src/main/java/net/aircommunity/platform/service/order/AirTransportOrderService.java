package net.aircommunity.platform.service.order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.model.domain.Order;

/**
 * AirTransportOrder service.
 * 
 * @author Bin.Zhang
 */
public interface AirTransportOrderService extends StandardOrderService<AirTransportOrder> {

	/**
	 * Create a AirTransportOrder.
	 * 
	 * @param userId the userId
	 * @param airTransportOrder the airTransportOrder to be created
	 * @return AirTransportOrder created
	 */
	@Nonnull
	default AirTransportOrder createAirTransportOrder(@Nonnull String userId,
			@Nonnull AirTransportOrder airTransportOrder) {
		return createOrder(userId, airTransportOrder);
	}

	/**
	 * Retrieves the specified AirTransportOrder.
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 * @return the AirTransportOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	default AirTransportOrder findAirTransportOrder(@Nonnull String airTransportOrderId) {
		return findOrder(airTransportOrderId);
	}

	/**
	 * Update a AirTransportOrder.
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 * @param newAirTransportOrder the AirTransportOrder to be updated
	 * @return AirTransportOrder created
	 */
	@Nonnull
	default AirTransportOrder updateAirTransportOrder(@Nonnull String airTransportOrderId,
			@Nonnull AirTransportOrder newAirTransportOrder) {
		return updateOrder(airTransportOrderId, newAirTransportOrder);
	}

	/**
	 * Update AirTransportOrder status
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 * @param status the AirTransportOrder
	 * @return updated AirTransportOrder
	 */
	@Nonnull
	default AirTransportOrder updateAirTransportOrderStatus(@Nonnull String airTransportOrderId,
			@Nonnull Order.Status status) {
		return updateOrderStatus(airTransportOrderId, status);
	}

	/**
	 * List all AirTransportOrders by pagination filtered by userId and order status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	@Nonnull
	default Page<AirTransportOrder> listUserAirTransportOrders(@Nonnull String userId, @Nullable Order.Status status,
			int page, int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	/**
	 * List all AirTransportOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	@Nonnull
	default Page<AirTransportOrder> listUserAirTransportOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrders(userId, page, pageSize);
	}

	/**
	 * List all the AirTransportOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	default Page<AirTransportOrder> listTenantAirTransportOrders(@Nonnull String tenantId,
			@Nullable Order.Status status, int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	/**
	 * List all the AirTransportOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	default Page<AirTransportOrder> listTenantAirTransportOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantOrders(tenantId, page, pageSize);
	}

	/**
	 * List all AirTransportOrders by pagination filtered by order status.
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	@Nonnull
	default Page<AirTransportOrder> listAirTransportOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	/**
	 * List all AirTransportOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	default @Nonnull Page<AirTransportOrder> listAirTransportOrders(int page, int pageSize) {
		return listAllOrders(page, pageSize);
	}

	/**
	 * Delete a AirTransportOrder.
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 */
	default void deleteAirTransportOrder(@Nonnull String airTransportOrderId) {
		deleteOrder(airTransportOrderId);
	}

	/**
	 * Delete AirTransportOrders.
	 * 
	 * @param userId the userId
	 */
	default void deleteAirTransportOrders(@Nonnull String userId) {
		deleteOrders(userId);
	}
}
