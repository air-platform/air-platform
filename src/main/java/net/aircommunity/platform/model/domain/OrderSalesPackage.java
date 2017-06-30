package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.CurrencyUnit;
import net.aircommunity.platform.model.jaxb.ProductAdapter;

/**
 * Sales Package for an order (one to one mapping), it's an snapshot of original {@code SalesPackage}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_order_salespackage")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderSalesPackage extends Persistable {
	private static final long serialVersionUID = 1L;

	// price package name
	@Size(max = SALES_PACKAGE_NAME_LEN)
	@Column(name = "name", length = SALES_PACKAGE_NAME_LEN, nullable = false)
	private String name;

	// the number of passengers of this package
	@Column(name = "passengers", nullable = false)
	private int passengers;

	// prices for 30days, separated by comma (,) if 0 means the product is not available that day
	// e.g. 1000,2000,0,3000 etc.
	@Size(max = SALES_PACKAGE_PRICES_LEN)
	@Column(name = "prices", length = SALES_PACKAGE_PRICES_LEN)
	private String prices;

	// in presalesDays before
	@Min(0)
	@Column(name = "presales_days")
	private int presalesDays = 0;

	// product price CurrencyUnit
	@Enumerated(EnumType.STRING)
	@Column(name = "currency_unit", length = CURRENCY_UNIT_LEN, nullable = false)
	private CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	// price package desc
	@Lob
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "aircraft_id")
	private Aircraft aircraft;

	@ManyToOne
	@JoinColumn(name = "product_id")
	@XmlJavaTypeAdapter(ProductAdapter.class)
	private Product product;

	public OrderSalesPackage() {
	}

	public OrderSalesPackage(String id) {
		this.id = id;
	}

	public OrderSalesPackage(SalesPackage sp) {
		this.id = null; // clear id which is the id of SalesPackage
		this.name = sp.getName();
		this.passengers = sp.getPassengers();
		this.prices = sp.getPrices();
		this.presalesDays = sp.getPresalesDays();
		this.currencyUnit = sp.getCurrencyUnit();
		this.description = sp.getDescription();
		this.aircraft = sp.getAircraft();
		this.product = sp.getProduct();
	}

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

	public int getPresalesDays() {
		return presalesDays;
	}

	public void setPresalesDays(int presalesDays) {
		this.presalesDays = presalesDays;
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderSalesPackage [name=").append(name).append(", passengers=").append(passengers)
				.append(", prices=").append(prices).append(", presalesDays=").append(presalesDays)
				.append(", currencyUnit=").append(currencyUnit).append(", description=").append(description)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}

}
