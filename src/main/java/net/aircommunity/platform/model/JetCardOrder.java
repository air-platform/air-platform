package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * JetCard Order on a {@code JetCard}.
 * 
 * @author Bin.Zhang
 * @deprecated
 */
@Entity
@Table(name = "air_platfrom_jetcard_order")
public class JetCardOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "jetcard_id", nullable = false)
	private JetCard jetCard;

	@Override
	public Type getType() {
		return Type.JETCARD;
	}

	@Override
	public JetCard getProduct() {
		return jetCard;
	}

	@Override
	protected void doSetProduct(Product product) {
		jetCard = (JetCard) product;
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
