package net.aircommunity.platform.service.order;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.CandidateOrder;
import net.aircommunity.platform.model.domain.OrderItemCandidate;

/**
 * Candidate order service shared by all orders for a {@code User} (should be implemented by a concrete order service).
 *
 * @author Bin.Zhang
 */
public interface CandidateOrderService<C extends OrderItemCandidate, T extends CandidateOrder<C>>
		extends StandardOrderService<T> {

	/**
	 * Update order to offer a order item candidate(optional) and a price by tenant
	 * 
	 * @param orderId the orderId
	 * @param candidateId the candidateId to be selected
	 * @param totalAmount the tenant offered totalAmount > 0 for the charter order
	 * @return updated order
	 */
	@Nonnull
	T offerOrderCandidate(@Nonnull String orderId, @Nonnull String candidateId,
			@Nonnull @Nonnegative BigDecimal totalAmount);

	/**
	 * Update order to refuse a order item candidate by tenant
	 * 
	 * @param orderId the orderId
	 * @param candidateId the candidateId to be selected
	 * @return updated order
	 */
	@Nonnull
	T refuseOrderCandidate(@Nonnull String orderId, @Nonnull String candidateId);

	/**
	 * Update order to select a order item candidate by a user
	 * 
	 * @param orderId the orderId
	 * @param candidateId the candidateId to be selected
	 * @return updated order
	 */
	@Nonnull
	T selectOrderCandidate(@Nonnull String orderId, @Nonnull String candidateId);

}
