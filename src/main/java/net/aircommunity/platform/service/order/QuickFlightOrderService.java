package net.aircommunity.platform.service.order;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AircraftCandidate;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.QuickFlightOrder;

/**
 * QuickFlightOrder service.
 * 
 * @author Bin.Zhang
 */
public interface QuickFlightOrderService extends CandidateOrderService<AircraftCandidate, QuickFlightOrder> {

	/**
	 * Create a QuickFlightOrder.
	 * 
	 * @param userId the userId
	 * @param QuickFlightOrder the QuickFlightOrder to be created
	 * @return QuickFlightOrder created
	 */
	@Nonnull
	default QuickFlightOrder createQuickFlightOrder(@Nonnull String userId,
			@Nonnull QuickFlightOrder quickFlightOrder) {
		return createOrder(userId, quickFlightOrder);
	}

	/**
	 * Retrieves the specified QuickFlightOrder.
	 * 
	 * @param quickFlightOrderId the quickFlightOrderId
	 * @return the QuickFlightOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	default QuickFlightOrder findQuickFlightOrder(@Nonnull String quickFlightOrderId) {
		return findOrder(quickFlightOrderId);
	}

	/**
	 * Update a QuickFlightOrder.
	 * 
	 * @param quickFlightOrderId the quickFlightOrderId
	 * @param newQuickFlightOrder the QuickFlightOrder to be updated
	 * @return QuickFlightOrder created
	 */
	@Nonnull
	default QuickFlightOrder updateQuickFlightOrder(@Nonnull String quickFlightOrderId,
			@Nonnull QuickFlightOrder newQuickFlightOrder) {
		return updateOrder(quickFlightOrderId, newQuickFlightOrder);
	}

	/**
	 * Update QuickFlightOrder status
	 * 
	 * @param quickFlightOrderId the quickFlightOrderId
	 * @param status the QuickFlightOrder
	 * @return updated QuickFlightOrder
	 */
	@Nonnull
	default QuickFlightOrder updateQuickFlightOrderStatus(@Nonnull String quickFlightOrderId,
			@Nonnull Order.Status status) {
		return updateOrderStatus(quickFlightOrderId, status);
	}

	/**
	 * Update QuickFlightOrder to initiate a set aircraft candidates and a price(optional) by TENANT
	 * 
	 * @param orderId the orderId
	 * @param candidates the candidates
	 * @return updated QuickFlightOrder
	 */
	@Nonnull
	QuickFlightOrder initiateOrderCandidates(@Nonnull String orderId, @Nonnull Set<AircraftCandidate> candidates);

	/**
	 * Update QuickFlightOrder to promote aircraft candidates to user by platform ADMIN
	 * 
	 * @param orderId the orderId
	 * @param candidateIds the candidateIds to be offered
	 * @return updated QuickFlightOrder
	 */
	@Nonnull
	QuickFlightOrder promoteOrderCandidates(@Nonnull String orderId, @Nonnull Set<String> candidateIds);

	/**
	 * List all QuickFlightOrders by pagination filtered by userId and order status. (USER)
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of QuickFlightOrders or empty
	 */
	@Nonnull
	default Page<QuickFlightOrder> listUserQuickFlightOrders(@Nonnull String userId, @Nullable Order.Status status,
			int page, int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	/**
	 * List all QuickFlightOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of QuickFlightOrders or empty
	 */
	@Nonnull
	default Page<QuickFlightOrder> listUserQuickFlightOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrders(userId, page, pageSize);
	}

	/**
	 * List all unconfirmed (order status=CREATED) QuickFlightOrders by pagination filtered by tenantId. (TENANT)
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of QuickFlightOrders or empty
	 */
	@Nonnull
	Page<QuickFlightOrder> listTenantUnconfirmedOrders(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List all the QuickFlightOrders placed on this tenant. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of QuickFlightOrders or empty
	 */
	default Page<QuickFlightOrder> listTenantQuickFlightOrders(@Nonnull String tenantId, @Nullable Order.Status status,
			int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	/**
	 * List all the QuickFlightOrders placed on this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of QuickFlightOrders or empty
	 */
	default Page<QuickFlightOrder> listTenantQuickFlightOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantOrders(tenantId, page, pageSize);
	}

	/**
	 * List all QuickFlightOrders by pagination filtered by order status. (ADMIN)
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of QuickFlightOrders or empty
	 */
	@Nonnull
	default Page<QuickFlightOrder> listQuickFlightOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	/**
	 * List all QuickFlightOrders by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of QuickFlightOrders or empty
	 */
	default @Nonnull Page<QuickFlightOrder> listQuickFlightOrders(int page, int pageSize) {
		return listQuickFlightOrders(null, page, pageSize);
	}

	/**
	 * Delete a QuickFlightOrder.
	 * 
	 * @param quickFlightOrderId the quickFlightOrderId
	 */
	default void deleteQuickFlightOrder(@Nonnull String quickFlightOrderId) {
		deleteOrder(quickFlightOrderId);
	}

	/**
	 * Delete QuickFlightOrders.
	 * 
	 * @param userId the userId
	 */
	default void deleteQuickFlightOrders(@Nonnull String userId) {
		deleteOrders(userId);
	}
}
