package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Empty Legs, preferential charter.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_ferryflight", indexes = { @Index(name = "idx_departure", columnList = "departure") })
public class FerryFlight extends PricedProduct {
	private static final long serialVersionUID = 1L;

	// properties inherited:
	// name:
	// price: whole price (full load)

	// Flight NO.
	@NotEmpty
	@Column(name = "flight_no", nullable = false, unique = true)
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

	// single seat price
	@Column(name = "seat_price")
	private int seatPrice;

	// Number of seats
	@Column(name = "seats")
	private int seats;

	// min passengers required
	@Min(0)
	@Column(name = "min_passengers")
	private int minPassengers;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	private Date date;

	// departure timeSlot, e.g. 8:00-9:00
	@Column(name = "time_slot", nullable = false)
	private String timeSlot;

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

	public int getSeatPrice() {
		return seatPrice;
	}

	public void setSeatPrice(int seatPrice) {
		this.seatPrice = seatPrice;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getMinPassengers() {
		return minPassengers;
	}

	public void setMinPassengers(int minPassengers) {
		this.minPassengers = minPassengers;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FerryFlight [flightNo=").append(flightNo).append(", aircraftType=").append(aircraftType)
				.append(", departure=").append(departure).append(", arrival=").append(arrival).append(", seatPrice=")
				.append(seatPrice).append(", seats=").append(seats).append(", minPassengers=").append(minPassengers)
				.append(", date=").append(date).append(", timeSlot=").append(timeSlot).append(", image=").append(image)
				.append(", price=").append(price).append(", currencyUnit=").append(currencyUnit).append(", name=")
				.append(name).append(", description=").append(description).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
