package net.aircommunity.platform.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;

/**
 * Charterable Product of an {@code Tenant} with single price(full price), seat price and seats.
 * 
 * @author Bin.Zhang
 */
@Entity
public abstract class CharterableProduct extends StandardProduct {
	private static final long serialVersionUID = 1L;

	// single seat price
	@Column(name = "seat_price")
	protected BigDecimal seatPrice = BigDecimal.ZERO;

	// Number of seats
	@Column(name = "seats")
	protected int seats;

	// min passengers required (XXX useful?)
	@Min(0)
	@Column(name = "min_passengers")
	protected int minPassengers;

	public BigDecimal getSeatPrice() {
		return seatPrice;
	}

	public void setSeatPrice(BigDecimal seatPrice) {
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

}
