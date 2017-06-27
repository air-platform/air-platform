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
 * AirTaxi Order.
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platform_airtaxi_order", indexes = {
		@Index(name = "idx_user_id_status", columnList = "user_id,status,creation_date"),
		@Index(name = "idx_tenant_id_status", columnList = "tenant_id,status,creation_date")
		//
})
public class AirTaxiOrder extends AircraftAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtaxi_id", nullable = false)
	private AirTaxi airTaxi;

	@Override
	public AirTaxi getProduct() {
		return airTaxi;
	}

	@Override
	protected void doSetProduct(Product product) {
		airTaxi = (AirTaxi) product;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.AIRTAXI);
	}

	@Override
	public String toString() {
		return toBaseString(getClass());
	}

}
