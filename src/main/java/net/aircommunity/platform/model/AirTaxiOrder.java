package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * AirTaxi Order.
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platfrom_airtaxi_order")
public class AirTaxiOrder extends AircraftAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtaxi_id", nullable = false)
	private AirTaxi airTaxi;

	@Override
	public Type getType() {
		return Type.AIRTAXI;
	}

	@Override
	public AirTaxi getProduct() {
		return airTaxi;
	}

	@Override
	protected void doSetProduct(Product product) {
		airTaxi = (AirTaxi) product;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTaxiOrder [departureDate=").append(departureDate).append(", timeSlot=").append(timeSlot)
				.append(", contact=").append(contact).append(", orderNo=").append(orderNo).append(", status=")
				.append(status).append(", commented=").append(commented).append(", creationDate=").append(creationDate)
				.append(", paymentDate=").append(paymentDate).append(", finishedDate=").append(finishedDate)
				.append(", cancelledDate=").append(cancelledDate).append(", deletedDate=").append(deletedDate)
				.append(", note=").append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
