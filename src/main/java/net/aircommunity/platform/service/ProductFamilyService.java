package net.aircommunity.platform.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;

/**
 * ProductFamily Service
 * 
 * @author Bin.Zhang
 */
public interface ProductFamilyService {

	/**
	 * Create a productFamily.
	 * 
	 * @param tenantId the tenantId
	 * @param productFamily the productFamily to be created
	 * @return productFamily created
	 */
	@Nonnull
	ProductFamily createProductFamily(@Nonnull String tenantId, @Nonnull ProductFamily productFamily);

	/**
	 * Retrieves the specified ProductFamily.
	 * 
	 * @param productFamilyId the productFamilyId
	 * @return the ProductFamily found
	 * @throws AirException if not found
	 */
	@Nonnull
	ProductFamily findProductFamily(@Nonnull String productFamilyId);

	/**
	 * Update a productFamily.
	 * 
	 * @param productFamilyId the productFamilyId
	 * @param newProductFamily the productFamily to be updated
	 * @return productFamily updated
	 */
	@Nonnull
	ProductFamily updateProductFamily(@Nonnull String productFamilyId, @Nonnull ProductFamily newProductFamily);

	/**
	 * Review a productFamily to approved or not (by platform ADMIN only).
	 * 
	 * @param productFamilyId the productFamilyId
	 * @param reviewStatus the reviewStatus
	 * @param rejectedReason the rejected reason if it's NOT approved
	 * @return productFamily updated
	 */
	@Nonnull
	ProductFamily reviewProductFamily(String productFamilyId, ReviewStatus reviewStatus, String rejectedReason);

	/**
	 * List all product families filter by reviewStatus. (ADMIN)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listAllProductFamilies(@Nullable ReviewStatus reviewStatus, int page, int pageSize);

	// TODO ?
	// Page<ProductFamily> listAllProductFamilies(@Nullable ReviewStatus reviewStatus, @Nullable Category category,
	// int page, int pageSize);

	long countAllProductFamilies(@Nullable ReviewStatus reviewStatus);

	/**
	 * List tenant product families filter by reviewStatus. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param category the product category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listTenantProductFamilies(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus,
			@Nullable Category category, int page, int pageSize);

	long countTenantProductFamilies(@Nonnull String tenantId, @Nullable ReviewStatus reviewStatus);

	/**
	 * List all product families (USER)
	 * 
	 * @param category the productFamily category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listProductFamilies(@Nullable Category category, int page, int pageSize);

	/**
	 * Delete a productFamily.
	 * 
	 * @param productFamilyId the productFamilyId
	 */
	void deleteProductFamily(@Nonnull String productFamilyId);

	/**
	 * Delete product families.
	 * 
	 * @param tenantId the tenantId
	 */
	void deleteProductFamilies(@Nonnull String tenantId);
}
