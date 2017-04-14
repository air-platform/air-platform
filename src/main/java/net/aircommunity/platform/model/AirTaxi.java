package net.aircommunity.platform.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * AirTaxi
 */
@Entity
@Table(name = "air_platfrom_airtaxi")
public class AirTaxi extends Product {
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AircraftItem> aircraftItems = new HashSet<>();

    @Column(name = "departure", nullable = false)
    private String departure;

    @Column(name = "arrvial", nullable = false)
    private String arrival;

    @Column(name = "depart_loc", nullable = false)
    private String departLoc;

    @Column(name = "arrival_loc", nullable = false)
    private String arrivalLoc;

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDepartLoc() {
        return departLoc;
    }

    public void setDepartLoc(String departLoc) {
        this.departLoc = departLoc;
    }

    public String getArrivalLoc() {
        return arrivalLoc;
    }

    public void setArrivalLoc(String arrivalLoc) {
        this.arrivalLoc = arrivalLoc;
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
}
