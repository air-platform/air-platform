package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * AirTour
 * 
 * @author guankai
 */
@Entity
@Table(name = "air_platfrom_airtour", indexes = { @Index(name = "idx_city", columnList = "city") })
public class AirTour extends AircraftAwareProduct {
	private static final long serialVersionUID = 1L;

	@Column(name = "city", nullable = false)
	private String city;

	@Column(name = "tour_distance")
	private String tourDistance;

	// e.g. time in minutes
	@Column(name = "tour_time")
	private int tourTime;

	@Lob
	@Column(name = "tour_point")
	private String tourPoint;

	@Lob
	@Column(name = "tour_show")
	private String tourShow;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTour [city=").append(city).append(", tourPoint=").append(tourPoint).append(", tourDistance=")
				.append(tourDistance).append(", tourTime=").append(tourTime).append(", tourShow=").append(tourShow)
				.append(", boardingLocation=").append(boardingLocation).append(", traffic=").append(traffic)
				.append(", name=").append(name).append(", score=").append(score).append(", creationDate=")
				.append(creationDate).append(", description=").append(description).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}
}
