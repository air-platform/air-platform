package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.ProductFaq;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

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
	 * Increase product sale by 1
	 * 
	 * @param productId the productId
	 * @return product found
	 */
	@Nonnull
	Product increaseProductSales(@Nonnull String productId);

	/**
	 * Put product on sale of pull product off sale
	 * 
	 * @param productId the productId
	 * @param published the put on sale or pull off sale
	 * @return product update
	 */
	@Nonnull
	Product publishProduct(@Nonnull String productId, boolean published);

	/**
	 * Review a product to approve or not (by platform ADMIN only)
	 * 
	 * @param productId the productId
	 * @param reviewStatus the product is reviewStatus
	 * @param rejectedReason the rejected reason if it's NOT approved
	 * @return product update
	 */
	@Nonnull
	Product reviewProduct(@Nonnull String productId, @Nonnull ReviewStatus reviewStatus,
			@Nullable String rejectedReason);

	/**
	 * List all products by reviewStatus. (ADMIN ONLY)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Product to be reviewed
	 */
	default Page<Product> listAllProducts(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, null, page, pageSize);
	}

	/**
	 * List all approved products by category. (ADMIN ONLY)
	 * 
	 * @param category the product category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a list of product
	 */
	default Page<Product> listAllApprovedProducts(@Nullable Category category, int page, int pageSize) {
		return listAllProducts(ReviewStatus.APPROVED, category, page, pageSize);
	}

	/**
	 * List all products by reviewStatus & category. (ADMIN ONLY)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param category the product category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a list of product
	 */
	Page<Product> listAllProducts(@Nullable ReviewStatus reviewStatus, @Nullable Category category, int page,
			int pageSize);

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
