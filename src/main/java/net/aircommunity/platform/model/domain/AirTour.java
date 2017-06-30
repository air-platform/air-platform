package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import io.micro.annotation.constraint.NotEmpty;

/**
 * AirTour Model.
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platform_airtour", indexes = { @Index(name = "idx_city", columnList = "city") })
public class AirTour extends SalesPackageProduct {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "city", length = CITY_NAME_LEN, nullable = false)
	private String city;

	// TODO better make it int
	@Size(max = FLYING_DISTANCE_LEN)
	@Column(name = "tour_distance", length = FLYING_DISTANCE_LEN)
	private String tourDistance;

	// e.g. time in minutes
	@Min(0)
	@Column(name = "tour_time")
	private int tourTime;

	@Lob
	@Column(name = "tour_point")
	private String tourPoint;

	@Lob
	@Column(name = "tour_show")
	private String tourShow;

	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "boarding_location")
	private String boardingLocation;

	@Lob
	@Column(name = "traffic")
	private String traffic;

	@Lob
	@Column(name = "tour_route")
	private String tourRoute;

	public AirTour() {
	}

	public AirTour(String id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTourPoint() {
		return tourPoint;
	}

	public void setTourPoint(String tourPoint) {
		this.tourPoint = tourPoint;
	}

	public String getTourDistance() {
		return tourDistance;
	}

	public void setTourDistance(String tourDistance) {
		this.tourDistance = tourDistance;
	}

	public int getTourTime() {
		return tourTime;
	}

	public void setTourTime(int tourTime) {
		this.tourTime = tourTime;
	}

	public String getTourShow() {
		return tourShow;
	}

	public void setTourShow(String tourShow) {
		this.tourShow = tourShow;
	}

	public String getBoardingLocation() {
		return boardingLocation;
	}

	public void setBoardingLocation(String boardingLocation) {
		this.boardingLocation = boardingLocation;
	}

	public String getTraffic() {
		return traffic;
	}

	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	public String getTourRoute() {
		return tourRoute;
	}

	public void setTourRoute(String tourRoute) {
		this.tourRoute = tourRoute;
	}

	@PrePersist
	private void prePersist() {
		setCategory(Category.AIR_TOUR);
	}

	@Override
	public Type getType() {
		return Type.AIRTOUR;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTour [city=").append(city).append(", tourDistance=").append(tourDistance)
				.append(", tourTime=").append(tourTime).append(", tourPoint=").append(tourPoint).append(", tourShow=")
				.append(tourShow).append(", boardingLocation=").append(boardingLocation).append(", traffic=")
				.append(traffic).append(", tourRoute=").append(tourRoute).append(", name=").append(name)
				.append(", image=").append(image).append(", score=").append(score).append(", totalSales=")
				.append(totalSales).append(", rank=").append(rank).append(", published=").append(published)
				.append(", creationDate=").append(creationDate).append(", clientManagers=").append(clientManagers)
				.append(", description=").append(description).append(", reviewStatus=").append(reviewStatus)
				.append(", rejectedReason=").append(rejectedReason).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
