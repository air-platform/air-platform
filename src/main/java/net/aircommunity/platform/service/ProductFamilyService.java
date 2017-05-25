package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;

import net.aircommunity.platform.AirException;
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
	 * @return productFamily created
	 */
	@Nonnull
	ProductFamily updateProductFamily(@Nonnull String productFamilyId, @Nonnull ProductFamily newProductFamily);

	/**
	 * List all product families filter by tenantId.
	 * 
	 * @param tenantId the tenantId
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	List<ProductFamily> listProductFamilies(@Nonnull String tenantId);

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
	List<ProductFamily> listProductFamiliesByCategory(@Nonnull String tenantId, Category category);

	/**
	 * List all product families
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	List<ProductFamily> listAllProductFamilies();

	/**
	 * List all product families by type and pagination.
	 * 
	 * @param category the productFamily category
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of product families or empty
	 */
	@Nonnull
	List<ProductFamily> listAllProductFamiliesByCategory(Category category);

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
	void deleteProductFamilys(@Nonnull String tenantId);
}
