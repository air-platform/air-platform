package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;

/**
 * Product of an {@code Tenant} with price.
 * 
 * @author Bin.Zhang
 */
public abstract class PricedProduct extends Product {
	private static final long serialVersionUID = 1L;

	// product price
	@Min(0)
	@Column(name = "price", nullable = false)
	protected int price;

	// product price CurrencyUnit
	@Column(name = "currency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	protected CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

}
