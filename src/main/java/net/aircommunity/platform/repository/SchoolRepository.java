package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.School;

/**
 * Created by guankai on 11/04/2017.
 */
public interface SchoolRepository extends JpaRepository<School, String> {

	/**
	 * find schools by tenant
	 *
	 * @param tenant
	 * @param pageable
	 * @return
	 */
	Page<School> findByVendorId(String tenantId, Pageable pageable);

	/**
	 * Delete all the orders of a vendor.
	 * 
	 * @param tenantId the tenantId
	 * @return the records deleted
	 */
	long deleteByVendorId(String tenantId);
}
