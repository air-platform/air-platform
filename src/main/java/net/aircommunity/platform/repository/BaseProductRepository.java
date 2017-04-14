package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Tenant;

/**
 * Repository interface for {@link Product} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface BaseProductRepository<T extends Product> extends JpaRepository<T, String> {

	/**
	 * Find by products by vendor.
	 * 
	 * @param vendor the vendor
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<T> findByVendor(Tenant vendor, Pageable pageable);

	/**
	 * Delete all the orders of a vendor.
	 * 
	 * @param userId the userId
	 * @return the records deleted
	 */
	long deleteByVendor(Tenant vendor);

}
