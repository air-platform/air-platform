package net.aircommunity.platform.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * AircraftAware Product of an {@code Tenant}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AircraftAwareProduct extends Product {
	private static final long serialVersionUID = 1L;

	// available date, e.g. 2017-5-1
	@Temporal(value = TemporalType.DATE)
	@Column(name = "available_date")
	@XmlJavaTypeAdapter(DateAdapter.class)
	protected Date availableDate;

	// in presalesDays before
	@Column(name = "presales_days")
	protected int presalesDays;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected Set<AircraftItem> aircraftItems = new HashSet<>();

	public Date getAvailableDate() {
		return availableDate;
	}

	public void setAvailableDate(Date availableDate) {
		this.availableDate = availableDate;
	}

	public int getPresalesDays() {
		return presalesDays;
	}

	public void setPresalesDays(int presalesDays) {
		this.presalesDays = presalesDays;
	}

	public Set<AircraftItem> getAircraftItems() {
		return aircraftItems;
	}

	public void setAircraftItems(Set<AircraftItem> aircraftItems) {
		if (aircraftItems != null) {
			aircraftItems.stream().forEach(item -> item.setProduct(this));
			this.aircraftItems.clear();
			this.aircraftItems.addAll(aircraftItems);
		}
	}

	public boolean addAircraftItem(AircraftItem item) {
		if (aircraftItems.contains(item)) {
			return false;
		}
		item.setProduct(this);
		return aircraftItems.add(item);
	}

	public boolean removeAircraftItem(AircraftItem item) {
		if (!aircraftItems.contains(item)) {
			return false;
		}
		return aircraftItems.remove(item);
	}
}