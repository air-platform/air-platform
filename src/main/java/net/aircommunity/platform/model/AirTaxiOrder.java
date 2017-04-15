package net.aircommunity.platform.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

public class AirTaxiOrder extends Order {
    private static final long serialVersionUID = 1L;

    // departure date, e.g. 2017-5-1
    @Temporal(value = TemporalType.DATE)
    @Column(name = "date", nullable = false)
    private Date date;

    // e.g. 8:00-9:00
    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @ManyToOne
    @JoinColumn(name = "airtaxi_id", nullable = false)
    private AirTaxi airTaxi;

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

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }

    public AirTaxi getAirTaxi() {
        return airTaxi;
    }

    public void setAirTaxi(AirTaxi airTaxi) {
        this.airTaxi = airTaxi;
    }
}
