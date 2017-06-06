package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;

/**
 * Repository interface for {@link Product} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface BaseProductRepository<T extends Product> extends JpaRepository<T, String> {

	/**
	 * Find all products by review status. (For END USER mainly). Only show approved and published product.
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param published the published status
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findByPublishedOrderByRankAscScoreDesc(boolean published, Pageable pageable);
	// Page<T> findByReviewStatusAndPublishedOrderByRankAscScoreDesc(ReviewStatus reviewStatus, boolean published,
	// Pageable pageable);

	/**
	 * Find all products. (For ADMIN ONLY)
	 * 
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find all products by review status. (For ADMIN ONLY)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param pageable the page request
	 * @return @return page of products
	 */
	Page<T> findByReviewStatusOrderByCreationDateDesc(ReviewStatus reviewStatus, Pageable pageable);

	/**
	 * Count all products by review status. (For ADMIN ONLY)
	 * 
	 * @param reviewStatus the review status
	 * @return count of products
	 */
	long countByReviewStatus(ReviewStatus reviewStatus);

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
	 * Find by products by vendor. (For TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findByVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	/**
	 * Find by products by vendor. (For TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @param reviewStatus the reviewStatus
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findByVendorIdAndReviewStatusOrderByCreationDateDesc(String tenantId, ReviewStatus reviewStatus,
			Pageable pageable);

	/**
	 * Delete all the products of a vendor. (For ADMIN ONLY)
	 * @param tenantId the tenantId
	 * @return the records deleted
	 */
	long deleteByVendorId(String tenantId);

}
