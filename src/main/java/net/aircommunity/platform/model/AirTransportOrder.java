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

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * AirTransport Order on a {@code AirTransport}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airtransport_order")
public class AirTransportOrder extends AircraftAwareOrder {
	private static final long serialVersionUID = 1L;

	// the number of passengers if NOT chartered
	@Column(name = "passenger_num")
	private int passengerNum;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date date;

	// e.g. 08:00-09:00, HHmm
	@NotEmpty
	@Column(name = "time_slot", nullable = false)
	private String timeSlot;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtransport_id", nullable = false)
	private AirTransport airTransport;

	public int getPassengerNum() {
		return passengerNum;
	}

	public void setPassengerNum(int passengerNum) {
		this.passengerNum = passengerNum;
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

	public AirTransport getAirTransport() {
		return airTransport;
	}

	public void setAirTransport(AirTransport airTransport) {
		this.airTransport = airTransport;
		this.vendor = airTransport.getVendor();
	}

	@Override
	public Type getType() {
		return Type.AIRTRANSPORT;
	}

	@Override
	public Product getProduct() {
		return airTransport;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTransportOrder [date=").append(date).append(", timeSlot=").append(timeSlot)
				.append(", airTransport=").append(airTransport).append(", passengers=").append(passengers)
				.append(", orderNo=").append(orderNo).append(", status=").append(status).append(", commented=")
				.append(commented).append(", creationDate=").append(creationDate).append(", paymentDate=")
				.append(paymentDate).append(", finishedDate=").append(finishedDate).append(", salesPackageNum=")
				.append(salesPackageNum).append(", contact=").append(contact).append(", note=").append(note)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}
}
