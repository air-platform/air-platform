package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.OrderAdapter;

/**
 * Flight segment (of a voyage) for a {@code CharterOrder}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_flightleg")
@XmlAccessorType(XmlAccessType.FIELD)
public class FlightLeg extends Persistable {
	private static final long serialVersionUID = 1L;

	// departure city
	@NotEmpty
	@Column(name = "departure", nullable = false)
	private String departure;

	// arrival city
	@NotEmpty
	@Column(name = "arrival", nullable = false)
	private String arrival;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	private Date date;

	// the number of passengers
	@Min(1)
	@Column(name = "passengers")
	private int passengers;

	@XmlJavaTypeAdapter(OrderAdapter.class)
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private CharterOrder order;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public CharterOrder getOrder() {
		return order;
	}

	public void setOrder(CharterOrder order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrival == null) ? 0 : arrival.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((departure == null) ? 0 : departure.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlightLeg other = (FlightLeg) obj;
		if (arrival == null) {
			if (other.arrival != null)
				return false;
		}
		else if (!arrival.equals(other.arrival))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		}
		else if (!date.equals(other.date))
			return false;
		if (departure == null) {
			if (other.departure != null)
				return false;
		}
		else if (!departure.equals(other.departure))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FlightLeg [departure=").append(departure).append(", arrival=").append(arrival).append(", date=")
				.append(date).append(", passengers=").append(passengers).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
