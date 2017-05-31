package net.aircommunity.platform.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

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

	// TODO a better place?
	@Transient
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date currentTime;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected Set<SalesPackage> salesPackages = new HashSet<>();

	@PostLoad
	private void afterLoad() {
		setCurrentTime(new Date());
	}

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}

	public Set<SalesPackage> getSalesPackages() {
		return salesPackages;
	}

	public void setSalesPackages(Set<SalesPackage> salesPackages) {
		if (salesPackages != null) {
			salesPackages.stream().forEach(item -> item.setProduct(this));
			this.salesPackages.clear();
			this.salesPackages.addAll(salesPackages);
		}
	}

	public boolean addSalesPackage(SalesPackage item) {
		if (salesPackages.contains(item)) {
			return false;
		}
		item.setProduct(this);
		return salesPackages.add(item);
	}

	public boolean removeSalesPackage(SalesPackage item) {
		if (!salesPackages.contains(item)) {
			return false;
		}
		return salesPackages.remove(item);
	}
}