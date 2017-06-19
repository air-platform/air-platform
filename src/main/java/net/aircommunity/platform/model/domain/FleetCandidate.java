package net.aircommunity.platform.model.domain;

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
@Table(name = "air_platfrom_fleet_candidate", indexes = { @Index(name = "idx_status", columnList = "status") })
@XmlAccessorType(XmlAccessType.FIELD)
public class FleetCandidate extends Persistable {
	private static final long serialVersionUID = 1L;

	// can have only one SELECTED
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
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