package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	// the number of passengers if NOT chartered
	// @Column(name = "passenger_num")
	// private int passengerNum;

	// // departure date, e.g. 2017-5-1
	// @NotNull
	// @Temporal(value = TemporalType.DATE)
	// @Column(name = "date", nullable = false)
	// @XmlJavaTypeAdapter(DateAdapter.class)
	// private Date date;

	// e.g. 08:00-09:00, HHmm
	// @NotEmpty
	// @Column(name = "time_slot", nullable = false)
	// private String timeSlot;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "airtransport_id", nullable = false)
	private AirTransport airTransport;

	@Override
	public Type getType() {
		return Type.AIRTRANSPORT;
	}

	@Override
	public Product getProduct() {
		return airTransport;
	}

	@Override
	protected void doSetProduct(Product product) {
		airTransport = (AirTransport) product;
	}

	@Override
	public String toString() {
		return toBaseString(getClass());
	}
}