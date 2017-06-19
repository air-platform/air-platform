package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * AirTransport Order on a {@code AirTransport}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airtransport_order")
public class AirTransportOrder extends AircraftAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtransport_id", nullable = false)
	private AirTransport airTransport;

	@Override
	public Product getProduct() {
		return airTransport;
	}

	@Override
	protected void doSetProduct(Product product) {
		airTransport = (AirTransport) product;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.AIRTRANSPORT);
	}

	@Override
	public String toString() {
		return toBaseString(getClass());
	}
}
