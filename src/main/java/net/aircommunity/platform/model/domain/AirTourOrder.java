package net.aircommunity.platform.model.domain;

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
		return toBaseString(getClass());
	}
}