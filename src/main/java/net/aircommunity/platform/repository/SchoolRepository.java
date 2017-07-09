package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.School;

/**
 * Repository interface for {@link School} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author guankai
 */
public interface SchoolRepository extends JpaRepository<School, String> {

	/**
	 * find schools by tenant
	 *
	 * @param tenant
	 * @param pageable
	 * @return a page of school or empty
	 */
	Page<School> findByVendorId(String tenantId, Pageable pageable);

	/**
	 * Count tenant products. (TENANT)
	 * 
	 * @param tenantId the tenantId
	 * @return count of schools
	 */
	long countByVendorId(String tenantId);

	/**
	 * Delete all the orders of a vendor.
	 * 
	 * @param tenantId the tenantId
	 * @return the records deleted
	 */
	long deleteByVendorId(String tenantId);
}
