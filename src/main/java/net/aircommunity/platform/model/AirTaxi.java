package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * AirTaxi
 */
@Entity
@Table(name = "air_platfrom_airtaxi")
public class AirTaxi extends Product {
	private static final long serialVersionUID = 1L;

	@Column(name = "departure", nullable = false)
	private String departure;

	@Column(name = "arrvial", nullable = false)
	private String arrival;

	@Column(name = "depart_loc", nullable = false)
	private String departLoc;

	@Column(name = "arrival_loc", nullable = false)
	private String arrivalLoc;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<AircraftItem> aircraftItems = new HashSet<>();

	public AirTaxi() {
	}

	public AirTaxi(String id) {
		this.id = id;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public String getDepartLoc() {
		return departLoc;
	}

	public void setDepartLoc(String departLoc) {
		this.departLoc = departLoc;
	}

	public String getArrivalLoc() {
		return arrivalLoc;
	}

	public void setArrivalLoc(String arrivalLoc) {
		this.arrivalLoc = arrivalLoc;
	}

	public Set<AircraftItem> getAircraftItems() {
		return aircraftItems;
	}

	public void setAircraftItems(Set<AircraftItem> aircraftItems) {
		if (aircraftItems != null) {
			aircraftItems.stream().forEach(item -> item.setProduct(this));
			this.aircraftItems = aircraftItems;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTaxi [departure=").append(departure).append(", arrival=").append(arrival)
				.append(", departLoc=").append(departLoc).append(", arrivalLoc=").append(arrivalLoc).append(", name=")
				.append(name).append(", score=").append(score).append(", creationDate=").append(creationDate)
				.append(", description=").append(description).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
