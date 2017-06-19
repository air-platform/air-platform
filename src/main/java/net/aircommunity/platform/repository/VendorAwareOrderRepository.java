package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.VendorAwareOrder;
import net.aircommunity.platform.model.domain.Order.Status;

/**
 * Repository interface for {@link VendorAwareOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface VendorAwareOrderRepository<T extends VendorAwareOrder> extends BaseOrderRepository<T> {

	/**
	 * Find all tenant orders not status
	 * 
	 * @param tenantId the tenantId
	 * @param ignoredStatus the ignoredStatus
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByVendorIdAndStatusNotOrderByCreationDateDesc(String tenantId, Status ignoredStatus, Pageable pageable);

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param status the status
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByVendorIdAndStatusOrderByCreationDateDesc(String tenantId, Status status, Pageable pageable);
}
