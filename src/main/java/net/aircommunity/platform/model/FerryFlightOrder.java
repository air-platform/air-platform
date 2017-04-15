package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
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

	// customer contact information for this order
	@Embedded
	protected Contact contact;

	@ManyToOne
	@JoinColumn(name = "ferryflight_id", nullable = false)
	private FerryFlight ferryFlight;

	// NOTE: XXX
	// extra info (the vendor of this card, it's already available in jetCard, just add extra id to avoid join)
	// should be set via Product.vendor
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	private Tenant vendor;

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public FerryFlight getFerryFlight() {
		return ferryFlight;
	}

	public void setFerryFlight(FerryFlight ferryFlight) {
		this.ferryFlight = ferryFlight;
		this.vendor = ferryFlight.getVendor();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FerryFlightOrder [passengers=").append(passengers).append(", orderNo=").append(orderNo)
				.append(", contact=").append(contact).append(", note=").append(note).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}

}
