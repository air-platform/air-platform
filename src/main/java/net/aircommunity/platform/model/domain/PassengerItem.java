package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.OrderAdapter;

/**
 * Order passenger Item
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_order_passengeritem")
@XmlAccessorType(XmlAccessType.FIELD)
public class PassengerItem extends Persistable {
	private static final long serialVersionUID = 1L;

	// Passenger name
	@XmlTransient
	@Size(max = 255)
	@Column(name = "name")
	private String name;

	// Passenger mobile
	@XmlTransient
	@Size(max = 255)
	@Column(name = "mobile")
	private String mobile;

	// e.g. ID Card number
	@XmlTransient
	@Size(max = 255)
	@Column(name = "identity")
	private String identity;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "order_id")
	@XmlJavaTypeAdapter(OrderAdapter.class)
	private Order order;

	@NotNull
	@XmlElement
	@Transient
	private Passenger passenger;

	public PassengerItem() {
	}

	public PassengerItem(String id) {
		this.id = id;
	}

	@PostLoad
	private void onLoad() {
		passenger = new Passenger();
		passenger.setMobile(mobile);
		passenger.setName(name);
		passenger.setIdentity(identity);
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
		setIdentity(passenger.getIdentity());
		setMobile(passenger.getMobile());
		setName(passenger.getName());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PassengerItem [order=").append(order).append(", name=").append(name).append(", mobile=")
				.append(mobile).append(", identity=").append(identity).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
