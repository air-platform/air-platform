package net.aircommunity.platform.service.product;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.ProductFaq;

/**
 * Product FAQ service.
 * 
 * @author Bin.Zhang
 */
public interface ProductFaqService {

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
	 * Increase views of a FAQ of a product.
	 * 
	 * @param productFaqId the productFaqId
	 * @return the updated FAQ
	 */
	@Nonnull
	ProductFaq increaseProductFaqViews(@Nonnull String productFaqId);

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
