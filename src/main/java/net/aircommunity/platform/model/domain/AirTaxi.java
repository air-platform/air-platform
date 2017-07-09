package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * AirTaxi Model.
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platform_airtaxi", indexes = {
		@Index(name = "idx_review_status_tenant_id", columnList = "review_status,tenant_id"),
		@Index(name = "idx_published_rank_score", columnList = "published,rank,score")
		// @Index(name = "idx_published_departure_arrval", columnList = "published,departure,arrival")//NOT USED
})
public class AirTaxi extends SalesPackageProduct {
	private static final long serialVersionUID = 1L;

	@Embedded
	private FlightRoute flightRoute;

	// TODO better make it int
	// flying distance in km
	@Size(max = FLYING_DISTANCE_LEN)
	@Column(name = "distance", length = FLYING_DISTANCE_LEN)
	private String distance;

	// TODO better make it int
	// duration in minutes
	@Size(max = FLYING_DURATION_LEN)
	@Column(name = "duration", length = FLYING_DURATION_LEN)
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

	@PrePersist
	private void prePersist() {
		setType(Type.AIRTAXI);
		setCategory(Category.AIR_TAXI);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTaxi [id=").append(id).append(", name=").append(name).append(", type=").append(type)
				.append(", category=").append(category).append(", image=").append(image).append(", stock=")
				.append(stock).append(", score=").append(score).append(", totalSales=").append(totalSales)
				.append(", rank=").append(rank).append(", published=").append(published).append(", creationDate=")
				.append(creationDate).append(", clientManagers=").append(clientManagers).append(", description=")
				.append(description).append(", reviewStatus=").append(reviewStatus).append(", rejectedReason=")
				.append(rejectedReason).append(", flightRoute=").append(flightRoute).append(", distance=")
				.append(distance).append(", duration=").append(duration).append("]");
		return builder.toString();
	}
}
