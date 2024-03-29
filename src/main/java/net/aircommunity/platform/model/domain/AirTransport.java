package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.ProductFamilyAdapter;

/**
 * Air Transport
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_airtransport", indexes = {
		@Index(name = "idx_review_status_tenant_id", columnList = "review_status,tenant_id"),
		@Index(name = "idx_published_rank_score", columnList = "published,rank,score")//
})
public class AirTransport extends SalesPackageProduct {
	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "family_id", nullable = false)
	@XmlJavaTypeAdapter(ProductFamilyAdapter.class)
	private ProductFamily family;

	// in minutes
	@Min(1)
	@Column(name = "time_estimation", nullable = false)
	private int timeEstimation;

	@Embedded
	private FlightRoute flightRoute;

	public AirTransport() {
	}

	public AirTransport(String id) {
		this.id = id;
	}

	public ProductFamily getFamily() {
		return family;
	}

	public void setFamily(ProductFamily family) {
		this.family = family;
	}

	public int getTimeEstimation() {
		return timeEstimation;
	}

	public void setTimeEstimation(int timeEstimation) {
		this.timeEstimation = timeEstimation;
	}

	public FlightRoute getFlightRoute() {
		return flightRoute;
	}

	public void setFlightRoute(FlightRoute flightRoute) {
		this.flightRoute = flightRoute;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.AIRTRANSPORT);
		setCategory(Category.AIR_TRANS);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirTransport [id=").append(id).append(", name=").append(name).append(", type=").append(type)
				.append(", category=").append(category).append(", image=").append(image).append(", stock=")
				.append(stock).append(", score=").append(score).append(", totalSales=").append(totalSales)
				.append(", rank=").append(rank).append(", published=").append(published).append(", creationDate=")
				.append(creationDate).append(", clientManagers=").append(clientManagers).append(", description=")
				.append(description).append(", reviewStatus=").append(reviewStatus).append(", rejectedReason=")
				.append(rejectedReason).append(", family=").append(family).append(", timeEstimation=")
				.append(timeEstimation).append(", flightRoute=").append(flightRoute).append("]");
		return builder.toString();
	}
}
