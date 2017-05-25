package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Created by guankai on 15/04/2017.
 */
@Entity
@Table(name = "air_platfrom_airtour_order")
public class AirTourOrder extends AircraftAwareOrder {
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
	@JoinColumn(name = "airtour_id", nullable = false)
	private AirTour airTour;

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

	public AirTour getAirTour() {
		return airTour;
	}

	public void setAirTour(AirTour airTour) {
		this.airTour = airTour;
		this.vendor = airTour.getVendor();
	}

	@Override
	public Type getType() {
		return Type.AIRTOUR;
	}

	@Override
	public Product getProduct() {
		return airTour;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTourOrder [date=").append(date).append(", timeSlot=").append(timeSlot).append(", airTour=")
				.append(airTour).append(", passengers=").append(passengers).append(", orderNo=").append(orderNo)
				.append(", status=").append(status).append(", commented=").append(commented).append(", creationDate=")
				.append(creationDate).append(", paymentDate=").append(paymentDate).append(", finishedDate=")
				.append(finishedDate).append(", aircraftItem=").append(aircraftItem).append(", contact=")
				.append(contact).append(", note=").append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
