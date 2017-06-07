package net.aircommunity.platform.service;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.AirTourOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * AirTourOrder service.
 * 
 * @author Bin.Zhang
 */
public interface AirTourOrderService {

	/**
	 * Create a AirTourOrder.
	 * 
	 * @param userId the userId
	 * @param airTourOrder the airTourOrder to be created
	 * @return AirTourOrder created
	 */
	@Nonnull
	AirTourOrder createAirTourOrder(@Nonnull String userId, @Nonnull AirTourOrder airTourOrder);

	/**
	 * Retrieves the specified AirTourOrder.
	 * 
	 * @param airTourOrderId the airTourOrderId
	 * @return the AirTourOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	AirTourOrder findAirTourOrder(@Nonnull String airTourOrderId);

	/**
	 * Update a AirTourOrder.
	 * 
	 * @param airTourOrderId the airTourOrderId
	 * @param newAirTourOrder the AirTourOrder to be updated
	 * @return AirTourOrder created
	 */
	@Nonnull
	AirTourOrder updateAirTourOrder(@Nonnull String airTourOrderId, @Nonnull AirTourOrder newAirTourOrder);

	/**
	 * Update AirTourOrder status
	 * 
	 * @param airTourOrderId the airTourOrderId
	 * @param status the AirTourOrder
	 * @return updated AirTourOrder
	 */
	@Nonnull
	AirTourOrder updateAirTourOrderStatus(@Nonnull String airTourOrderId, @Nonnull Order.Status status);

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
	Page<AirTourOrder> listUserAirTourOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize);

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
		return listUserAirTourOrders(userId, null, page, pageSize);
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
	Page<AirTourOrder> listTenantAirTourOrders(@Nonnull String tenantId, @Nullable Order.Status status, int page,
			int pageSize);

	/**
	 * List all the AirTourOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	default Page<AirTourOrder> listTenantAirTourOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantAirTourOrders(tenantId, null, page, pageSize);
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
	Page<AirTourOrder> listAirTourOrders(@Nullable Order.Status status, int page, int pageSize);

	/**
	 * List all AirTourOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTourOrders or empty
	 */
	default @Nonnull Page<AirTourOrder> listAirTourOrders(int page, int pageSize) {
		return listAirTourOrders(null, page, pageSize);
	}

	/**
	 * Delete a AirTourOrder.
	 * 
	 * @param airTourOrderId the airTourOrderId
	 */
	void deleteAirTourOrder(@Nonnull String airTourOrderId);

	/**
	 * Delete AirTourOrders.
	 * 
	 * @param userId the userId
	 */
	void deleteAirTourOrders(@Nonnull String userId);
}
