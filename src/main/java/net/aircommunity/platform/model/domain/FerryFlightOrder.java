package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import net.aircommunity.platform.model.domain.Product.Type;

/**
 * FerryFlight order.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_ferryflight_order", indexes = {
		@Index(name = "idx_user_id_status", columnList = "user_id,status,creation_date"),
		@Index(name = "idx_tenant_id_status", columnList = "tenant_id,status,creation_date")
		//
})
public class FerryFlightOrder extends CharterableOrder {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "ferryflight_id", nullable = false)
	private FerryFlight ferryFlight;

	@Override
	public FerryFlight getProduct() {
		return ferryFlight;
	}

	@Override
	protected void doSetProduct(Product product) {
		ferryFlight = (FerryFlight) product;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.FERRYFLIGHT);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FerryFlightOrder [passengers=").append(passengers).append(", contact=").append(contact)
				.append(", ferryFlight=").append(ferryFlight).append(", chartered=").append(chartered)
				.append(", orderNo=").append(orderNo).append(", status=").append(status).append(", commented=")
				.append(commented).append(", creationDate=").append(creationDate).append(", paymentDate=")
				.append(paymentDate).append(", finishedDate=").append(finishedDate).append(", note=").append(note)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}

}
