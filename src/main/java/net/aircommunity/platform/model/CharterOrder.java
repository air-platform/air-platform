package net.aircommunity.platform.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Charter Order on a {@code Fleet}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_charter_order")
@XmlAccessorType(XmlAccessType.FIELD)
public class CharterOrder extends StandardOrder {
	private static final long serialVersionUID = 1L;

	// multiple flight legs
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<FlightLeg> flightLegs = new HashSet<>();

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

	public Set<FleetCandidate> getFleetCandidates() {
		return fleetCandidates;
	}

	public void setFleetCandidates(Set<FleetCandidate> candidates) {
		if (candidates != null) {
			candidates.stream().forEach(fleetCandidate -> fleetCandidate.setOrder(this));
			fleetCandidates.clear();
			fleetCandidates.addAll(candidates);
		}
	}

	public void selectFleetCandidate(String fleetCandidateId) {
		if (fleetCandidateId != null) {
			Optional<FleetCandidate> optional = fleetCandidates.stream()
					.filter(candidate -> candidate.getId().equals(fleetCandidateId)).findFirst();
			if (optional.isPresent()) {
				// clear all status and make the input fleetCandidateId as selected
				fleetCandidates.stream().forEach(candidate -> {
					if (candidate.getStatus() == FleetCandidate.Status.SELECTED) {
						candidate.setStatus(FleetCandidate.Status.CANDIDATE);
					}
				});
				optional.get().setStatus(FleetCandidate.Status.SELECTED);
			}
		}
	}

	public void offerFleetCandidate(String fleetCandidateId) {
		if (fleetCandidateId != null) {
			fleetCandidates.stream().filter(candidate -> candidate.getId().equals(fleetCandidateId)).findFirst()
					.ifPresent(candidate -> candidate.setStatus(FleetCandidate.Status.OFFERED));
		}
	}

	@Override
	public Type getType() {
		return Type.FLEET;
	}

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
	public void setProduct(Product product) {
		// XXX noops, use FleetCandidate only
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
