package net.aircommunity.platform.service.order;

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
public interface JetTravelOrderService extends StandardOrderService<JetTravelOrder> {

	/**
	 * Create a JetTravelOrder.
	 * 
	 * @param userId the userId
	 * @param jetTravelOrder the jetTravelOrder to be created
	 * @return JetTravelOrder created
	 */
	@Nonnull
	default JetTravelOrder createJetTravelOrder(@Nonnull String userId, @Nonnull JetTravelOrder jetTravelOrder) {
		return createOrder(userId, jetTravelOrder);
	}

	/**
	 * Retrieves the specified JetTravelOrder.
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 * @return the JetTravelOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	default JetTravelOrder findJetTravelOrder(@Nonnull String jetTravelOrderId) {
		return findOrder(jetTravelOrderId);
	}

	/**
	 * Update a JetTravelOrder.
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 * @param newJetTravelOrder the JetTravelOrder to be updated
	 * @return JetTravelOrder created
	 */
	@Nonnull
	default JetTravelOrder updateJetTravelOrder(@Nonnull String jetTravelOrderId,
			@Nonnull JetTravelOrder newJetTravelOrder) {
		return updateOrder(jetTravelOrderId, newJetTravelOrder);
	}

	/**
	 * Update JetTravelOrder status
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 * @param status the JetTravelOrder
	 * @return updated JetTravelOrder
	 */
	@Nonnull
	default JetTravelOrder updateJetTravelOrderStatus(@Nonnull String jetTravelOrderId, @Nonnull Order.Status status) {
		return updateOrderStatus(jetTravelOrderId, status);
	}

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
	default Page<JetTravelOrder> listUserJetTravelOrders(@Nonnull String userId, @Nullable Order.Status status,
			int page, int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

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
		return listUserOrders(userId, page, pageSize);
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
	default Page<JetTravelOrder> listTenantJetTravelOrders(@Nonnull String tenantId, @Nullable Order.Status status,
			int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	/**
	 * List all the JetTravelOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	default Page<JetTravelOrder> listTenantJetTravelOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantOrders(tenantId, page, pageSize);
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
	default Page<JetTravelOrder> listJetTravelOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	/**
	 * List all JetTravelOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of JetTravelOrders or empty
	 */
	default @Nonnull Page<JetTravelOrder> listJetTravelOrders(int page, int pageSize) {
		return listAllOrders(page, pageSize);
	}

	/**
	 * Delete a JetTravelOrder.
	 * 
	 * @param jetTravelOrderId the jetTravelOrderId
	 */
	default void deleteJetTravelOrder(@Nonnull String jetTravelOrderId) {
		deleteOrder(jetTravelOrderId);
	}

	/**
	 * Delete JetTravelOrders.
	 * 
	 * @param userId the userId
	 */
	default void deleteJetTravelOrders(@Nonnull String userId) {
		deleteOrders(userId);
	}
}
