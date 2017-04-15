package net.aircommunity.platform.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * AirTour
 */
@Entity
@Table(name = "air_platfrom_airtour")
public class AirTour extends Product {

    private static final long serialVersionUID = 1L;

    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "tour_point", nullable = false)
    private String tourPoint;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AircraftItem> aircraftItems = new HashSet<>();

    @Column(name = "tour_distance", nullable = false)
    private String tourDistance;

    @Column(name = "tour_time", nullable = false)
    private String tourTime;

    @Lob
    @Column(name = "tour_show", nullable = false)
    private String tourShow;

    @Column(name = "boarding_loc", nullable = false)
    private String boardingLoc;

    @Column(name = "traffic", nullable = false)
    private String traffic;


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

    public Set<AircraftItem> getAircraftItems() {
        return aircraftItems;
    }

    public void setAircraftItems(Set<AircraftItem> aircraftItems) {
        if (aircraftItems != null) {
            aircraftItems.stream().forEach(item -> item.setProduct(this));
            this.aircraftItems = aircraftItems;
        }
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
}
