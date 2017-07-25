package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The user preferred candidate of an order that provides multiple-choices, and only one candidate can be SELECTED
 * finally.
 * 
 * @author Bin.Zhang
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class OrderItemCandidate extends Persistable {
	private static final long serialVersionUID = 1L;

	// Order offered price of this fleet
	@Column(name = "offered_price", nullable = false)
	protected BigDecimal offeredPrice = BigDecimal.ZERO;

	// can have only one SELECTED
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = ORDER_ITEM_CANDIDATE_STATUS_LEN, nullable = false)
	protected Status status = Status.CANDIDATE;

	public OrderItemCandidate() {
	}

	public OrderItemCandidate(String id) {
		this.id = id;
	}

	public BigDecimal getOfferedPrice() {
		return offeredPrice;
	}

	public void setOfferedPrice(BigDecimal offeredPrice) {
		this.offeredPrice = offeredPrice;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isOwnedByTenant(String tenantId) {
		return getVendor().getId().equals(tenantId);
	}

	protected abstract Tenant getVendor();

	// public abstract CandidateOrder getOrder();
	//
	// public abstract void setOrder(CandidateOrder order);

	/**
	 * Candidate status
	 */
	public enum Status {

		/**
		 * Candidate chosen by user (Initial state)
		 */
		CANDIDATE,

		/**
		 * Vendor or admin mark current candidate as offered according to user chosen candidates
		 */
		OFFERED,

		/**
		 * This candidate is selected by user
		 */
		SELECTED,

		/**
		 * Soft deletion
		 */
		DELETED;

		// According to JSR 311 spec, if used in @QueryParam, fromString is a naming conversion
		public static Status fromString(String source) {
			for (Status e : values()) {
				if (e.name().equalsIgnoreCase(source)) {
					return e;
				}
			}
			return null;
		}
	}

}
