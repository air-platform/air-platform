package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.model.domain.Order;

/**
 * JetTravelOrder service.
 * 
 * @author Bin.Zhang
 */
public interface JetTravelOrderService {

	/**
	 * Create a JetTravelOrder.
	 * 
	 * @param userId the userId
	 * @param jetTravelOrder the jetTravelOrder to be created
	 * @return JetTravelOrder created
	 */
	@Nonnull
	JetTravelOrder createJetTravelOrder(@Nonnull String userId, @Nonnull JetTravelOrder jetTravelOrder);

	/**
	 * Retrieves the specified JetTravelOrder.
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 * @return the JetTravelOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	JetTravelOrder findJetTravelOrder(@Nonnull String jetTravelOrderId);

	/**
	 * Update a JetTravelOrder.
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 * @param newJetTravelOrder the JetTravelOrder to be updated
	 * @return JetTravelOrder created
	 */
	@Nonnull
	JetTravelOrder updateJetTravelOrder(@Nonnull String jetTravelOrderId, @Nonnull JetTravelOrder newJetTravelOrder);

	/**
	 * Update JetTravelOrder status
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 * @param status the JetTravelOrder
	 * @return updated JetTravelOrder
	 */
	@Nonnull
	JetTravelOrder updateJetTravelOrderStatus(@Nonnull String jetTravelOrderId, @Nonnull Order.Status status);

	/**
	 * List all JetTravelOrders by pagination filtered by userId and order status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	@Nonnull
	Page<JetTravelOrder> listUserJetTravelOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize);

	/**
	 * List all JetTravelOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	@Nonnull
	default Page<JetTravelOrder> listUserJetTravelOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserJetTravelOrders(userId, null, page, pageSize);
	}

	/**
	 * List all the JetTravelOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	Page<JetTravelOrder> listTenantJetTravelOrders(@Nonnull String tenantId, @Nullable Order.Status status, int page,
			int pageSize);

	/**
	 * List all the JetTravelOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	default Page<JetTravelOrder> listTenantJetTravelOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantJetTravelOrders(tenantId, null, page, pageSize);
	}

	/**
	 * List all JetTravelOrders by pagination filtered by order status.
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	@Nonnull
	Page<JetTravelOrder> listJetTravelOrders(@Nullable Order.Status status, int page, int pageSize);

	/**
	 * List all JetTravelOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	default @Nonnull Page<JetTravelOrder> listJetTravelOrders(int page, int pageSize) {
		return listJetTravelOrders(null, page, pageSize);
	}

	/**
	 * Delete a JetTravelOrder.
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 */
	void deleteJetTravelOrder(@Nonnull String jetTravelOrderId);

	/**
	 * Delete JetTravelOrders.
	 * 
	 * @param userId the userId
	 */
	void deleteJetTravelOrders(@Nonnull String userId);
}
