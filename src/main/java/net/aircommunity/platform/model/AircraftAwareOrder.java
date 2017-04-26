package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Aircraft Aware Order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AircraftAwareOrder extends CharterableOrder {
	private static final long serialVersionUID = 1L;

	// selected aircraftItem
	@NotNull
	@ManyToOne
	@JoinColumn(name = "aircraftitem_id", nullable = false)
	protected AircraftItem aircraftItem;

	// passengers
	@NotEmpty
	protected transient Set<PassengerItem> passengers = new HashSet<>();

	// customer contact information for this order
	@Embedded
	protected Contact contact;

	public AircraftItem getAircraftItem() {
		return aircraftItem;
	}

	public void setAircraftItem(AircraftItem aircraftItem) {
		this.aircraftItem = aircraftItem;
	}

	public Set<PassengerItem> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<PassengerItem> passengers) {
		if (passengers != null) {
			passengers.stream().forEach(item -> item.setOrder(this));
			this.passengers.clear();
			this.passengers.addAll(passengers);
		}
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

}
