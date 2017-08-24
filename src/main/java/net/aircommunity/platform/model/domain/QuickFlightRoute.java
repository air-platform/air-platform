package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Quick Flight Route.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_quickflight_route")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuickFlightRoute extends Persistable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(name = "type", length = FLIGHT_ROUTE_TYPE_LEN, nullable = false)
	private RouteType type = RouteType.FLIGHT;

	// how far in meters for this route
	@Min(0)
	@Column(name = "distance")
	private int distance;

	// how long time it takes for this route in minutes
	@Min(0)
	@Column(name = "duration")
	private int duration;

	@XmlTransient
	@NotNull
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	public RouteType getType() {
		return type;
	}

	public void setType(RouteType type) {
		this.type = type;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuickFlightRoute [id=").append(id).append(", type=").append(type).append(", distance=")
				.append(distance).append(", duration=").append(duration).append("]");
		return builder.toString();
	}

	public enum RouteType {
		CAR, TAXI, FLIGHT
	}

}
