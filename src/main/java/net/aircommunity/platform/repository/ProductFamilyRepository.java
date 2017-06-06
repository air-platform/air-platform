package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.ProductFamily;

/**
 * Repository interface for {@link ProductFamily} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface ProductFamilyRepository extends JpaRepository<ProductFamily, String> {

	List<ProductFamily> findByName(String name);

	List<ProductFamily> findByCategory(Category category);

	Page<ProductFamily> findByCategory(Category category, Pageable pageable);

	Page<ProductFamily> findByVendorId(String tenantId, Pageable pageable);

	Page<ProductFamily> findByVendorIdAndCategory(String tenantId, Category category, Pageable pageable);

	/**
	 * Find all ProductFamilies by approved status.
	 * 
	 * @param approved the approved status
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<ProductFamily> findByApproved(boolean approved, Pageable pageable);

	/**
	 * Count all ProductFamilies by approved status.
	 * 
	 * @param approved the approved status
	 * @return count of products
	 */
	long countByApproved(boolean approved);

	long deleteByVendorId(String tenantId);
}
