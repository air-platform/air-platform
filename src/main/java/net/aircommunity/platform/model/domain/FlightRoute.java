package net.aircommunity.platform.model.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Flight Route.
 * 
 * @author Bin.Zhang
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class FlightRoute implements Serializable {
	private static final long serialVersionUID = 1L;

	// departure city
	@NotEmpty
	@Size(max = 255)
	@Column(name = "departure", nullable = false)
	private String departure;

	@Column(name = "departure_lat")
	private double departureLatitude;

	@Column(name = "departure_lon")
	private double departureLongitude;

	// arrival city
	@NotEmpty
	@Size(max = 255)
	@Column(name = "arrival", nullable = false)
	private String arrival;

	@Column(name = "arrival_lat")
	private double arrivalLatitude;

	@Column(name = "arrival_lon")
	private double arrivalLongitude;

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public double getDepartureLatitude() {
		return departureLatitude;
	}

	public void setDepartureLatitude(double departureLatitude) {
		this.departureLatitude = departureLatitude;
	}

	public double getDepartureLongitude() {
		return departureLongitude;
	}

	public void setDepartureLongitude(double departureLongitude) {
		this.departureLongitude = departureLongitude;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public double getArrivalLatitude() {
		return arrivalLatitude;
	}

	public void setArrivalLatitude(double arrivalLatitude) {
		this.arrivalLatitude = arrivalLatitude;
	}

	public double getArrivalLongitude() {
		return arrivalLongitude;
	}

	public void setArrivalLongitude(double arrivalLongitude) {
		this.arrivalLongitude = arrivalLongitude;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FlightRoute [departure=").append(departure).append(", departureLatitude=")
				.append(departureLatitude).append(", departureLongitude=").append(departureLongitude)
				.append(", arrival=").append(arrival).append(", arrivalLatitude=").append(arrivalLatitude)
				.append(", arrivalLongitude=").append(arrivalLongitude).append("]");
		return builder.toString();
	}

}
