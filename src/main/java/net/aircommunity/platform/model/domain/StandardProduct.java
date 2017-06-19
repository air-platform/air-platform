package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.aircommunity.platform.model.CurrencyUnit;

/**
 * Standard product with a single price.
 * 
 * @author Bin.Zhang
 */
@Entity
public abstract class StandardProduct extends Product {
	private static final long serialVersionUID = 1L;

	// @Digits(integer = 11, fraction = 2)
	// @Column(name = "price", precision = 9, scale = 2, nullable = false) // DECIMAL(11,2)

	// product price
	@Column(name = "price", nullable = false)
	protected BigDecimal price = BigDecimal.ZERO;

	// product price CurrencyUnit
	@Column(name = "currency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	protected CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

}