package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The user preferred candidate of an order that provides multiple-choices, and only one candidate can be SELECTED
 * finally.
 * 
 * @author Bin.Zhang
 */
final class OrderItemCandidateSupport {

	/**
	 * Only allow to select one OrderItemCandidate, other selected OrderItemCandidates will be reset to CANDIDATE
	 * status. (Normally called by USER)
	 * 
	 * @param candidateId the candidateId
	 * @param candidates a set of candidates to be selected from
	 * @return the selected OrderItemCandidate or Optional.empty
	 */
	public static <T extends OrderItemCandidate> Optional<T> selectCandidate(Set<T> candidates, String candidateId) {
		if (candidateId == null) {
			return Optional.empty();
		}
		Optional<T> optional = candidates.stream().filter(candidate -> candidate.getId().equals(candidateId))
				.findFirst();
		if (optional.isPresent()) {
			// reset all the SELECTED status to OFFERED
			candidates.stream().forEach(candidate -> {
				if (candidate.getStatus() == OrderItemCandidate.Status.SELECTED) {
					// if the candidate is in SELECTED status, it should be OFFERED before
					// so we just reset it to it's previous status OFFERED
					candidate.setStatus(OrderItemCandidate.Status.OFFERED);
				}
			});
			// make the current candidateId as SELECTED
			optional.get().setStatus(OrderItemCandidate.Status.SELECTED);
		}
		return optional;
	}

	/**
	 * Offer the OrderItemCandidate with the given candidateId, and other OrderItemCandidates of this tenant will be
	 * reset to CANDIDATE status, that means allow offer multiple candidate per tenant. (Normally called by TENANT)
	 * 
	 * @param candidates the candidates
	 * @param candidateId the candidateId
	 * @param offeredAmount the offeredAmount
	 * @return the selected OrderItemCandidate or Optional.empty
	 */
	public static <T extends OrderItemCandidate> Optional<T> offerCandidate(@Nonnull Set<T> candidates,
			@Nonnull String candidateId, @Nullable BigDecimal offeredAmount) {
		if (candidateId == null) {
			return Optional.empty();
		}
		Optional<T> candidateRef = candidates.stream().filter(candidate -> candidate.getId().equals(candidateId))
				.findFirst();
		if (candidateRef.isPresent()) {
			Tenant vendor = candidateRef.get().getVendor();
			candidates.stream().filter(candidate -> candidate.isOwnedByTenant(vendor.getId())).forEach(candidate -> {
				// only allow offer multiple candidate per tenant
				if (candidate.getId().equals(candidateId)) {
					candidate.setStatus(OrderItemCandidate.Status.OFFERED);
					if (offeredAmount != null) {
						candidate.setOfferedPrice(offeredAmount);
					}
				}
			});
		}
		return candidateRef;
	}

	/**
	 * Refuse the OrderItemCandidate with the given candidateId, its status be reset to CANDIDATE status. (Normally
	 * called by TENANT)
	 * 
	 * @param candidateId
	 * @return the refused OrderItemCandidate or Optional.empty
	 */
	public static <T extends OrderItemCandidate> Optional<T> refuseCandidate(Set<T> candidates, String candidateId) {
		if (candidateId == null) {
			return Optional.empty();
		}
		Optional<T> candidateRef = candidates.stream().filter(candidate -> candidate.getId().equals(candidateId))
				.findFirst();
		if (candidateRef.isPresent()) {
			T candidate = candidateRef.get();
			candidate.setStatus(OrderItemCandidate.Status.CANDIDATE);
			candidate.setOfferedPrice(BigDecimal.ZERO);
		}
		return candidateRef;
	}

	private OrderItemCandidateSupport() {
		throw new AssertionError();
	}

}
