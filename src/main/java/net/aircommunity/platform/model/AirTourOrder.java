package net.aircommunity.platform.model;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by guankai on 15/04/2017.
 */
public class AirTourOrder extends Order {

    private static final long serialVersionUID = 1L;

    // departure date, e.g. 2017-5-1
    @Temporal(value = TemporalType.DATE)
    @Column(name = "date", nullable = false)
    private Date date;

    // e.g. 8:00-9:00
    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @ManyToOne
    @JoinColumn(name = "airtour_id", nullable = false)
    private AirTour airTour;

    // passengers
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Passenger> passengers = new HashSet<>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public AirTour getAirTour() {
        return airTour;
    }

    public void setAirTour(AirTour airTour) {
        this.airTour = airTour;
    }

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }
}
