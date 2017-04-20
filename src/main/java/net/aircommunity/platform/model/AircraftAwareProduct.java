package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected Set<AircraftItem> aircraftItems = new HashSet<>();

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