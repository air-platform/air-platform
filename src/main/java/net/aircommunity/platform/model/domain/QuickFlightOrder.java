package net.aircommunity.platform.model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ImmutableSet;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * Quick flight order.
 * <p>
 * Order work flow: START -> USER place an order with basic information -> TENANT offer aircraft candidates (CANDIDATE)
 * -> ADMIN prompt some aircraft candidates (e.g. 3 out of 10) among all the candidates (OFFERED) -> USER select 1
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

	// departure date, e.g. 2017-5-1
	@NotNull
	@Temporal(value = TemporalType.DATE)
	@Column(name = "departure_date", nullable = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date departureDate;

	// departure location
	@NotNull
	@Embedded
	@AttributeOverrides({ //
			@AttributeOverride(name = "city", column = @Column(name = "departure_city", length = CITY_NAME_LEN, nullable = false)),
			@AttributeOverride(name = "name", column = @Column(name = "departure_name", length = CITY_NAME_LEN, nullable = false)),
			@AttributeOverride(name = "address", column = @Column(name = "departure_address", length = ADDRESS_LEN, nullable = true)),
			@AttributeOverride(name = "latitude", column = @Column(name = "departure_lat", precision = 10, scale = 8, nullable = false)),
			@AttributeOverride(name = "longitude", column = @Column(name = "departure_lon", precision = 11, scale = 8, nullable = false))//
	})
	private QuickFlightLocation departure;

	// arrival location
	@NotNull
	@Embedded
	@AttributeOverrides({ //
			@AttributeOverride(name = "city", column = @Column(name = "arrival_city", length = CITY_NAME_LEN, nullable = false)),
			@AttributeOverride(name = "name", column = @Column(name = "arrival_name", length = CITY_NAME_LEN, nullable = false)),
			@AttributeOverride(name = "address", column = @Column(name = "arrival_address", length = ADDRESS_LEN, nullable = true)),
			@AttributeOverride(name = "latitude", column = @Column(name = "arrival_lat", precision = 10, scale = 8, nullable = false)),
			@AttributeOverride(name = "longitude", column = @Column(name = "arrival_lon", precision = 11, scale = 8, nullable = false))//
	})
	private QuickFlightLocation arrival;

	// can be null
	@ManyToOne
	@JoinColumn(name = "departure_apron_id")
	private Apron departureApron;

	// can be null
	@ManyToOne
	@JoinColumn(name = "arrival_apron_id")
	private Apron arrivalApron;

	// route order matters (use list)
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<QuickFlightRoute> routes = new ArrayList<>();

	// the number of passengers,
	@Min(1)
	@Column(name = "passengers")
	private int passengerNum;

	// passengers info
	@NotEmpty
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<PassengerItem> passengers = new HashSet<>();

	// candidates
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<AircraftCandidate> aircraftCandidates = new HashSet<>();

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	public QuickFlightLocation getDeparture() {
		return departure;
	}

	public void setDeparture(QuickFlightLocation departure) {
		this.departure = departure;
	}

	public QuickFlightLocation getArrival() {
		return arrival;
	}

	public void setArrival(QuickFlightLocation arrival) {
		this.arrival = arrival;
	}

	public Apron getDepartureApron() {
		return departureApron;
	}

	public void setDepartureApron(Apron departureApron) {
		this.departureApron = departureApron;
	}

	public Apron getArrivalApron() {
		return arrivalApron;
	}

	public void setArrivalApron(Apron arrivalApron) {
		this.arrivalApron = arrivalApron;
	}

	public List<QuickFlightRoute> getRoutes() {
		return routes;
	}

	public void setRoutes(List<QuickFlightRoute> routes) {
		if (routes != null) {
			routes.stream().forEach(product -> product.setOrder(this));
			this.routes.clear();
			this.routes.addAll(ImmutableSet.copyOf(routes));
		}
	}

	public int getPassengerNum() {
		return passengerNum;
	}

	public void setPassengerNum(int passengerNum) {
		this.passengerNum = passengerNum;
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
	public boolean isPayable() {
		return getSelectedCandidate().isPresent() && (status.ordinal() <= Status.CONTRACT_SIGNED.ordinal()
				|| status == Status.PARTIAL_PAID || status == Status.PAYMENT_FAILED);
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
	public String getDescription() {
		Optional<AircraftCandidate> candidate = getSelectedCandidate();
		if (candidate.isPresent()) {
			return candidate.get().getAircraft().getName();
		}
		return null;
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
		builder.append("QuickFlightOrder [payment=").append(payment).append(", refund=").append(refund)
				.append(", orderNo=").append(orderNo).append(", type=").append(type).append(", pointsUsed=")
				.append(pointsUsed).append(", quantity=").append(quantity).append(", totalPrice=").append(totalPrice)
				.append(", originalTotalPrice=").append(originalTotalPrice).append(", currencyUnit=")
				.append(currencyUnit).append(", status=").append(status).append(", channel=").append(channel)
				.append(", commented=").append(commented).append(", creationDate=").append(creationDate)
				.append(", lastModifiedDate=").append(lastModifiedDate).append(", paymentDate=").append(paymentDate)
				.append(", refundedDate=").append(refundedDate).append(", finishedDate=").append(finishedDate)
				.append(", closedDate=").append(closedDate).append(", cancelledDate=").append(cancelledDate)
				.append(", deletedDate=").append(deletedDate).append(", contact=").append(contact)
				.append(", refundReason=").append(refundReason).append(", refundFailureCause=")
				.append(refundFailureCause).append(", paymentFailureCause=").append(paymentFailureCause)
				.append(", cancelReason=").append(cancelReason).append(", closedReason=").append(closedReason)
				.append(", note=").append(note).append(", owner=").append(owner).append(", id=").append(id)
				.append(", departureDate=").append(departureDate).append(", departure=").append(departure)
				.append(", arrival=").append(arrival).append(", departureApron=").append(departureApron)
				.append(", arrivalApron=").append(arrivalApron).append(", passengerNum=").append(passengerNum)
				.append("]");
		return builder.toString();
	}
}
