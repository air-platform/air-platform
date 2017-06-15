package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Jet travel Product
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_jettravel")
public class JetTravel extends PricedProduct {
	private static final long serialVersionUID = 1L;

	public JetTravel() {
	}

	public JetTravel(String id) {
		this.id = id;
	}

	@Override
	public Category getCategory() {
		if (category == null) {
			return Category.AIR_JET;
		}
		return category;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JetTravel [price=").append(price).append(", currencyUnit=").append(currencyUnit)
				.append(", name=").append(name).append(", image=").append(image).append(", score=").append(score)
				.append(", totalSales=").append(totalSales).append(", rank=").append(rank).append(", published=")
				.append(published).append(", creationDate=").append(creationDate).append(", clientManagers=")
				.append(clientManagers).append(", description=").append(description).append(", reviewStatus=")
				.append(reviewStatus).append(", rejectedReason=").append(rejectedReason).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}
}
