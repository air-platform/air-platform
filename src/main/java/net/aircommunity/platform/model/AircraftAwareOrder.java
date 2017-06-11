package net.aircommunity.platform.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Aircraft Aware Order model has SalesPackage (taxi, tour, trans)
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AircraftAwareOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	// TODO REMOVE --> use quantity
	// the number of package in this order
	// @NotNull
	// @JoinColumn(name = "salespackage_num", nullable = false)
	// protected int salesPackageNum;

	// calculated on order creation
	@Column(name = "salespackage_price", nullable = false)
	protected BigDecimal salesPackagePrice = BigDecimal.ZERO;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "salespackage_id", nullable = false)
	protected SalesPackage salesPackage;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "departure_date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	protected Date departureDate;

	// e.g. 8:00-9:00
	@NotEmpty
	@Column(name = "time_slot", nullable = false)
	protected String timeSlot;

	// passengers
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected Set<PassengerItem> passengers = new HashSet<>();

	public BigDecimal getSalesPackagePrice() {
		return salesPackagePrice;
	}

	public void setSalesPackagePrice(BigDecimal salesPackagePrice) {
		this.salesPackagePrice = salesPackagePrice;
	}

	public SalesPackage getSalesPackage() {
		return salesPackage;
	}

	public void setSalesPackage(SalesPackage salesPackage) {
		this.salesPackage = salesPackage;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public Set<PassengerItem> getPassengers() {
		return passengers;
	}

	public void setPassengers(Set<PassengerItem> items) {
		if (items != null) {
			items.stream().forEach(item -> item.setOrder(this));
			passengers.clear();
			passengers.addAll(items);
		}
	}

}
