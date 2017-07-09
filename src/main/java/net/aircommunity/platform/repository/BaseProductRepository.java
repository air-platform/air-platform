package net.aircommunity.platform.repository;

import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.domain.Reviewable.ReviewStatus;

/**
 * Repository interface for {@link Product} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface BaseProductRepository<T extends Product>
		extends JpaRepository<T, String>, JpaSpecificationExecutor<T> {

	// *****************
	// USER
	// *****************
	/**
	 * Find all products by review status. (For END USER mainly). Only show approved and published product.
	 * 
	 * (COMMENT OUT - NOT USED)
	 * 
	 * @param published the published status
	 * @param category the product category
	 * @param pageable the page request
	 * @return page of products
	 */
	// Page<T> findByCategoryAndPublishedOrderByRankAscScoreDesc(Category category, boolean published, Pageable
	// pageable);

	/**
	 * Find all products by review status. (For END USER mainly). Only show approved and published product.
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param published the published status
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findByPublishedTrueOrderByRankDescScoreDesc(Pageable pageable);
	// Page<T> findByPublishedOrderByRankAscScoreDesc(boolean published, Pageable pageable);
	// Page<T> findByReviewStatusAndPublishedOrderByRankAscScoreDesc(ReviewStatus reviewStatus, boolean published,
	// Pageable pageable);

	// *****************
	// ADMIN
	// *****************
	/**
	 * Find all products. (For ADMIN ONLY)
	 * 
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find all products. (For ADMIN ONLY)
	 * 
	 * @param category the category
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findByCategoryOrderByCreationDateDesc(Category category, Pageable pageable);

	/**
	 * Find all products by review status. (For ADMIN ONLY)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param pageable the page request
	 * @return @return page of products
	 */
	Page<T> findByReviewStatusOrderByCreationDateDesc(ReviewStatus reviewStatus, Pageable pageable);

	/**
	 * Find all products by review status. (For ADMIN ONLY)
	 * 
	 * @param reviewStatus the reviewStatus
	 * @param category the category
	 * @param pageable the page request
	 * @return @return page of products
	 */
	Page<T> findByReviewStatusAndCategoryOrderByCreationDateDesc(ReviewStatus reviewStatus, Category category,
			Pageable pageable);

	/**
	 * Count all products by review status. (For ADMIN ONLY) (bad performance when used without a concrete type, use it
	 * only when necessary)
	 * 
	 * @param reviewStatus the review status
	 * @return count of products
	 */
	long countByReviewStatus(ReviewStatus reviewStatus);

	long countByPublished(boolean published);

	long countByVendorIdAndPublished(String tenantId, boolean published);

	/**
	 * Count all products by type. (For ADMIN ONLY) (bad performance when used without a concrete product type, use it
	 * only when necessary, and it's USELESS when used in a concrete product type, because type is always same for a
	 * concrete product type)
	 * 
	 * @param type the product type
	 * @return count of products
	 */
	long countByType(Type type);

	// iterator over products (For ADMIN ONLY)
	Stream<T> findByVendorId(String tenantId);

	/**
	 * Delete all the products of a vendor. (For ADMIN ONLY)
	 * @param tenantId the tenantId
	 * @return the records deleted
	 */
	long deleteByVendorId(String tenantId);

	// *****************
	// TENANT
	// *****************
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

}
