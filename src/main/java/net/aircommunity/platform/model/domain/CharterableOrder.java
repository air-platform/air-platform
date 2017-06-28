package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import net.aircommunity.platform.model.PricePolicy;
import net.aircommunity.platform.model.UnitProductPrice;

/**
 * Charterable vendor aware order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CharterableOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	// true: charter, false: not-chartered
	@NotNull
	@Column(name = "chartered", nullable = false)
	protected boolean chartered = false;

	// the number of passengers,
	// if charter=true, the passengers is ignored
	// if charter=false, the passengers is ignored
	@Min(1)
	@Column(name = "passengers")
	protected int passengers;

	public boolean isChartered() {
		return chartered;
	}

	public void setChartered(boolean chartered) {
		this.chartered = chartered;
	}

	public int getPassengers() {
		return passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	@Override
	public UnitProductPrice getUnitProductPrice() {
		Product product = getProduct();
		if (!CharterableProduct.class.isAssignableFrom(product.getClass())) {
			throw new IllegalStateException(String.format("Invalid order model, expect product: %s, but was: %s",
					CharterableProduct.class.getSimpleName(), product.getClass().getSimpleName()));
		}
		// calculate unit product price
		CharterableProduct charterableProduct = (CharterableProduct) product;
		BigDecimal unitPrice = BigDecimal.ZERO;
		// chartered: true
		// productPrice= (seatPrice * passengers)
		if (chartered) {
			unitPrice = charterableProduct.getPrice();
		}
		// chartered: false
		// productPrice = price
		else {
			unitPrice = charterableProduct.getSeatPrice().multiply(BigDecimal.valueOf(passengers));
		}
		return new UnitProductPrice(PricePolicy.STANDARD, unitPrice);
	}

}
