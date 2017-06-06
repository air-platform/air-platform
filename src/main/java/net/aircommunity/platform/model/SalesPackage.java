package net.aircommunity.platform.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import io.micro.annotation.constraint.NotEmpty;
import io.micro.common.Strings;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.constraint.PriceList;
import net.aircommunity.platform.model.jaxb.ProductAdapter;

/**
 * Sales Package for taxi, tour and transportation.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_salespackage")
@XmlAccessorType(XmlAccessType.FIELD)
public class SalesPackage extends Persistable {
	private static final long serialVersionUID = 1L;

	public static final int NUM_OF_PRICES = 30;
	public static final Range<Integer> DAYS_RANGE = Range.closedOpen(0, NUM_OF_PRICES);

	// price package name
	@NotEmpty
	@Column(name = "name", nullable = false)
	private String name;

	// the number of passengers of this package
	@Column(name = "passengers", nullable = false)
	private int passengers;

	// prices for 30days, separated by comma (,) if 0 means the product is not available that day
	// e.g. 1000,2000,0,3000 etc.
	@PriceList
	@Column(name = "prices")
	private String prices;

	@XmlTransient
	@Transient
	private List<Double> priceList;

	// in presalesDays before
	@Column(name = "presales_days")
	private int presalesDays = 0;

	// product price CurrencyUnit
	@NotNull
	@Column(name = "currency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	private CurrencyUnit currencyUnit = CurrencyUnit.RMB;

	// price package desc
	@Lob
	@Column(name = "description")
	private String description;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "aircraft_id")
	private Aircraft aircraft;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "product_id")
	@XmlJavaTypeAdapter(ProductAdapter.class)
	private Product product;

	@PostLoad
	private void onLoad() {
		normalizeAndApplyPrices(prices);
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

	public double getPrice(int offset) {
		if (offset < 0 || offset >= priceList.size()) {
			return 0;
		}
		return priceList.get(offset);
	}

	public void setPrices(String prices) {
		// normalize prices (pad to max prices size)
		normalizeAndApplyPrices(prices);
	}

	private void normalizeAndApplyPrices(String rawPrices) {
		if (Strings.isBlank(rawPrices)) {
			priceList = Collections.emptyList();
		}
		else {
			ImmutableList.Builder<Double> builder = ImmutableList.builder();
			List<String> list = Splitter.on(Constants.PRICE_SEPARATOR).trimResults().omitEmptyStrings()
					.splitToList(rawPrices);
			Double[] normalizedPrices = new Double[SalesPackage.NUM_OF_PRICES];
			Arrays.fill(normalizedPrices, 0);
			int size = Math.min(SalesPackage.NUM_OF_PRICES, list.size());
			for (int i = 0; i < size; i++) {
				normalizedPrices[i] = Double.valueOf(list.get(i));
				builder.add(normalizedPrices[i]);
			}
			priceList = builder.build();
			prices = Joiner.on(Constants.PRICE_SEPARATOR).join(normalizedPrices);
		}
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
				.append(", prices=").append(prices).append(", presalesDays=").append(presalesDays)
				.append(", currencyUnit=").append(currencyUnit).append(", description=").append(description)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}

}
