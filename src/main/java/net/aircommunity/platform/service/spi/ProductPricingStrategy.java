package net.aircommunity.platform.service.spi;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Order;

/**
 * Offer quotation of price for an order.
 * 
 * @author Bin.Zhang
 */
public interface ProductPricingStrategy {

	/**
	 * Quotation for and order.
	 * 
	 * @param order the order to be quoted
	 * @return quoted price
	 */
	@Nonnull
	BigDecimal quotationFor(@Nonnull Order order);

	/**
	 * NoOp implementation
	 */
	ProductPricingStrategy NOOP = order -> BigDecimal.ZERO;

}
