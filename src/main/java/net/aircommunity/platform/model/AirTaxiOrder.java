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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Created by guankai on 15/04/2017.
 */
@Entity
@Table(name = "air_platfrom_airtaxi_order")
public class AirTaxiOrder extends AircraftAwareOrder {
	private static final long serialVersionUID = 1L;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date date;

	// e.g. 8:00-9:00
	@Column(name = "time_slot", nullable = false)
	private String timeSlot;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtaxi_id", nullable = false)
	private AirTaxi airTaxi;

	// passengers
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<PassengerItem> passengers = new HashSet<>();

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

	public Set<PassengerItem> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<PassengerItem> passengers) {
		if (passengers != null) {
			passengers.stream().forEach(item -> item.setOrder(this));
			this.passengers.clear();
			this.passengers.addAll(passengers);
		}
	}

	public AirTaxi getAirTaxi() {
		return airTaxi;
	}

	public void setAirTaxi(AirTaxi airTaxi) {
		this.airTaxi = airTaxi;
		this.vendor = airTaxi.getVendor();
	}

	@Override
	public Type getType() {
		return Type.AIRTAXI;
	}

	@Override
	public Product getProduct() {
		return airTaxi;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTaxiOrder [chartered=").append(chartered).append(", date=").append(date)
				.append(", timeSlot=").append(timeSlot).append(", airTaxi=").append(airTaxi).append(", passengers=")
				.append(passengers).append(", orderNo=").append(orderNo).append(", status=").append(status)
				.append(", commented=").append(commented).append(", creationDate=").append(creationDate)
				.append(", paymentDate=").append(paymentDate).append(", finishedDate=").append(finishedDate)
				.append(", aircraftItem=").append(aircraftItem).append(", contact=").append(contact).append(", note=")
				.append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
