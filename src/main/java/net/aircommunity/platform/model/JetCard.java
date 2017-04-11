package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Jet Card Product
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_jetcard")
public class JetCard extends Persistable {
	private static final long serialVersionUID = 1L;

	// card name, e.g. Deer Jet Hour Card
	@Column(name = "name", nullable = false)
	private String name;

	// card type
	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "price", nullable = false)
	private int price;

	@Column(name = "unit", nullable = false)
	@Enumerated(EnumType.STRING)
	private CurrencyUnit unit;

	// card intro summary
	@Column(name = "summary")
	private String summary;

	// card description, e.g. hour card: 25hours
	@Lob
	@Column(name = "description")
	private String description;

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	private Tenant owner;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public CurrencyUnit getUnit() {
		return unit;
	}

	public void setUnit(CurrencyUnit unit) {
		this.unit = unit;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Tenant getOwner() {
		return owner;
	}

	public void setOwner(Tenant owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JetCard [name=").append(name).append(", type=").append(type).append(", price=").append(price)
				.append(", unit=").append(unit).append(", summary=").append(summary).append(", description=")
				.append(description).append("]");
		return builder.toString();
	}

}
