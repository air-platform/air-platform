package net.aircommunity.platform.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Unit product price model.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitProductPrice implements Serializable {
	private static final long serialVersionUID = 1L;

	private final PricePolicy pricePolicy;
	private final BigDecimal unitPrice;

	public UnitProductPrice(@Nonnull PricePolicy pricePolicy, @Nonnull BigDecimal unitPrice) {
		this.pricePolicy = Objects.requireNonNull(pricePolicy, "pricePolicy cannot be null");
		this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice cannot be null");
	}

	@Nonnull
	public PricePolicy getPricePolicy() {
		return pricePolicy;
	}

	@Nonnull
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UnitProductPrice [pricePolicy=").append(pricePolicy).append(", unitPrice=").append(unitPrice)
				.append("]");
		return builder.toString();
	}
}
