package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * AirTaxi Model.
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platfrom_airtaxi")
public class AirTaxi extends SalesPackageProduct {
	private static final long serialVersionUID = 1L;

	@Embedded
	private FlightRoute flightRoute;

	@Column(name = "distance")
	private String distance;

	@Column(name = "duration")
	private String duration;

	public AirTaxi() {
	}

	public AirTaxi(String id) {
		this.id = id;
	}

	public FlightRoute getFlightRoute() {
		return flightRoute;
	}

	public void setFlightRoute(FlightRoute flightRoute) {
		this.flightRoute = flightRoute;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	@Override
	public Category getCategory() {
		if (category == null) {
			return Category.AIR_TAXI;
		}
		return category;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTaxi [flightRoute=").append(flightRoute).append(", distance=").append(distance)
				.append(", duration=").append(duration).append(", name=").append(name).append(", image=").append(image)
				.append(", score=").append(score).append(", totalSales=").append(totalSales).append(", rank=")
				.append(rank).append(", published=").append(published).append(", creationDate=").append(creationDate)
				.append(", clientManagers=").append(clientManagers).append(", description=").append(description)
				.append(", reviewStatus=").append(reviewStatus).append(", rejectedReason=").append(rejectedReason)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}
}
