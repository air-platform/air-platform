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
public interface AirTaxiOrderService {

	/**
	 * Create a AirTaxiOrder.
	 * 
	 * @param userId the userId
	 * @param airTaxiOrder the airTaxiOrder to be created
	 * @return AirTaxiOrder created
	 */
	@Nonnull
	AirTaxiOrder createAirTaxiOrder(@Nonnull String userId, @Nonnull AirTaxiOrder airTaxiOrder);

	/**
	 * Retrieves the specified AirTaxiOrder.
	 * 
	 * @param airTaxiOrderId the airTaxiOrderId
	 * @return the AirTaxiOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	AirTaxiOrder findAirTaxiOrder(@Nonnull String airTaxiOrderId);

	/**
	 * Update a AirTaxiOrder.
	 * 
	 * @param airTaxiOrderId the airTaxiOrderId
	 * @param newAirTaxiOrder the AirTaxiOrder to be updated
	 * @return AirTaxiOrder created
	 */
	@Nonnull
	AirTaxiOrder updateAirTaxiOrder(@Nonnull String airTaxiOrderId, @Nonnull AirTaxiOrder newAirTaxiOrder);

	/**
	 * Update AirTaxiOrder status
	 * 
	 * @param airTaxiOrderId the airTaxiOrderId
	 * @param status the AirTaxiOrder
	 * @return updated AirTaxiOrder
	 */
	@Nonnull
	AirTaxiOrder updateAirTaxiOrderStatus(@Nonnull String airTaxiOrderId, @Nonnull Order.Status status);

	/**
	 * List all AirTaxiOrders by pagination filtered by userId and order status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	@Nonnull
	Page<AirTaxiOrder> listUserAirTaxiOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize);

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
		return listUserAirTaxiOrders(userId, null, page, pageSize);
	}

	/**
	 * List all the AirTaxiOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	Page<AirTaxiOrder> listTenantAirTaxiOrders(@Nonnull String tenantId, @Nullable Order.Status status, int page,
			int pageSize);

	/**
	 * List all the AirTaxiOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	default Page<AirTaxiOrder> listTenantAirTaxiOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantAirTaxiOrders(tenantId, null, page, pageSize);
	}

	/**
	 * List all AirTaxiOrders by pagination filtered by order status.
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of AirTaxiOrders or empty
	 */
	@Nonnull
	Page<AirTaxiOrder> listAirTaxiOrders(@Nullable Order.Status status, int page, int pageSize);

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
	void deleteAirTaxiOrder(@Nonnull String airTaxiOrderId);

	/**
	 * Delete AirTaxiOrders.
	 * 
	 * @param userId the userId
	 */
	void deleteAirTaxiOrders(@Nonnull String userId);
}
