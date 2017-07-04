package net.aircommunity.platform.service.order;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.CharterOrder;
import net.aircommunity.platform.model.domain.Order;

/**
 * CharterOrder service.
 * 
 * @author Bin.Zhang
 */
public interface CharterOrderService extends StandardOrderService<CharterOrder> {

	/**
	 * Create a CharterOrder.
	 * 
	 * @param userId the userId
	 * @param fleetCandidates the fleetCandidates Set<FleetCandidate> fleetCandidates,
	 * @param charterOrder the charterOrder to be created
	 * @return CharterOrder created
	 */
	@Nonnull
	default CharterOrder createCharterOrder(@Nonnull String userId, @Nonnull CharterOrder charterOrder) {
		return createOrder(userId, charterOrder);
	}

	/**
	 * Retrieves the specified CharterOrder.
	 * 
	 * @param charterOrderId the charterOrderId
	 * @return the CharterOrder found
	 * @throws AirException if not found
	 */
	@Nonnull
	default CharterOrder findCharterOrder(@Nonnull String charterOrderId) {
		return findOrder(charterOrderId);
	}

	/**
	 * Update a CharterOrder.
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param newCharterOrder the CharterOrder to be updated
	 * @return CharterOrder created
	 */
	@Nonnull
	default CharterOrder updateCharterOrder(@Nonnull String charterOrderId, @Nonnull CharterOrder newCharterOrder) {
		return updateOrder(charterOrderId, newCharterOrder);
	}

	/**
	 * Update CharterOrder status
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param status the CharterOrder
	 * @return updated CharterOrder
	 */
	@Nonnull
	default CharterOrder updateCharterOrderStatus(@Nonnull String charterOrderId, @Nonnull Order.Status status) {
		return updateOrderStatus(charterOrderId, status);
	}

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
	 * Update CharterOrder to offer a fleet candidate(optional) and a price by tenant
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param fleetCandidateId the fleetCandidateId to be selected
	 * @param totalAmount the tenant offered totalAmount > 0 for the charter order
	 * @return updated CharterOrder
	 */
	@Nonnull
	CharterOrder offerFleetCandidate(@Nonnull String charterOrderId, String fleetCandidateId,
			@Nonnull @Nonnegative BigDecimal totalAmount);

	/**
	 * Update CharterOrder to refuse a fleet candidate by tenant
	 * 
	 * @param charterOrderId the charterOrderId
	 * @param fleetCandidateId the fleetCandidateId to be selected
	 * @return updated CharterOrder
	 */
	@Nonnull
	CharterOrder refuseFleetCandidate(@Nonnull String charterOrderId, @Nonnull String fleetCandidateId);

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
	default Page<CharterOrder> listUserCharterOrders(@Nonnull String userId, @Nullable Order.Status status, int page,
			int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	/**
	 * List all CharterOrders by pagination filtered by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	default @Nonnull Page<CharterOrder> listUserCharterOrders(@Nonnull String userId, int page, int pageSize) {
		return listUserOrders(userId, page, pageSize);
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
	default Page<CharterOrder> listTenantCharterOrders(@Nonnull String tenantId, @Nullable Order.Status status,
			int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

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
		return listTenantOrders(tenantId, page, pageSize);
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
	default Page<CharterOrder> listCharterOrders(@Nullable Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	/**
	 * List all CharterOrders by pagination (all users).
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	default Page<CharterOrder> listCharterOrders(int page, int pageSize) {
		return listAllOrders(page, pageSize);
	}

	/**
	 * Delete a CharterOrder.
	 * 
	 * @param charterOrderId the charterOrderId
	 */
	default void deleteCharterOrder(@Nonnull String charterOrderId) {
		deleteOrder(charterOrderId);
	}

	/**
	 * Delete all CharterOrders of a user.
	 * 
	 * @param userId the userId
	 */
	default void deleteCharterOrders(@Nonnull String userId) {
		deleteOrders(userId);
	}

}
