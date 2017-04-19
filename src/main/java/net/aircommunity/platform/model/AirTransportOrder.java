package net.aircommunity.platform.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import io.micro.annotation.constraint.NotEmpty;


/**
 * AirTransport Order on a {@code AirTransport}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airtransport_order")
public class AirTransportOrder extends CharterableOrder {
	private static final long serialVersionUID = 1L;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	private Date date;

	// e.g. 8:00-9:00
	@NotEmpty
	@Column(name = "time_slot", nullable = false)
	private String timeSlot;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtransport_id", nullable = false)
	private AirTransport airTransport;

	// passengers
	@NotEmpty
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Passenger> passengers = new HashSet<>();

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

	public AirTransport getAirTransport() {
		return airTransport;
	}

	public void setAirTransport(AirTransport airTransport) {
		this.airTransport = airTransport;
		this.vendor = airTransport.getVendor();
	}

	public Set<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<Passenger> passengers) {
		this.passengers = passengers;
	}

	@Override
	public Product getProduct() {
		return airTransport;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTransportOrder [chartered=").append(chartered).append(", date=").append(date)
				.append(", timeSlot=").append(timeSlot).append(", airTransport=").append(airTransport)
				.append(", passengers=").append(passengers).append(", orderNo=").append(orderNo).append(", status=")
				.append(status).append(", commented=").append(commented).append(", creationDate=").append(creationDate)
				.append(", paymentDate=").append(paymentDate).append(", finishedDate=").append(finishedDate)
				.append(", note=").append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
