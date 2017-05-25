package net.aircommunity.platform.model;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.ProductAdapter;

/**
 * Aircraft Item
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_aircraftitem")
@XmlAccessorType(XmlAccessType.FIELD)
public class AircraftItem extends Persistable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Embedded
	private List<SalesPackage> salesPackages;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "aircraft_id")
	private Aircraft aircraft;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "product_id")
	@XmlJavaTypeAdapter(ProductAdapter.class)
	private Product product;

	public AircraftItem() {
	}

	public AircraftItem(String id) {
		this.id = id;
	}

	public List<SalesPackage> getSalesPackages() {
		return salesPackages;
	}

	public void setSalesPackages(List<SalesPackage> salesPackages) {
		this.salesPackages = salesPackages;
	}

	public Aircraft getAircraft() {
		return aircraft;
	}

	public void setAircraft(Aircraft aircraft) {
		this.aircraft = aircraft;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aircraft == null) ? 0 : aircraft.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AircraftItem other = (AircraftItem) obj;
		if (aircraft == null) {
			if (other.aircraft != null)
				return false;
		}
		else if (!aircraft.equals(other.aircraft))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		}
		else if (!product.equals(other.product))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AircraftItem [salesPackages=").append(salesPackages).append(", aircraft=").append(aircraft)
				.append(", product=").append(product).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
