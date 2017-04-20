package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * AirTaxi
 */
@Entity
@Table(name = "air_platfrom_airtaxi")
public class AirTaxi extends AircraftAwareProduct {
	private static final long serialVersionUID = 1L;

	@Column(name = "departure", nullable = false)
	private String departure;

	@Column(name = "arrvial", nullable = false)
	private String arrival;

	@Column(name = "depart_loc", nullable = false)
	private String departLoc;

	@Column(name = "arrival_loc", nullable = false)
	private String arrivalLoc;

	@Column(name = "distance")
	private String distance;

	@Column(name = "duration")
	private String duration;

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
		builder.append("AirTaxi [departure=").append(departure).append(", arrival=").append(arrival)
				.append(", departLoc=").append(departLoc).append(", arrivalLoc=").append(arrivalLoc).append(", name=")
				.append(name).append(", score=").append(score).append(", creationDate=").append(creationDate)
				.append(", description=").append(description).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
