package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Jet Card Product
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_jetcard")
public class JetCard extends Product {
	private static final long serialVersionUID = 1L;

	// card type
	@Column(name = "type", nullable = false)
	private String type;

	// card intro summary
	@Column(name = "summary")
	private String summary;

	public JetCard() {
	}

	public JetCard(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JetCard [type=").append(type).append(", summary=").append(summary).append(", name=")
				.append(name).append(", price=").append(price).append(", currencyUnit=").append(currencyUnit)
				.append(", description=").append(description).append(", id=").append(id).append("]");
		return builder.toString();
	}

}
