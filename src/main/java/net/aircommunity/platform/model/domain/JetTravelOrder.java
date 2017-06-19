package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * JetTravel Order on a {@code JetTravel}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_jettravel_order")
public class JetTravelOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "jettravel_id", nullable = false)
	private JetTravel jetTravel;

	@Override
	public Type getType() {
		return Type.JETTRAVEL;
	}

	@Override
	public JetTravel getProduct() {
		return jetTravel;
	}

	@Override
	protected void doSetProduct(Product product) {
		jetTravel = (JetTravel) product;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JetTravelOrder [orderNo=").append(orderNo).append(", pointsUsed=").append(pointsUsed)
				.append(", quantity=").append(quantity).append(", totalPrice=").append(totalPrice).append(", status=")
				.append(status).append(", commented=").append(commented).append(", creationDate=").append(creationDate)
				.append(", lastModifiedDate=").append(lastModifiedDate).append(", paymentDate=").append(paymentDate)
				.append(", refundedDate=").append(refundedDate).append(", finishedDate=").append(finishedDate)
				.append(", closedDate=").append(closedDate).append(", cancelledDate=").append(cancelledDate)
				.append(", deletedDate=").append(deletedDate).append(", contact=").append(contact)
				.append(", refundReason=").append(refundReason).append(", refundFailureCause=")
				.append(refundFailureCause).append(", closedReason=").append(closedReason).append(", note=")
				.append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}
}