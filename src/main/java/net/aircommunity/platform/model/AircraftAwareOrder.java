package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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

	public AircraftItem getAircraftItem() {
		return aircraftItem;
	}

	public void setAircraftItem(AircraftItem aircraftItem) {
		this.aircraftItem = aircraftItem;
	}

}
