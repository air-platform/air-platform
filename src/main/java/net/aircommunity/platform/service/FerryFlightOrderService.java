package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.FerryFlightOrder;
import net.aircommunity.platform.model.domain.Order;

/**
 * FerryFlightOrder service.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightOrderService {

	/**
	 * Create a FerryFlightOrder.
	 * 
	 * @param userId the userId
	 * @param ferryFlightOrder the ferryFlightOrder to be created
	 * @return FerryFlightOrder created
	 */
	@Nonnull
	FerryFlightOrder createFerryFlightOrder(@Nonnull String userId, @Nonnull FerryFlightOrder ferryFlightOrder);

	/**
	 * Retrieves the specified FerryFlightOrder.
	 * 
	 * @param ferryFlightOrderId the ferryFlightOrderId
	 * @return the FerryFlightOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	FerryFlightOrder findFerryFlightOrder(@Nonnull String ferryFlightOrderId);

	/**
	 * Update a FerryFlightOrder.
	 * 
	 * @param ferryFlightOrderId the ferryFlightOrderId
	 * @param newFerryFlightOrder the FerryFlightOrder to be updated
	 * @return FerryFlightOrder created
	 */
	@Nonnull
	FerryFlightOrder updateFerryFlightOrder(@Nonnull String ferryFlightOrderId,
			@Nonnull FerryFlightOrder newFerryFlightOrder);

	/**
	 * Update FerryFlightOrder status
	 * 
	 * @param ferryFlightOrderId the ferryFlightOrderId
	 * @param status the FerryFlightOrder
	 * @return updated FerryFlightOrder
	 */
	@Nonnull
	FerryFlightOrder updateFerryFlightOrderStatus(@Nonnull String ferryFlightOrderId, @Nonnull Order.Status status);

	/**
	 * List all FerryFlightOrders by pagination filtered by userId and order status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlightOrders or empty
	 */
	@Nonnull
	Page<FerryFlightOrder> listUserFerryFlightOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize);

	/**
	 * List all FerryFlightOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlightOrders or empty
	 */
	@Nonnull
	default Page<FerryFlightOrder> listUserFerryFlightOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserFerryFlightOrders(userId, null, page, pageSize);
	}

	/**
	 * List all the FerryFlightOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlightOrders or empty
	 */
	Page<FerryFlightOrder> listTenantFerryFlightOrders(@Nonnull String tenantId, @Nullable Order.Status status,
			int page, int pageSize);

	/**
	 * List all the FerryFlightOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlightOrders or empty
	 */
	default Page<FerryFlightOrder> listTenantFerryFlightOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantFerryFlightOrders(tenantId, null, page, pageSize);
	}

	/**
	 * List all FerryFlightOrders by pagination filtered by order status.
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlightOrders or empty
	 */
	@Nonnull
	Page<FerryFlightOrder> listFerryFlightOrders(@Nullable Order.Status status, int page, int pageSize);

	/**
	 * List all FerryFlightOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of FerryFlightOrders or empty
	 */
	default @Nonnull Page<FerryFlightOrder> listFerryFlightOrders(int page, int pageSize) {
		return listFerryFlightOrders(null, page, pageSize);
	}

	/**
	 * Delete a FerryFlightOrder.
	 * 
	 * @param ferryFlightOrderId the ferryFlightOrderId
	 */
	void deleteFerryFlightOrder(@Nonnull String ferryFlightOrderId);

	/**
	 * Delete FerryFlightOrders.
	 * 
	 * @param userId the userId
	 */
	void deleteFerryFlightOrders(@Nonnull String userId);
}
