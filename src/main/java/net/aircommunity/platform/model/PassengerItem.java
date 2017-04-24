package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.OrderAdapter;

/**
 * Order passenger Item
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_order_passengeritem")
@XmlAccessorType(XmlAccessType.FIELD)
public class PassengerItem extends Persistable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "order_id")
	@XmlJavaTypeAdapter(OrderAdapter.class)
	private Order order;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "passenger_id")
	private Passenger passenger;

	public PassengerItem() {
	}

	public PassengerItem(String id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PassengerItem [order=").append(order == null ? null : order.getId()).append(", passenger=")
				.append(passenger == null ? null : passenger.getId()).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
