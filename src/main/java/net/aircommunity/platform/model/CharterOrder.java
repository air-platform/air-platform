package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Charter Order on a {@code Fleet}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_charter_order")
public class CharterOrder extends Order {
	private static final long serialVersionUID = 1L;

	@Column(name = "aircraft_type")
	private String aircraftType;

	// multiple flight legs
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<FlightLeg> flightLegs = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "fleet_id", nullable = false)
	private Fleet fleet;

	public String getAircraftType() {
		return aircraftType;
	}

	public void setAircraftType(String aircraftType) {
		this.aircraftType = aircraftType;
	}

	public Set<FlightLeg> getFlightLegs() {
		return flightLegs;
	}

	public void setFlightLegs(Set<FlightLeg> flightLegs) {
		this.flightLegs = flightLegs;
	}

	public Fleet getFleet() {
		return fleet;
	}

	public void setFleet(Fleet fleet) {
		this.fleet = fleet;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CharterOrder [aircraftType=").append(aircraftType).append(", orderNo=").append(orderNo)
				.append(", contact=").append(contact).append(", note=").append(note).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}

}
