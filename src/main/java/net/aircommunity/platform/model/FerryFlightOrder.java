package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * FerryFlight order.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_ferryflight_order")
public class FerryFlightOrder extends Order {
	private static final long serialVersionUID = 1L;

	// the number of passengers
	@Min(1)
	@Column(name = "passengers")
	private int passengers;

	@ManyToOne
	@JoinColumn(name = "flight_id", nullable = false)
	private FerryFlight flight;

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public FerryFlight getFlight() {
		return flight;
	}

	public void setFlight(FerryFlight flight) {
		this.flight = flight;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FerryFlightOrder [passengers=").append(passengers).append(", flight=").append(flight)
				.append(", orderNo=").append(orderNo).append(", contact=").append(contact).append(", note=")
				.append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
