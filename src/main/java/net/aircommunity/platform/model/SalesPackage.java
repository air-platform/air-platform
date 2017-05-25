package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.constraint.PriceList;

/**
 * Sales Package for taxi, tour and transportation.
 * 
 * @author Bin.Zhang
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class SalesPackage implements Serializable {
	private static final long serialVersionUID = 1L;

	// price package name
	@NotEmpty
	@Column(name = "name", nullable = false)
	private String name;

	// the number of passengers of this package
	@Column(name = "passengers", nullable = false)
	private int passengers;

	// TODO add bean-validation @annotation
	// prices for 30days, separated by comma (,) if 0 means the product is not available that day
	// e.g. 1000,2000,0,3000 etc.
	@PriceList
	@Column(name = "prices")
	private String prices;

	// product price CurrencyUnit
	@NotNull
	@Column(name = "currency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	private CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	// price package desc
	@Lob
	@Column(name = "description")
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public String getPrices() {
		return prices;
	}

	public void setPrices(String prices) {
		this.prices = prices;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		SalesPackage other = (SalesPackage) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesPackage [name=").append(name).append(", passengers=").append(passengers)
				.append(", prices=").append(prices).append(", currencyUnit=").append(currencyUnit)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}
}
