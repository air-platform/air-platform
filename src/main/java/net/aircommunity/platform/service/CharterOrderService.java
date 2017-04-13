package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

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

	@Nonnull
	CharterOrder updateCharterOrderStatus(@Nonnull String charterOrderId, @Nonnull Order.Status status);

	Page<CharterOrder> listTenantCharterOrders(String tenantId, int page, int pageSize);

	/**
	 * List all CharterOrders by pagination filter by userId.
	 * 
	 * @param userId the userId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	Page<CharterOrder> listCharterOrders(@Nonnull String userId, int page, int pageSize);

	/**
	 * List all CharterOrders by pagination (all users)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of CharterOrders or empty
	 */
	@Nonnull
	Page<CharterOrder> listCharterOrders(int page, int pageSize);

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
