package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
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
		@Index(name = "idx_tenant_id_status", columnList = "tenant_id,status") //
})
@XmlAccessorType(XmlAccessType.FIELD)
public class FleetCandidate extends OrderItemCandidate {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	@XmlJavaTypeAdapter(OrderAdapter.class)
	private CharterOrder order;

	// XXX: add adapter to return less information, e.g. name ?
	@ManyToOne
	@JoinColumn(name = "fleet_id")
	private Fleet fleet;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private Tenant vendor;

	public FleetCandidate() {
		super();
	}

	public FleetCandidate(String id) {
		super(id);
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

	@Override
	protected Tenant getVendor() {
		return vendor;
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
}
