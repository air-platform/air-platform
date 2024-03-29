package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * Repository interface for {@link ProductFamily} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface ProductFamilyRepository extends JpaRepository<ProductFamily, String> {

	/**
	 * Find all ProductFamilies. (For ADMIN ONLY) (REMOVE ORDER BY FOR PERFORMANCE)
	 * 
	 * @param pageable the page request
	 * @return page of products
	 */
	// Page<ProductFamily> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find all ProductFamilies by review status. (For ADMIN ONLY)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param pageable the page request
	 * @return @return page of products
	 */
	Page<ProductFamily> findByReviewStatus(ReviewStatus reviewStatus, Pageable pageable);

	/**
	 * Count all products by review status. (For ADMIN ONLY)
	 * 
	 * @param reviewStatus the review status
	 * @return count of products
	 */
	long countByReviewStatus(ReviewStatus reviewStatus);

	/**
	 * Find by products by vendor. (For TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<ProductFamily> findByVendorId(String tenantId, Pageable pageable);

	Page<ProductFamily> findByVendorIdAndCategory(String tenantId, Category category, Pageable pageable);

	/**
	 * Find by products by vendor. (For TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<ProductFamily> findByVendorIdAndReviewStatus(String tenantId, ReviewStatus reviewStatus, Pageable pageable);

	Page<ProductFamily> findByVendorIdAndReviewStatusAndCategory(String tenantId, ReviewStatus reviewStatus,
			Category category, Pageable pageable);

	/**
	 * Count tenant products. (For TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @return count of products
	 */
	long countByVendorId(String tenantId);

	/**
	 * Count tenant products by review status. (For TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the review status
	 * @return count of products
	 */
	long countByVendorIdAndReviewStatus(String tenantId, ReviewStatus reviewStatus);

	/**
	 * Find all ProductFamilies by review status. (For END USER mainly). Only show approved and published product.
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param published the published status
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<ProductFamily> findByReviewStatusAndCategory(ReviewStatus reviewStatus, Category category, Pageable pageable);

	/**
	 * Delete all ProductFamilies for this tenant.
	 * 
	 * @param tenantId the tenantId
	 * @return records deleted
	 */
	long deleteByVendorId(String tenantId);

}
