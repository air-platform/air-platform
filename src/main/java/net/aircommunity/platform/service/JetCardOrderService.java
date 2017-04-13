package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.JetCardOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

/**
 * JetCardOrder service.
 * 
 * @author Bin.Zhang
 */
public interface JetCardOrderService {

	/**
	 * Create a JetCardOrder.
	 * 
	 * @param userId the userId
	 * @param jetCardOrder the jetCardOrder to be created
	 * @return JetCardOrder created
	 */
	@Nonnull
	JetCardOrder createJetCardOrder(@Nonnull String userId, @Nonnull JetCardOrder jetCardOrder);

	/**
	 * Retrieves the specified JetCardOrder.
	 * 
	 * @param jetCardOrderId the jetCardOrderId
	 * @return the JetCardOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	JetCardOrder findJetCardOrder(@Nonnull String jetCardOrderId);

	/**
	 * Update a JetCardOrder.
	 * 
	 * @param jetCardOrderId the jetCardOrderId
	 * @param newJetCardOrder the JetCardOrder to be updated
	 * @return JetCardOrder created
	 */
	@Nonnull
	JetCardOrder updateJetCardOrder(@Nonnull String jetCardOrderId, @Nonnull JetCardOrder newJetCardOrder);

	/**
	 * Update JetCardOrder status
	 * 
	 * @param jetCardOrderId the jetCardOrderId
	 * @param status the JetCardOrder
	 * @return updated JetCardOrder
	 */
	@Nonnull
	JetCardOrder updateJetCardOrderStatus(@Nonnull String jetCardOrderId, @Nonnull Order.Status status);

	/**
	 * List all JetCardOrders by pagination filtered by userId and order status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCardOrders or empty
	 */
	@Nonnull
	Page<JetCardOrder> listUserJetCardOrders(@Nonnull String userId, Order.Status status, int page, int pageSize);

	/**
	 * List all JetCardOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCardOrders or empty
	 */
	@Nonnull
	default Page<JetCardOrder> listUserJetCardOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserJetCardOrders(userId, null, page, pageSize);
	}

	/**
	 * List all the JetCardOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCardOrders or empty
	 */
	Page<JetCardOrder> listTenantJetCardOrders(@Nonnull String tenantId, Order.Status status, int page, int pageSize);

	/**
	 * List all the JetCardOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCardOrders or empty
	 */
	default Page<JetCardOrder> listTenantJetCardOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantJetCardOrders(tenantId, null, page, pageSize);
	}

	/**
	 * List all JetCardOrders by pagination filtered by order status.
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCardOrders or empty
	 */
	@Nonnull
	Page<JetCardOrder> listJetCardOrders(Order.Status status, int page, int pageSize);

	/**
	 * List all JetCardOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetCardOrders or empty
	 */
	default @Nonnull Page<JetCardOrder> listJetCardOrders(int page, int pageSize) {
		return listJetCardOrders(null, page, pageSize);
	}

	/**
	 * Delete a JetCardOrder.
	 * 
	 * @param jetCardOrderId the jetCardOrderId
	 */
	void deleteJetCardOrder(@Nonnull String jetCardOrderId);

	/**
	 * Delete JetCardOrders.
	 * 
	 * @param userId the userId
	 */
	void deleteJetCardOrders(@Nonnull String userId);
}
