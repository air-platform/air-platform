package net.aircommunity.platform.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.aircommunity.platform.model.constraint.NotEmpty;

/**
 * Charter Order on a {@code Fleet}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_charter_order")
public class CharterOrder extends Order {
	private static final long serialVersionUID = 1L;

	// customer contact information for this order
	@Embedded
	protected Contact contact;

	// multiple flight legs
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<FlightLeg> flightLegs = new HashSet<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<FleetCandidate> fleetCandidates = new HashSet<>();

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Set<FlightLeg> getFlightLegs() {
		return flightLegs;
	}

	public void setFlightLegs(Set<FlightLeg> flightLegs) {
		if (flightLegs != null) {
			flightLegs.stream().forEach(flightLeg -> flightLeg.setOrder(this));
			this.flightLegs.addAll(flightLegs);
		}
	}

	public Set<FleetCandidate> getFleetCandidates() {
		return fleetCandidates;
	}

	public void setFleetCandidates(Set<FleetCandidate> fleetCandidates) {
		if (fleetCandidates != null) {
			fleetCandidates.stream().forEach(fleetCandidate -> fleetCandidate.setOrder(this));
			this.fleetCandidates = fleetCandidates;
		}
	}

	public void selectFleetCandidate(String fleetCandidateId) {
		if (fleetCandidateId != null) {
			fleetCandidates.stream().filter(candidate -> candidate.getId().equals(fleetCandidateId)).findFirst()
					.ifPresent(candidate -> candidate.setStatus(FleetCandidate.Status.SELECTED));
		}
	}

	@Override
	public Product getProduct() {
		if (fleetCandidates == null || fleetCandidates.isEmpty()) {
			return null;
		}
		Optional<FleetCandidate> candidate = fleetCandidates.stream()
				.filter(fleetCandidate -> fleetCandidate.getStatus() == FleetCandidate.Status.SELECTED).findFirst();
		if (candidate.isPresent()) {
			return candidate.get().getFleet();
		}
		return null;
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
