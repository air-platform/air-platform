package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * AirTaxi
 */
@Entity
@Table(name = "air_platfrom_airtaxi")
public class AirTaxi extends AircraftAwareProduct {
	private static final long serialVersionUID = 1L;

	@Embedded
	private FlightRoute flightRoute;

	@Column(name = "distance")
	private String distance;

	@Column(name = "duration")
	private String duration;

	public AirTaxi() {
	}

	public AirTaxi(String id) {
		this.id = id;
	}

	public FlightRoute getFlightRoute() {
		return flightRoute;
	}

	public void setFlightRoute(FlightRoute flightRoute) {
		this.flightRoute = flightRoute;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTaxi [flightRoute=").append(flightRoute).append(", distance=").append(distance)
				.append(", duration=").append(duration).append(", currentTime=").append(currentTime).append(", name=")
				.append(name).append(", image=").append(image).append(", score=").append(score).append(", rank=")
				.append(rank).append(", creationDate=").append(creationDate).append(", description=")
				.append(description).append(", published=").append(published).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
