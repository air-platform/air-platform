package net.aircommunity.platform.repository;

import java.util.List;

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

	List<ProductFamily> findByVendorId(String tenantId);

	List<ProductFamily> findByVendorIdAndCategory(String tenantId, Category category);

	long deleteByVendorId(String tenantId);
}
