package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * AirTour Order
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platfrom_airtour_order")
public class AirTourOrder extends AircraftAwareOrder {
	private static final long serialVersionUID = 1L;

	// departure date, e.g. 2017-5-1
	// @NotNull
	// @Temporal(value = TemporalType.DATE)
	// @Column(name = "date", nullable = false)
	// @XmlJavaTypeAdapter(DateAdapter.class)
	// private Date date;

	// e.g. 8:00-9:00
	// @Column(name = "time_slot", nullable = false)
	// private String timeSlot;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtour_id", nullable = false)
	private AirTour airTour;

	@Override
	public Type getType() {
		return Type.AIRTOUR;
	}

	@Override
	public AirTour getProduct() {
		return airTour;
	}

	@Override
	protected void doSetProduct(Product product) {
		airTour = (AirTour) product;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTourOrder [departureDate=").append(departureDate).append(", timeSlot=").append(timeSlot)
				.append(", airTour=").append(airTour).append(", passengers=").append(passengers).append(", orderNo=")
				.append(orderNo).append(", status=").append(status).append(", commented=").append(commented)
				.append(", creationDate=").append(creationDate).append(", paymentDate=").append(paymentDate)
				.append(", finishedDate=").append(finishedDate).append(", contact=").append(contact).append(", note=")
				.append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
