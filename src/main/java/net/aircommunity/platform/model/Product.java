package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Product of an {@code Tenant} (AKA. vendor). TODO need set accountManagers (a1@company.com,a2@company.com)
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Product extends Persistable {
	private static final long serialVersionUID = 1L;

	// product name
	@Column(name = "name", nullable = false)
	protected String name;

	// product price
	@Min(0)
	@Column(name = "price", nullable = false)
	protected int price;

	// product price CurrencyUnit
	@Column(name = "currency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	protected CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	// product description
	@Lob
	@Column(name = "description")
	protected String description;

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	protected Tenant vendor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

}
