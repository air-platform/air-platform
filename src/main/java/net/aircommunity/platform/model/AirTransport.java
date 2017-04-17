package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.aircommunity.platform.model.constraint.NotEmpty;

/**
 * Air Transport
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airtransport")
public class AirTransport extends Product {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Column(name = "family", nullable = false)
	private String family;

	// in minutes
	@Column(name = "time_estimation", nullable = false)
	private int timeEstimation;

	@Embedded
	private FlightRoute flightRoute;

	@NotEmpty
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<AircraftItem> aircraftItems = new HashSet<>();

	public AirTransport() {
	}

	public AirTransport(String id) {
		this.id = id;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public int getTimeEstimation() {
		return timeEstimation;
	}

	public void setTimeEstimation(int timeEstimation) {
		this.timeEstimation = timeEstimation;
	}

	public FlightRoute getFlightRoute() {
		return flightRoute;
	}

	public void setFlightRoute(FlightRoute flightRoute) {
		this.flightRoute = flightRoute;
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

	public boolean addAircraftItem(AircraftItem item) {
		if (aircraftItems.contains(item)) {
			return false;
		}
		item.setProduct(this);
		return aircraftItems.add(item);
	}

	public boolean removeAircraftItem(AircraftItem item) {
		if (!aircraftItems.contains(item)) {
			return false;
		}
		return aircraftItems.remove(item);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTransport [family=").append(family).append(", timeEstimation=").append(timeEstimation)
				.append(", flightRoute=").append(flightRoute).append(", aircraftItems=").append(aircraftItems)
				.append(", name=").append(name).append(", description=").append(description).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}
}
