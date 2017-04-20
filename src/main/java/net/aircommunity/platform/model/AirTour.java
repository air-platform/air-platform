package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * AirTour
 */
@Entity
@Table(name = "air_platfrom_airtour")
public class AirTour extends AircraftAwareProduct {
	private static final long serialVersionUID = 1L;

	@Column(name = "city", nullable = false)
	private String city;

	@Column(name = "tour_point")
	private String tourPoint;

	@Column(name = "tour_distance")
	private String tourDistance;

	@Column(name = "tour_time")
	private String tourTime;

	@Lob
	@Column(name = "tour_show")
	private String tourShow;

	@Column(name = "boarding_loc")
	private String boardingLoc;

	@Column(name = "traffic")
	private String traffic;

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

	public String getTourTime() {
		return tourTime;
	}

	public void setTourTime(String tourTime) {
		this.tourTime = tourTime;
	}

	public String getTourShow() {
		return tourShow;
	}

	public void setTourShow(String tourShow) {
		this.tourShow = tourShow;
	}

	public String getBoardingLoc() {
		return boardingLoc;
	}

	public void setBoardingLoc(String boardingLoc) {
		this.boardingLoc = boardingLoc;
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
				.append(", boardingLoc=").append(boardingLoc).append(", traffic=").append(traffic).append(", name=")
				.append(name).append(", score=").append(score).append(", creationDate=").append(creationDate)
				.append(", description=").append(description).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
