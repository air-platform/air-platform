package net.aircommunity.platform.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class AirTaxiOrder extends Order {
	private static final long serialVersionUID = 1L;

	// date, e.g. 2017-5-1
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	private Date date;

	// 0-24 hour
	@Column(name = "time", nullable = false)
	private int time;

	// SpecicalOffer (接送机15, 机场停车24), details

	// passengers
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Passenger> passengers = new HashSet<>();

}
