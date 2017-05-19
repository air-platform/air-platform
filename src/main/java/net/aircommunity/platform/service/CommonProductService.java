package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.ProductFaq;

/**
 * Common ProductService
 *
 * @author Bin.Zhang
 */
public interface CommonProductService {

	// *******
	// Product
	// *******

	/**
	 * Find product
	 * 
	 * @param productId the productId
	 * @return product found
	 */
	@Nonnull
	Product findProduct(@Nonnull String productId);

	/**
	 * Put product on sale of pull product off sale
	 * 
	 * @param productId the productId
	 * @param putOnSale the put on sale or pull off sale
	 * @return product update
	 */
	@Nonnull
	Product putProductOnSale(@Nonnull String productId, boolean putOnSale);

	/**
	 * Delete a product.
	 * 
	 * @param productId the productId
	 */
	void deleteProduct(@Nonnull String productId);

	/**
	 * Delete all products of a tenant.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteProducts(@Nonnull String tenantId);

	// *******
	// FAQ
	// *******

	/**
	 * List all FAQs of a product.
	 * 
	 * @param productId the productId
	 * @param page the page start from 1
	 * @param pageSize the pageSize
	 * @return a page if faqs or empty page
	 */
	@Nonnull
	Page<ProductFaq> listProductFaqs(@Nonnull String productId, int page, int pageSize);

	/**
	 * Create a FAQ for a product.
	 * 
	 * @param productId the productId
	 * @param faq the FAQ to be created
	 * @return the created FAQ
	 */
	@Nonnull
	ProductFaq createProductFaq(@Nonnull String productId, @Nonnull ProductFaq faq);

	/**
	 * Find a FAQ by ID.
	 * 
	 * @param productFaqId the productFaqId
	 * @return the created FAQ
	 */
	@Nonnull
	ProductFaq findProductFaq(@Nonnull String productFaqId);

	/**
	 * Update a FAQ of a product.
	 * 
	 * @param productFaqId the productFaqId
	 * @param newFaq the new FAQ to be updated
	 * @return the updated FAQ
	 */
	@Nonnull
	ProductFaq updateProductFaq(@Nonnull String productFaqId, @Nonnull ProductFaq newFaq);

	/**
	 * Delete a product FAQ.
	 * 
	 * @param productFaqId the productFaqId
	 */
	void deleteProductFaq(@Nonnull String productFaqId);

	/**
	 * Delete all FAQs of a product.
	 * 
	 * @param productId the productId
	 */
	void deleteProductFaqs(@Nonnull String productId);
}
