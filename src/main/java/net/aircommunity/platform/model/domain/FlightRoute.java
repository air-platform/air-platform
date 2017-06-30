package net.aircommunity.platform.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class FlightRoute implements Serializable, DomainConstants {
	private static final long serialVersionUID = 1L;

	// departure city
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "departure", length = CITY_NAME_LEN, nullable = false)
	private String departure;

	@Column(name = "departure_lat", precision = 10, scale = 8)
	private BigDecimal departureLatitude = BigDecimal.ZERO;

	@Column(name = "departure_lon", precision = 11, scale = 8)
	private BigDecimal departureLongitude = BigDecimal.ZERO;

	// arrival city
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "arrival", length = CITY_NAME_LEN, nullable = false)
	private String arrival;

	@Column(name = "arrival_lat", precision = 10, scale = 8)
	private BigDecimal arrivalLatitude = BigDecimal.ZERO;

	@Column(name = "arrival_lon", precision = 11, scale = 8)
	private BigDecimal arrivalLongitude = BigDecimal.ZERO;

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public BigDecimal getDepartureLatitude() {
		return departureLatitude;
	}

	public void setDepartureLatitude(BigDecimal departureLatitude) {
		this.departureLatitude = departureLatitude;
	}

	public BigDecimal getDepartureLongitude() {
		return departureLongitude;
	}

	public void setDepartureLongitude(BigDecimal departureLongitude) {
		this.departureLongitude = departureLongitude;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public BigDecimal getArrivalLatitude() {
		return arrivalLatitude;
	}

	public void setArrivalLatitude(BigDecimal arrivalLatitude) {
		this.arrivalLatitude = arrivalLatitude;
	}

	public BigDecimal getArrivalLongitude() {
		return arrivalLongitude;
	}

	public void setArrivalLongitude(BigDecimal arrivalLongitude) {
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
