package net.aircommunity.platform.service.product;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Product;

/**
 * Standard product service with creation and update support (should be implemented by a concrete product).
 * 
 * @author Bin.Zhang
 *
 * @param <T> the product type
 */
public interface StandardProductService<T extends Product> extends ProductService<T>, ProductFaqService {

	/**
	 * Create a product for the given tenant.
	 * 
	 * @param tenantId the tenantId
	 * @param product the product to be created
	 * @return product created
	 */
	@Nonnull
	T createProduct(@Nonnull String tenantId, @Nonnull T product);

	/**
	 * Update a product for the given productId.
	 * 
	 * @param productId the productId
	 * @param newProduct the product to be updated
	 * @return product updated
	 */
	@Nonnull
	T updateProduct(@Nonnull String productId, @Nonnull T newProduct);
}
