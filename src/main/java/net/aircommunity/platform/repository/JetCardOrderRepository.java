package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.JetCardOrder;
import net.aircommunity.platform.model.Order.Status;

/**
 * Repository interface for {@link JetCardOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface JetCardOrderRepository extends BaseOrderRepository<JetCardOrder> {

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param pageable page request
	 * @return a page of JetCardOrders or empty if none
	 */
	Page<JetCardOrder> findByVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param status the status
	 * @param pageable page request
	 * @return a page of JetCardOrders or empty if none
	 */
	Page<JetCardOrder> findByVendorIdAndStatusOrderByCreationDateDesc(String tenantId, Status status,
			Pageable pageable);

}
