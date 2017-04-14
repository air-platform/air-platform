package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Air Transport
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airtransport")
public class AirTransport extends Product {
	private static final long serialVersionUID = 1L;

	@Column(name = "family", nullable = false)
	private String family;

	// departure city
	@Column(name = "departure", nullable = false)
	private String departure;

	// arrival city
	@Column(name = "arrival", nullable = false)
	private String arrival;

	@OneToMany
	private Set<AircraftItem> aircraftItems = new HashSet<>();

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
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

	public Set<AircraftItem> getAircraftItems() {
		return aircraftItems;
	}

	public void setAircraftItems(Set<AircraftItem> aircraftItems) {
		if (aircraftItems != null) {
			aircraftItems.stream().forEach(item -> item.setProduct(this));
			this.aircraftItems = aircraftItems;
		}
	}

}
