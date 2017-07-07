package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.OrderAdapter;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * The user preferred candidate fleets of an {@code CharterOrder}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_fleet_candidate", indexes = {
		@Index(name = "idx_tenant_id_status", columnList = "tenant_id,status") })
@XmlAccessorType(XmlAccessType.FIELD)
public class FleetCandidate extends Persistable {
	private static final long serialVersionUID = 1L;

	// Order offered price of this fleet
	@Column(name = "offered_Price", nullable = false)
	private BigDecimal offeredPrice = BigDecimal.ZERO;

	// can have only one SELECTED
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = FLEET_CANDIDATE_STATUS_LEN, nullable = false)
	private Status status = Status.CANDIDATE;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	@XmlJavaTypeAdapter(OrderAdapter.class)
	private CharterOrder order;

	// TODO add adapter to return less information, e.g. name ?
	@ManyToOne
	@JoinColumn(name = "fleet_id")
	private Fleet fleet;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private Tenant vendor;

	public FleetCandidate() {
	}

	public FleetCandidate(String id) {
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

	public CharterOrder getOrder() {
		return order;
	}

	public void setOrder(CharterOrder order) {
		this.order = order;
	}

	public Fleet getFleet() {
		return fleet;
	}

	public void setFleet(Fleet fleet) {
		this.fleet = fleet;
		this.vendor = fleet.getVendor();
	}

	public boolean isOwnedByTenant(String tenantId) {
		return fleet.getVendor().getId().equals(tenantId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fleet == null) ? 0 : fleet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FleetCandidate other = (FleetCandidate) obj;
		if (fleet == null) {
			if (other.fleet != null)
				return false;
		}
		else if (!fleet.equals(other.fleet))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetCandidate [offeredPrice=").append(offeredPrice).append(", status=").append(status)
				.append(", order=").append(order.getId()).append("]");
		return builder.toString();
	}

	/**
	 * FleetCandidate status
	 */
	public enum Status {

		/**
		 * Candidates chosen by user (Initial state)
		 */
		CANDIDATE,

		/**
		 * Vendor mark current candidate as offered according to user chosen candidates
		 */
		OFFERED,

		/**
		 * This FleetCandidate is selected by user
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
