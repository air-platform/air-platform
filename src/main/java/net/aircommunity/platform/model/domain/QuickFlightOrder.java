package net.aircommunity.platform.model.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Quick flight order.
 * <p>
 * Order work flow: START -> USER place an order with basic information -> TENANT offer aircraft candidates (CANDIDATE)
 * -> ADMIN prompt some aircraft candidates (e.g. 3 out of 10) among all the candidates (OFFERED) -> USER select one
 * candidate among the offered candidates (SELECTED) -> END
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_quickflight_order", indexes = {
		@Index(name = "idx_user_id_status", columnList = "user_id,status,creation_date")//
})
public class QuickFlightOrder extends CandidateOrder<AircraftCandidate> implements Cloneable {
	private static final long serialVersionUID = 1L;

	// the number of passengers,
	@Min(1)
	@Column(name = "passengers")
	private int passengers;

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "departure_date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date departureDate;

	// departure city
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "departure_city", length = CITY_NAME_LEN, nullable = false)
	private String departureCity;

	// departure location
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "departure_location", length = CITY_NAME_LEN, nullable = false)
	private String departureLocation;

	// arrival city
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "arrival_city", length = CITY_NAME_LEN, nullable = false)
	private String arrivalCity;

	// arrival location
	@NotEmpty
	@Size(max = CITY_NAME_LEN)
	@Column(name = "arrival_location", length = CITY_NAME_LEN, nullable = false)
	private String arrivalLocation;

	// candidates
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<AircraftCandidate> aircraftCandidates = new HashSet<>();

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public String getDepartureCity() {
		return departureCity;
	}

	public void setDepartureCity(String departureCity) {
		this.departureCity = departureCity;
	}

	public String getDepartureLocation() {
		return departureLocation;
	}

	public void setDepartureLocation(String departureLocation) {
		this.departureLocation = departureLocation;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}

	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}

	public String getArrivalLocation() {
		return arrivalLocation;
	}

	public void setArrivalLocation(String arrivalLocation) {
		this.arrivalLocation = arrivalLocation;
	}

	@Override
	public Set<AircraftCandidate> getCandidates() {
		return aircraftCandidates;
	}

	@Override
	public void setCandidates(Set<AircraftCandidate> candidates) {
		if (candidates != null) {
			candidates.stream().forEach(candidate -> candidate.setOrder(this));
			aircraftCandidates.clear();
			aircraftCandidates.addAll(candidates);
		}
	}

	public void initiateCandidates(Set<AircraftCandidate> candidates) {
		if (candidates != null && !candidates.isEmpty()) {
			// Calculate all the related tenants
			Map<String, List<AircraftCandidate>> grouped = candidates.stream()
					.collect(Collectors.groupingBy(candidate -> candidate.getVendor().getId()));
			// Remove all the candidates of the related tenant
			aircraftCandidates.removeIf(candidate -> grouped.containsKey(candidate.getVendor().getId()));
			// Add with new candidates
			candidates.stream().forEach(candidate -> candidate.setOrder(this));
			aircraftCandidates.addAll(candidates);
		}
	}

	/**
	 * promote a set of candidateIds to status OFFERED (called by ADMIN)
	 * 
	 * @param candidateIds the candidateIds
	 */
	public void promoteCandidate(Set<String> candidateIds) {
		candidateIds.stream()
				.forEach(candidateId -> OrderItemCandidateSupport.offerCandidate(getCandidates(), candidateId, null));
	}

	@Override
	public Type getType() {
		// NOTE: specific case, the product is always null
		// because we need check type before @PrePersist
		if (type == null) {
			return Type.QUICKFLIGHT;
		}
		return type;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.QUICKFLIGHT);
	}

	@Override
	public QuickFlightOrder clone() {
		try {
			return (QuickFlightOrder) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuickFlightOrder [id=").append(id).append(", orderNo=").append(orderNo).append(", type=")
				.append(type).append(", pointsUsed=").append(pointsUsed).append(", quantity=").append(quantity)
				.append(", totalPrice=").append(totalPrice).append(", originalTotalPrice=").append(originalTotalPrice)
				.append(", currencyUnit=").append(currencyUnit).append(", status=").append(status).append(", channel=")
				.append(channel).append(", commented=").append(commented).append(", creationDate=").append(creationDate)
				.append(", lastModifiedDate=").append(lastModifiedDate).append(", paymentDate=").append(paymentDate)
				.append(", refundedDate=").append(refundedDate).append(", finishedDate=").append(finishedDate)
				.append(", closedDate=").append(closedDate).append(", cancelledDate=").append(cancelledDate)
				.append(", deletedDate=").append(deletedDate).append(", contact=").append(contact)
				.append(", refundReason=").append(refundReason).append(", refundFailureCause=")
				.append(refundFailureCause).append(", paymentFailureCause=").append(paymentFailureCause)
				.append(", cancelReason=").append(cancelReason).append(", closedReason=").append(closedReason)
				.append(", note=").append(note).append(", passengers=").append(passengers).append(", departureDate=")
				.append(departureDate).append(", departureCity=").append(departureCity).append(", departureLocation=")
				.append(departureLocation).append(", arrivalCity=").append(arrivalCity).append(", arrivalLocation=")
				.append(arrivalLocation).append("]");
		return builder.toString();
	}
}
