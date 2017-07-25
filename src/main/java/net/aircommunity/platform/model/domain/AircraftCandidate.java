package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.OrderAdapter;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * The user preferred candidate aircrafts of an {@code QuickFlightOrder}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_aircraft_candidate", indexes = {
		@Index(name = "idx_tenant_id_status", columnList = "tenant_id,status") //
})
@XmlAccessorType(XmlAccessType.FIELD)
public class AircraftCandidate extends OrderItemCandidate {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	@XmlJavaTypeAdapter(OrderAdapter.class)
	private QuickFlightOrder order;

	// should attach to an aircraft
	@NotNull
	@ManyToOne
	@JoinColumn(name = "aircraft_id")
	private Aircraft aircraft;

	@ManyToOne
	@JoinColumn(name = "tenant_id")
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private Tenant vendor;

	public AircraftCandidate() {
		super();
	}

	public AircraftCandidate(String id) {
		super(id);
	}

	public QuickFlightOrder getOrder() {
		return order;
	}

	public void setOrder(QuickFlightOrder order) {
		this.order = order;
	}

	public Aircraft getAircraft() {
		return aircraft;
	}

	public void setAircraft(Aircraft aircraft) {
		this.aircraft = aircraft;
		this.vendor = aircraft.getVendor();
	}

	@Override
	protected Tenant getVendor() {
		return vendor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aircraft == null) ? 0 : aircraft.hashCode());
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
		AircraftCandidate other = (AircraftCandidate) obj;
		if (aircraft == null) {
			if (other.aircraft != null)
				return false;
		}
		else if (!aircraft.equals(other.aircraft))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AircraftCandidate [offeredPrice=").append(offeredPrice).append(", status=").append(status)
				.append(", order=").append(order.getId()).append("]");
		return builder.toString();
	}
}
