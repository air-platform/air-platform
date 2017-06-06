package net.aircommunity.platform.model;

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
public class CharterOrder extends Order {
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
				fleetCandidates.stream().forEach(candidate -> candidate.setStatus(FleetCandidate.Status.CANDIDATE));
				optional.get().setStatus(FleetCandidate.Status.SELECTED);
			}
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CharterOrder [contact=").append(contact).append(", flightLegs=").append(flightLegs)
				.append(", fleetCandidates=").append(fleetCandidates).append(", orderNo=").append(orderNo)
				.append(", creationDate=").append(creationDate).append(", paymentDate=").append(paymentDate)
				.append(", finishedDate=").append(finishedDate).append(", note=").append(note).append(", id=")
				.append(id).append("]");
		return builder.toString();
	}
}
