package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;

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
	 * @param approved the approved or not
	 * @param rejectedReason the rejected reason if it's NOT approved
	 * @return productFamily updated
	 */
	@Nonnull
	ProductFamily reviewProductFamily(String productFamilyId, boolean approved, String rejectedReason);

	/**
	 * List all product families filter by tenantId.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listProductFamilies(@Nonnull String tenantId, int page, int pageSize);

	/**
	 * List all product families by type and pagination.
	 * 
	 * @param tenantId the tenantId
	 * @param category the productFamily category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listProductFamiliesByCategory(@Nonnull String tenantId, Category category, int page,
			int pageSize);

	/**
	 * List all product families
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listProductFamilies(int page, int pageSize);

	/**
	 * List all product families filter by approved status.
	 * 
	 * @param approved the approved status
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listProductFamilies(boolean approved, int page, int pageSize);

	long countProductFamilies(boolean approved);

	/**
	 * List all product families by type and pagination.
	 * 
	 * @param category the productFamily category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	Page<ProductFamily> listProductFamiliesByCategory(Category category, int page, int pageSize);

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
