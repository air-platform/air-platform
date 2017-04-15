package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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

	// product price
	@Min(0)
	@Column(name = "price", nullable = false)
	private int price;

	// single seat price
	@Column(name = "seat_price")
	private int seatPrice;

	// product price CurrencyUnit
	@Column(name = "currency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	private CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	@ManyToOne
	@JoinColumn(name = "aircraft_id")
	private Aircraft aircraft;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getSeatPrice() {
		return seatPrice;
	}

	public void setSeatPrice(int seatPrice) {
		this.seatPrice = seatPrice;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
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
		result = prime * result + ((aircraft == null) ? 0 : aircraft.getId().hashCode());
		result = prime * result + ((product == null) ? 0 : product.getId().hashCode());
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
		else if (!aircraft.getId().equals(other.aircraft.getId()))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		}
		else if (!product.getId().equals(other.product.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AircraftItem [price=").append(price).append(", seatPrice=").append(seatPrice)
				.append(", currencyUnit=").append(currencyUnit).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
