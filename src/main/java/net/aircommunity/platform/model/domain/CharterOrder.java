package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.PricePolicy;
import net.aircommunity.platform.model.UnitProductPrice;
import net.aircommunity.platform.model.domain.Product.Type;

/**
 * Charter Order on a {@code Fleet}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_charter_order", indexes = {
		@Index(name = "idx_user_id_status", columnList = "user_id,status,creation_date")//
})
@XmlAccessorType(XmlAccessType.FIELD)
public class CharterOrder extends CandidateOrder<FleetCandidate> implements Cloneable {
	private static final long serialVersionUID = 1L;

	// multiple flight legs (need to remove DUPs, but also requires flightLegs in order)
	// ref: https://stackoverflow.com/questions/19871765/jpa-hibernate-sorted-collection-orderby-vs-sort
	@NotEmpty
	@OrderBy("date ASC")
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private SortedSet<FlightLeg> flightLegs = new TreeSet<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<FleetCandidate> fleetCandidates = new HashSet<>();

	public Set<FlightLeg> getFlightLegs() {
		return flightLegs;
	}

	public void setFlightLegs(Set<FlightLeg> legs) {
		if (legs != null) {
			legs.stream().forEach(flightLeg -> flightLeg.setOrder(this));
			flightLegs.clear();
			flightLegs.addAll(legs);
		}
	}

	@Override
	public Set<FleetCandidate> getCandidates() {
		return fleetCandidates;
	}

	@Override
	public void setCandidates(Set<FleetCandidate> candidates) {
		if (candidates != null) {
			candidates.stream().forEach(fleetCandidate -> fleetCandidate.setOrder(this));
			fleetCandidates.clear();
			fleetCandidates.addAll(candidates);
		}
	}

	// /**
	// * Only allow to select one FleetCandidate, other selected FleetCandidates will be reset to CANDIDATE status.
	// * (Normally called by USER)
	// *
	// * @param candidateId
	// * @return the selected FleetCandidate or Optional.empty
	// */
	// public Optional<FleetCandidate> selectFleetCandidate(String candidateId) {
	// return OrderItemCandidateSupport.selectCandidate(fleetCandidates, candidateId);
	// }
	//
	// /**
	// * Offer the FleetCandidate with the given candidateId, and other FleetCandidates of this tenant will be reset to
	// * CANDIDATE status, that means allow offer multiple fleetCandidate per tenant. (Normally called by TENANT)
	// *
	// * @param fleetCandidateId
	// * @return the selected FleetCandidate or Optional.empty
	// */
	// public Optional<FleetCandidate> offerFleetCandidate(String candidateId, BigDecimal offeredAmount) {
	// return OrderItemCandidateSupport.offerCandidate(fleetCandidates, candidateId, offeredAmount);
	// }
	//
	// /**
	// * Refuse the FleetCandidate with the given candidateId, its status be reset to CANDIDATE status. (Normally called
	// * by TENANT)
	// *
	// * @param fleetCandidateId
	// * @return the refused FleetCandidate or Optional.empty
	// */
	// public Optional<FleetCandidate> refuseFleetCandidate(String candidateId) {
	// return OrderItemCandidateSupport.refuseCandidate(fleetCandidates, candidateId);
	// }

	@Override
	public Fleet getProduct() {
		// quick charter order that selects no fleet
		if (fleetCandidates == null || fleetCandidates.isEmpty()) {
			return null;
		}
		Optional<FleetCandidate> candidate = fleetCandidates.stream()
				.filter(fleetCandidate -> fleetCandidate.getStatus() == FleetCandidate.Status.SELECTED).findFirst();
		if (candidate.isPresent()) {
			return candidate.get().getFleet();
		}
		// Multiple candidates, and none is selected yet
		return null;
	}

	@Override
	public UnitProductPrice getUnitProductPrice() {
		Product product = getProduct();
		BigDecimal unitPrice = BigDecimal.ZERO;
		// if Fleet, the product can be null
		if (product != null && StandardProduct.class.isAssignableFrom(product.getClass())) {
			unitPrice = ((StandardProduct) product).getPrice();
		}
		return new UnitProductPrice(PricePolicy.PER_HOUR, unitPrice);
	}

	@PrePersist
	private void prePersist() {
		setType(Type.FLEET);
	}

	@Override
	public Type getType() {
		// NOTE: specific case for charter, the product can be null
		// because we need check type before @PrePersist for fleet, so it can be null
		if (type == null) {
			return Type.FLEET;
		}
		return type;
	}

	@Override
	public CharterOrder clone() {
		try {
			return (CharterOrder) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CharterOrder [flightLegs=").append(flightLegs).append(", fleetCandidates=")
				.append(fleetCandidates).append(", orderNo=").append(orderNo).append(", pointsUsed=").append(pointsUsed)
				.append(", quantity=").append(quantity).append(", totalPrice=").append(totalPrice).append(", status=")
				.append(status).append(", commented=").append(commented).append(", creationDate=").append(creationDate)
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
