package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;

/**
 * CharterOrder service.
 * 
 * @author Bin.Zhang
 */
public interface CharterOrderService {

	/**
	 * Create a CharterOrder.
	 * 
	 * @param userId the userId
	 * @param fleetCandidates the fleetCandidates Set<FleetCandidate> fleetCandidates,
	 * @param charterOrder the charterOrder to be created
	 * @return CharterOrder created
	 */
	@Nonnull
	CharterOrder createCharterOrder(@Nonnull String userId, @Nonnull CharterOrder charterOrder);

	/**
	 * Retrieves the specified CharterOrder.
	 * 
	 * @param charterOrderId the charterOrderId
	 * @return the CharterOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	CharterOrder findCharterOrder(@Nonnull String charterOrderId);

	/**
	 * Retrieves the specified CharterOrder by orderNo.
	 * 
	 * @param orderNo the orderNo
	 * @return the CharterOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	CharterOrder findCharterOrderByOrderNo(@Nonnull String orderNo);

	/**
	 * Update a CharterOrder.
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param newCharterOrder the CharterOrder to be updated
	 * @return CharterOrder created
	 */
	@Nonnull
	CharterOrder updateCharterOrder(@Nonnull String charterOrderId, @Nonnull CharterOrder newCharterOrder);

	/**
	 * Update CharterOrder status
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param status the CharterOrder
	 * @return updated CharterOrder
	 */
	@Nonnull
	CharterOrder updateCharterOrderStatus(@Nonnull String charterOrderId, @Nonnull Order.Status status);

	/**
	 * Update CharterOrder to select a fleet candidate
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param fleetCandidateId the fleetCandidateId to be selected
	 * @return updated CharterOrder
	 */
	@Nonnull
	CharterOrder selectFleetCandidate(@Nonnull String charterOrderId, @Nonnull String fleetCandidateId);

	/**
	 * Update CharterOrder to offer a fleet candidate by tenant
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param fleetCandidateId the fleetCandidateId to be selected
	 * @param totalPrice the tenant offered totalPrice for the charter order
	 * @return updated CharterOrder
	 */
	@Nonnull
	CharterOrder offerFleetCandidate(@Nonnull String charterOrderId, @Nonnull String fleetCandidateId,
			double totalPrice);

	/**
	 * List all CharterOrders by pagination filtered by userId and order status.
	 * 
	 * @param userId the userId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	Page<CharterOrder> listUserCharterOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize);

	/**
	 * List all CharterOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	default @Nonnull Page<CharterOrder> listUserCharterOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserCharterOrders(userId, null, page, pageSize);
	}

	/**
	 * List all the CharterOrder placed on this tenant filtered by tenantId and order status.
	 * 
	 * @param tenantId the tenantId
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	Page<CharterOrder> listTenantCharterOrders(@Nonnull String tenantId, @Nullable Order.Status status, int page,
			int pageSize);

	/**
	 * List all the CharterOrder placed on this tenant
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	default Page<CharterOrder> listTenantCharterOrders(@Nonnull String tenantId, int page, int pageSize) {
		return listTenantCharterOrders(tenantId, null, page, pageSize);
	}

	/**
	 * List all CharterOrders by pagination (all users) filtered by order status.
	 * 
	 * @param status the order status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	Page<CharterOrder> listCharterOrders(@Nullable Order.Status status, int page, int pageSize);

	/**
	 * List all CharterOrders by pagination (all users).
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	default Page<CharterOrder> listCharterOrders(int page, int pageSize) {
		return listCharterOrders(null, page, pageSize);
	}

	/**
	 * Delete a CharterOrder.
	 * 
	 * @param charterOrderId the charterOrderId
	 */
	void deleteCharterOrder(@Nonnull String charterOrderId);

	/**
	 * Delete all CharterOrders of a user.
	 * 
	 * @param userId the userId
	 */
	void deleteCharterOrders(@Nonnull String userId);

}
