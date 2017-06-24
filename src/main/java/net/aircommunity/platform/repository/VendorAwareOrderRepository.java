package net.aircommunity.platform.repository;

import java.util.Collection;

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
	 * Find all tenant orders not status (TENANT) (XXX NOT GOOD, Using where; Using filesort) full table scan
	 * 
	 * @param tenantId the tenantId
	 * @param ignoredStatus the ignoredStatus
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 * @deprecated use instead {@code #findByVendorIdAndStatusInOrderByCreationDateDesc(String, Collection, Pageable)}
	 */
	Page<T> findByVendorIdAndStatusNotOrderByCreationDateDesc(String tenantId, Status ignoredStatus, Pageable pageable);

	/**
	 * Find all user orders in status (Using where; )
	 * 
	 * @param userId the userId
	 * @param statuses the expected statuses
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByVendorIdAndStatusInOrderByCreationDateDesc(String userId, Collection<Status> statuses,
			Pageable pageable);

	/**
	 * Find all orders place on a tenant (TENANT) (XXX NOT GOOD, Using index condition; Using where; Using filesort)
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
