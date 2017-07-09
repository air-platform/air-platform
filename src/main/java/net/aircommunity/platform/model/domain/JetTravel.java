package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * Jet travel Product
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_jettravel", indexes = {
		@Index(name = "idx_review_status_tenant_id", columnList = "review_status,tenant_id"),
		@Index(name = "idx_published_rank_score", columnList = "published,rank,score")//
})
public class JetTravel extends StandardProduct {
	private static final long serialVersionUID = 1L;

	public JetTravel() {
	}

	public JetTravel(String id) {
		this.id = id;
	}

	@PrePersist
	private void prePersist() {
		setType(Type.JETTRAVEL);
		setCategory(Category.AIR_JET);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JetTravel [id=").append(id).append(", price=").append(price).append(", currencyUnit=")
				.append(currencyUnit).append(", name=").append(name).append(", type=").append(type)
				.append(", category=").append(category).append(", image=").append(image).append(", stock=")
				.append(stock).append(", score=").append(score).append(", totalSales=").append(totalSales)
				.append(", rank=").append(rank).append(", published=").append(published).append(", creationDate=")
				.append(creationDate).append(", clientManagers=").append(clientManagers).append(", description=")
				.append(description).append(", reviewStatus=").append(reviewStatus).append(", rejectedReason=")
				.append(rejectedReason).append("]");
		return builder.toString();
	}
}
