package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Empty Legs, preferential charter.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_ferryflight")
public class FerryFlight extends Persistable {
	private static final long serialVersionUID = 1L;

	// Flight NO.
	@Column(name = "flight_no", nullable = false)
	private String flightNo;

	// e.g. Gulfstream 450
	@Column(name = "aircraft_type", nullable = false)
	private String aircraftType;

	// departure city
	@Column(name = "departure", nullable = false)
	private String departure;

	// arrival city
	@Column(name = "arrival", nullable = false)
	private String arrival;

	// whole price (full load)
	@Column(name = "fullload_price")
	private int fullLoadPrice;

	// single seat price
	@Column(name = "seat_price")
	private int seatPrice;

	@Column(name = "unit", nullable = false)
	@Enumerated(EnumType.STRING)
	private CurrencyUnit unit = CurrencyUnit.RMB;

	// Number of seats
	@Column(name = "seats")
	private int seats;

	// min passengers required
	@Min(0)
	@Column(name = "min_passengers")
	private int minPassengers;

	// departure date, e.g. 2017-5-1
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	private Date date;

	// the image of this flight
	@Column(name = "avatar")
	private String avatar;

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	private Tenant owner;

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

	public int getFullLoadPrice() {
		return fullLoadPrice;
	}

	public void setFullLoadPrice(int fullLoadPrice) {
		this.fullLoadPrice = fullLoadPrice;
	}

	public int getSeatPrice() {
		return seatPrice;
	}

	public void setSeatPrice(int seatPrice) {
		this.seatPrice = seatPrice;
	}

	public CurrencyUnit getUnit() {
		return unit;
	}

	public void setUnit(CurrencyUnit unit) {
		this.unit = unit;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Tenant getOwner() {
		return owner;
	}

	public void setOwner(Tenant owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FerryFlight [flightNo=").append(flightNo).append(", aircraftType=").append(aircraftType)
				.append(", departure=").append(departure).append(", arrival=").append(arrival)
				.append(", fullLoadPrice=").append(fullLoadPrice).append(", seatPrice=").append(seatPrice)
				.append(", unit=").append(unit).append(", seats=").append(seats).append(", minPassengers=")
				.append(minPassengers).append(", date=").append(date).append(", avatar=").append(avatar).append("]");
		return builder.toString();
	}

}
