package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Aircraft;

/**
 * Repository interface for {@link Aircraft} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AircraftRepository extends JpaRepository<Aircraft, String> {

	/**
	 * Find by flightNo
	 * 
	 * @param flightNo the unique flightNo
	 * @return Aircraft found or null if not found
	 */
	Aircraft findByFlightNo(String flightNo);

	/**
	 * Find by Aircrafts by vendor.
	 * 
	 * @param tenantId the tenantId
	 * @param pageable the page request
	 * @return page of products
	 */
	Page<Aircraft> findByVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	/**
	 * Delete all the Aircrafts of a vendor.
	 * 
	 * @param tenantId the tenantId
	 * @return the records deleted
	 */
	long deleteByVendorId(String tenantId);

}
