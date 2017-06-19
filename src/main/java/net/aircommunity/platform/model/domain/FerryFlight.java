package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Empty Legs, preferential charter.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_ferryflight", indexes = { @Index(name = "idx_departure", columnList = "departure") })
public class FerryFlight extends CharterableProduct {
	private static final long serialVersionUID = 1L;

	// Flight NO. cannot be unique, because it can be different date
	@NotEmpty
	@Column(name = "flight_no", nullable = false)
	private String flightNo;

	// e.g. Gulfstream 450
	@NotEmpty
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	// departure city
	@Column(name = "departure", nullable = false)
	private String departure;

	// arrival city
	@Column(name = "arrival", nullable = false)
	private String arrival;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "departure_date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date departureDate;

	// departure timeSlot, e.g. 8:00-9:00
	@Column(name = "time_slot", nullable = false)
	private String timeSlot;

	// images of this flight, comma separated, image1.png,image2/png
	@Lob
	@Column(name = "appearances")
	private String appearances;

	public FerryFlight() {
	}

	public FerryFlight(String id) {
		this.id = id;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getAircraftType() {
		return aircraftType;
	}

	public void setAircraftType(String aircraftType) {
		this.aircraftType = aircraftType;
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

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public String getAppearances() {
		return appearances;
	}

	public void setAppearances(String appearances) {
		this.appearances = appearances;
	}

	@Override
	public Category getCategory() {
		if (category == null) {
			return Category.AIR_JET;
		}
		return category;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FerryFlight [flightNo=").append(flightNo).append(", aircraftType=").append(aircraftType)
				.append(", departure=").append(departure).append(", arrival=").append(arrival).append(", seatPrice=")
				.append(seatPrice).append(", seats=").append(seats).append(", minPassengers=").append(minPassengers)
				.append(", departureDate=").append(departureDate).append(", timeSlot=").append(timeSlot)
				.append(", appearances=").append(appearances).append(", price=").append(price).append(", currencyUnit=")
				.append(currencyUnit).append(", name=").append(name).append(", image=").append(image).append(", score=")
				.append(score).append(", creationDate=").append(creationDate).append(", description=")
				.append(description).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
