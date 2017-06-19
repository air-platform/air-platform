package net.aircommunity.platform.service;

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
public interface AirTransportOrderService {

	/**
	 * Create a AirTransportOrder.
	 * 
	 * @param userId the userId
	 * @param airTransportOrder the airTransportOrder to be created
	 * @return AirTransportOrder created
	 */
	@Nonnull
	AirTransportOrder createAirTransportOrder(@Nonnull String userId, @Nonnull AirTransportOrder airTransportOrder);

	/**
	 * Retrieves the specified AirTransportOrder.
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 * @return the AirTransportOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	AirTransportOrder findAirTransportOrder(@Nonnull String airTransportOrderId);

	/**
	 * Update a AirTransportOrder.
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 * @param newAirTransportOrder the AirTransportOrder to be updated
	 * @return AirTransportOrder created
	 */
	@Nonnull
	AirTransportOrder updateAirTransportOrder(@Nonnull String airTransportOrderId,
			@Nonnull AirTransportOrder newAirTransportOrder);

	/**
	 * Update AirTransportOrder status
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 * @param status the AirTransportOrder
	 * @return updated AirTransportOrder
	 */
	@Nonnull
	AirTransportOrder updateAirTransportOrderStatus(@Nonnull String airTransportOrderId, @Nonnull Order.Status status);

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
	Page<AirTransportOrder> listUserAirTransportOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize);

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
		return listUserAirTransportOrders(userId, null, page, pageSize);
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
	Page<AirTransportOrder> listTenantAirTransportOrders(@Nonnull String tenantId, @Nullable Order.Status status,
			int page, int pageSize);

	/**
	 * List all the AirTransportOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	default Page<AirTransportOrder> listTenantAirTransportOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantAirTransportOrders(tenantId, null, page, pageSize);
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
	Page<AirTransportOrder> listAirTransportOrders(@Nullable Order.Status status, int page, int pageSize);

	/**
	 * List all AirTransportOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTransportOrders or empty
	 */
	default @Nonnull Page<AirTransportOrder> listAirTransportOrders(int page, int pageSize) {
		return listAirTransportOrders(null, page, pageSize);
	}

	/**
	 * Delete a AirTransportOrder.
	 * 
	 * @param airTransportOrderId the airTransportOrderId
	 */
	void deleteAirTransportOrder(@Nonnull String airTransportOrderId);

	/**
	 * Delete AirTransportOrders.
	 * 
	 * @param userId the userId
	 */
	void deleteAirTransportOrders(@Nonnull String userId);
}
