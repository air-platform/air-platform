package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The user preferred candidate fleets of an {@code CharterOrder}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_fleet_candidate")
@XmlAccessorType(XmlAccessType.FIELD)
public class FleetCandidate extends Persistable {
	private static final long serialVersionUID = 1L;

	// can have only one SELECTED
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.CANDIDATE;

	// TODO add nullable = false?

	@ManyToOne
	@JoinColumn(name = "order_id")
	private CharterOrder order;

	@ManyToOne
	@JoinColumn(name = "fleet_id")
	private Fleet fleet;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	private Tenant vendor;

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

	public enum Status {
		CANDIDATE, SELECTED;
	}

}
