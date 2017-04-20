package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Air Transport
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airtransport")
public class AirTransport extends AircraftAwareProduct {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Column(name = "family", nullable = false)
	private String family;

	// in minutes
	@Column(name = "time_estimation", nullable = false)
	private int timeEstimation;

	@Embedded
	private FlightRoute flightRoute;

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
