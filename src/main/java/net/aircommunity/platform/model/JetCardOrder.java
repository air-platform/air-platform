package net.aircommunity.platform.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * JetCard Order on a {@code JetCard}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_jetcard_order")
public class JetCardOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	// customer contact information for this order
	@Embedded
	private Contact contact;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "jetcard_id", nullable = false)
	private JetCard jetCard;

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public JetCard getJetCard() {
		return jetCard;
	}

	public void setJetCard(JetCard jetCard) {
		this.jetCard = jetCard;
		this.vendor = jetCard.getVendor();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JetCardOrder [contact=").append(contact).append(", orderNo=").append(orderNo)
				.append(", creationDate=").append(creationDate).append(", paymentDate=").append(paymentDate)
				.append(", finishedDate=").append(finishedDate).append(", note=").append(note).append(", id=")
				.append(id).append("]");
		return builder.toString();
	}

}
