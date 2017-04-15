package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.AirTransportOrder;
import net.aircommunity.platform.model.Order.Status;

/**
 * Repository interface for {@link AirTransportOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AirTransportOrderRepository extends BaseOrderRepository<AirTransportOrder> {

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<AirTransportOrder> findByVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param status the status
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<AirTransportOrder> findByVendorIdAndStatusOrderByCreationDateDesc(String tenantId, Status status,
			Pageable pageable);
}
