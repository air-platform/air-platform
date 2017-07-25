package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Order model with a multiple-candidates
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CandidateOrder<T extends OrderItemCandidate> extends StandardOrder {
	private static final long serialVersionUID = 1L;

	/**
	 * Offer the T with the given candidateId, and other candidates of this tenant will be reset to CANDIDATE status,
	 * that means allow offer multiple fleetCandidate per tenant. (Normally called by TENANT)
	 * 
	 * @param candidateId the candidateId
	 * @return the selected T or Optional.empty
	 */
	public Optional<T> offerCandidate(String candidateId, BigDecimal offeredAmount) {
		return OrderItemCandidateSupport.offerCandidate(getCandidates(), candidateId, offeredAmount);
	}

	/**
	 * Refuse the T with the given candidateId, its status be reset to CANDIDATE status. (Normally called by TENANT)
	 * 
	 * @param candidateId the candidateId
	 * @return the refused T or Optional.empty
	 */
	public Optional<T> refuseCandidate(String candidateId) {
		return OrderItemCandidateSupport.refuseCandidate(getCandidates(), candidateId);
	}

	/**
	 * Only allow to select one T, other selected candidates will be reset to CANDIDATE status. (Normally called by
	 * USER)
	 * 
	 * @param candidateId
	 * @return the selected T or Optional.empty
	 */
	public Optional<T> selectCandidate(String candidateId) {
		return OrderItemCandidateSupport.selectCandidate(getCandidates(), candidateId);
	}

	/**
	 * Return current selected candidate.
	 * 
	 * @return selected candidate
	 */
	@XmlTransient
	public Optional<T> getSelectedCandidate() {
		return getCandidates().stream().filter(OrderItemCandidate::isSelected).findFirst();
	}

	@Override
	public boolean hasCandidates() {
		return true;
	}

	@Override
	public Product getProduct() {
		// no product bound to this order
		return null;
	}

	@Override
	public void setProduct(Product product) {
		// no product bound to this order
	}

	@Override
	public boolean isForTenant(String tenantId) {
		boolean forTenant = super.isForTenant(tenantId);
		if (forTenant) {
			return true;
		}
		if (getCandidates().isEmpty()) {
			return false;
		}
		return getCandidates().stream().anyMatch(candidate -> candidate.isOwnedByTenant(tenantId));
	}

	/**
	 * MUST return JPA managed candidate (DO NOT copy or make it immutable)
	 * 
	 * @return JPA managed candidate
	 */
	@XmlTransient
	public abstract Set<T> getCandidates();

	public abstract void setCandidates(Set<T> candidates);
}
