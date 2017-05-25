package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Aircraft for (Taxi, Transportation, Tour).
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_aircraft")
public class Aircraft extends Product {
	private static final long serialVersionUID = 1L;

	// Flight NO. global unique, e.g. 353252
	@NotEmpty
	@Column(name = "flight_no", nullable = false, unique = true)
	private String flightNo;

	// e.g. G550
	@Column(name = "type", nullable = false)
	private String type;

	// e.g. fixed-wing / helicopter / balloon
	@Column(name = "category", nullable = false)
	private String category;

	// Number of seats
	@Min(1)
	@Column(name = "seats")
	private int seats;

	// min passengers required
	@Min(1)
	@Column(name = "min_passengers")
	private int minPassengers;

	public Aircraft() {
	}

	public Aircraft(String id) {
		this.id = id;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Aircraft [flightNo=").append(flightNo).append(", type=").append(type).append(", category=")
				.append(category).append(", seats=").append(seats).append(", minPassengers=").append(minPassengers)
				.append(", image=").append(image).append(", description=").append(description).append(", id=")
				.append(id).append("]");
		return builder.toString();
	}
}
