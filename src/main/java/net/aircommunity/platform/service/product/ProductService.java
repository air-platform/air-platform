package net.aircommunity.platform.service.product;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * Basic product service shared by all products (can be implemented as common product service not necessary a concrete
 * product). TODO cleanup all the concrete service API redefined provided by this service
 *
 * @author Bin.Zhang
 */
public interface ProductService<T extends Product> {

	/**
	 * Find product
	 * 
	 * @param productId the productId
	 * @return product found
	 */
	@Nonnull
	T findProduct(@Nonnull String productId);

	/**
	 * Find product
	 * 
	 * @param type the product type
	 * @param productId the productId
	 * @return product found
	 */
	@Nonnull
	T findProduct(@Nonnull Product.Type type, @Nonnull String productId);

	/**
	 * Increase product sale by 1 (ADMIN)
	 * 
	 * @param productId the productId
	 * @return product found
	 */
	@Nonnull
	T increaseProductSales(@Nonnull String productId);

	/**
	 * Update product sale with the given delta value. (ADMIN)
	 * 
	 * @param productId the productId
	 * @param deltaSales the deltaSales (+-)
	 * @return product found
	 */
	@Nonnull
	T updateProductSales(@Nonnull String productId, int deltaSales);

	/**
	 * Put product on sale of pull product off sale (ADMIN | TENANT)
	 * 
	 * @param productId the productId
	 * @param published the put on sale or pull off sale
	 * @return product update
	 */
	@Nonnull
	T publishProduct(@Nonnull String productId, boolean published);

	/**
	 * Update product rank. (ADMIN)
	 * 
	 * @param productId the productId
	 * @param newRank the newRank
	 * @return product update
	 */
	@Nonnull
	T updateProductRank(@Nonnull String productId, int newRank);

	/**
	 * Update product score. (ADMIN)
	 * 
	 * @param productId the productId
	 * @param score the new score
	 * @return product update
	 */
	@Nonnull
	T updateProductScore(@Nonnull String productId, double score);

	/**
	 * Update product to the given stock. (ADMIN)
	 * 
	 * @param productId the productId
	 * @param stock the stock
	 * @return product update
	 */
	@Nonnull
	T updateProductStock(@Nonnull String productId, int stock);

	/**
	 * Update product with delta stock(+-). (ADMIN)
	 * 
	 * @param productId the productId
	 * @param deltaStock the delta stock (+- to indicate increase or decrease)
	 * @return product update
	 */
	@Nonnull
	T updateProductStockWithDelta(@Nonnull String productId, int deltaStock);

	/**
	 * Review a product to approve or not (ADMIN)
	 * 
	 * @param productId the productId
	 * @param reviewStatus the product is reviewStatus
	 * @param rejectedReason the rejected reason if it's NOT approved
	 * @return product update
	 */
	@Nonnull
	T reviewProduct(@Nonnull String productId, @Nonnull ReviewStatus reviewStatus, @Nullable String rejectedReason);

	/**
	 * List all products by reviewStatus. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of T to be reviewed
	 */
	default Page<T> listAllProducts(@Nullable ReviewStatus reviewStatus, int page, int pageSize) {
		return listAllProducts(reviewStatus, null, page, pageSize);
	}

	/**
	 * Count all products of the given review status (ADMIN)
	 * 
	 * @param reviewStatus the review status
	 * @return the count all products
	 */
	long countAllProducts(@Nonnull ReviewStatus reviewStatus);

	/**
	 * List all approved products by category. (ADMIN)
	 * 
	 * @param category the product category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a list of product
	 */
	default Page<T> listAllApprovedProducts(@Nullable Category category, int page, int pageSize) {
		return listAllProducts(ReviewStatus.APPROVED, category, page, pageSize);
	}

	/**
	 * List all products by reviewStatus & category. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param category the product category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a list of product
	 */
	Page<T> listAllProducts(@Nullable ReviewStatus reviewStatus, @Nullable Category category, int page, int pageSize);

	/**
	 * List all products by reviewStatus for a tenant. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a list of product of a tenant
	 */
	@Nonnull
	Page<T> listTenantProducts(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus, int page, int pageSize);

	/**
	 * Count all products of a tenant of the given review status (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the review status, if null just count all
	 * @return the count of tenant products
	 */
	long countTenantProducts(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus);

	/**
	 * List all products (published) by pagination for anybody. (ANYBODY)
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of products or empty
	 */
	Page<T> listProducts(int page, int pageSize);

	/**
	 * Delete a product. (ADMIN | TENANT)
	 * 
	 * @param productId the productId
	 */
	void deleteProduct(@Nonnull String productId);

	/**
	 * Delete all products of a tenant. (ADMIN | TENANT)
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteProducts(@Nonnull String tenantId);

	/**
	 * Delete all products, and product families, aircrafts, everything related to a tenant. (in case there is no order
	 * placed on any product of this tenant), otherwise deletion will fail. (ADMIN | TENANT)
	 * 
	 * @param tenantId the tenantId
	 */
	void purgeProducts(@Nonnull String tenantId);

}
