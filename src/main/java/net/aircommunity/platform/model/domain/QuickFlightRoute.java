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

	@Min(0)
	//@Column(name = "ordinal_no", nullable = false, columnDefinition = "int default 0")
	@XmlTransient
	@Column(name = "ordinal_no")
	private Integer ordinalNo = 0;

	@XmlTransient
	@NotNull
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	public Integer getOrdinalNo() {
		return ordinalNo;
	}

	public void setOrdinalNo(Integer ordinalNo) {
		this.ordinalNo = ordinalNo;
	}

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + distance;
		result = prime * result + duration;
		if (ordinalNo != null) {
			result = prime * result + ordinalNo.intValue();
		}
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuickFlightRoute other = (QuickFlightRoute) obj;
		if (distance != other.distance)
			return false;
		if (duration != other.duration)
			return false;
		if (type != other.type)
			return false;
		if (ordinalNo != other.ordinalNo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuickFlightRoute [id=").append(id).append(", type=").append(type).append(", distance=")
				.append(distance).append(", duration=").append(duration).append("]");
		return builder.toString();
	}

	public enum RouteType {
		CAR, FLIGHT
	}

}
