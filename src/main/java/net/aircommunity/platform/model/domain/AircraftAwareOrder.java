package net.aircommunity.platform.model.domain;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.PricePolicy;
import net.aircommunity.platform.model.UnitProductPrice;
import net.aircommunity.platform.model.constraint.TimeSlot;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Aircraft aware order model that has multiple prices a.k.a. SalesPackage (for taxi, tour, trans)
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AircraftAwareOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "salespackage_id", nullable = false)
	protected OrderSalesPackage salesPackage;

	// calculated on order creation
	@NotNull
	@Column(name = "salespackage_price", nullable = false)
	protected BigDecimal salesPackagePrice = BigDecimal.ZERO;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "departure_date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	protected Date departureDate;

	// e.g. 8:00-9:00
	@NotEmpty
	@TimeSlot
	@Size(max = TIMESLOT_LEN)
	@Column(name = "time_slot", length = TIMESLOT_LEN, nullable = false)
	protected String timeSlot;

	// passengers
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected Set<PassengerItem> passengers = new HashSet<>();

	public OrderSalesPackage getSalesPackage() {
		return salesPackage;
	}

	public void setSalesPackage(OrderSalesPackage salesPackage) {
		this.salesPackage = salesPackage;
	}

	public BigDecimal getSalesPackagePrice() {
		return salesPackagePrice;
	}

	public void setSalesPackagePrice(BigDecimal salesPackagePrice) {
		this.salesPackagePrice = salesPackagePrice;
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

	@Override
	public UnitProductPrice getUnitProductPrice() {
		return new UnitProductPrice(PricePolicy.SALES_PACKAGE, salesPackagePrice);
	}

	protected String toBaseString(Class<?> type) {
		StringBuilder builder = new StringBuilder();
		builder.append(type.getSimpleName()).append(" [salesPackagePrice=").append(salesPackagePrice)
				.append(", departureDate=").append(departureDate).append(", timeSlot=").append(timeSlot)
				.append(", orderNo=").append(orderNo).append(", pointsUsed=").append(pointsUsed).append(", quantity=")
				.append(quantity).append(", totalPrice=").append(totalPrice).append(", status=").append(status)
				.append(", commented=").append(commented).append(", creationDate=").append(creationDate)
				.append(", lastModifiedDate=").append(lastModifiedDate).append(", paymentDate=").append(paymentDate)
				.append(", refundedDate=").append(refundedDate).append(", finishedDate=").append(finishedDate)
				.append(", closedDate=").append(closedDate).append(", cancelledDate=").append(cancelledDate)
				.append(", deletedDate=").append(deletedDate).append(", contact=").append(contact)
				.append(", refundReason=").append(refundReason).append(", refundFailureCause=")
				.append(refundFailureCause).append(", closedReason=").append(closedReason).append(", note=")
				.append(note).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
