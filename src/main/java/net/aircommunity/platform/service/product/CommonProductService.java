package net.aircommunity.platform.service.product;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.metrics.ProductMetrics;

/**
 * Common product service handles only the generic shared product properties.
 *
 * @author Bin.Zhang
 */
public interface CommonProductService extends ProductService<Product>, ProductFaqService {

	/**
	 * Get the overall product metrics.
	 * 
	 * @return the product metrics
	 */
	@Nonnull
	ProductMetrics getProductMetrics();

	/**
	 * Get the overall product metrics for a tenant.
	 * 
	 * @param tenantId the tenantId
	 * @return the product metrics
	 */
	@Nonnull
	ProductMetrics getProductMetrics(String tenantId);
}
