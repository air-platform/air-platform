package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public abstract class AircraftAwareOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	// the number of package in this order
	@NotNull
	@JoinColumn(name = "salespackage_num", nullable = false)
	protected int salesPackageNum;

	// selected salesPackage
	@NotNull
	@ManyToOne
	@JoinColumn(name = "salespackage_id", nullable = false)
	protected SalesPackage salesPackage;

	// passengers
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected Set<PassengerItem> passengers = new HashSet<>();

	// customer contact information for this order
	@Embedded
	protected Contact contact;

	public int getSalesPackageNum() {
		return salesPackageNum;
	}

	public void setSalesPackageNum(int salesPackageNum) {
		this.salesPackageNum = salesPackageNum;
	}

	public SalesPackage getSalesPackage() {
		return salesPackage;
	}

	public void setSalesPackage(SalesPackage salesPackage) {
		this.salesPackage = salesPackage;
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
