package net.aircommunity.platform.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * AirTransport Order on a {@code AirTransport}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airtransport_order")
public class AirTransportOrder extends Order {
	private static final long serialVersionUID = 1L;

	// departure date, e.g. 2017-5-1
	@Temporal(value = TemporalType.DATE)
	@Column(name = "date", nullable = false)
	private Date date;

	// e.g. 8:00-9:00
	@Column(name = "time_slot", nullable = false)
	private String timeSlot;

	@ManyToOne
	@JoinColumn(name = "airtransport_id", nullable = false)
	private AirTransport airTransport;

	// passengers
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Passenger> passengers = new HashSet<>();

}
