package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.FerryFlightOrder;
import net.aircommunity.platform.model.Order.Status;

/**
 * Repository interface for {@link FerryFlightOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FerryFlightOrderRepository extends JpaRepository<FerryFlightOrder, String> {

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<FerryFlightOrder> findByOwnerIdOrderByCreationDateDesc(String userId, Pageable pageable);

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param status the status
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<FerryFlightOrder> findByOwnerIdAndStatusOrderByCreationDateDesc(String userId, Status status,
			Pageable pageable);

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<FerryFlightOrder> findByVendorIdOrderByCreationDateDesc(String tenantId, Pageable pageable);

	/**
	 * Find all orders place on a tenant
	 * 
	 * @param tenantId the tenantId
	 * @param status the status
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<FerryFlightOrder> findByVendorIdAndStatusOrderByCreationDateDesc(String tenantId, Status status,
			Pageable pageable);

	/**
	 * Find all orders
	 * 
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<FerryFlightOrder> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find all orders
	 * 
	 * @param status the status
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<FerryFlightOrder> findByStatusOrderByCreationDateDesc(Status status, Pageable pageable);

	/**
	 * Delete all the orders of a user.
	 * 
	 * @param userId the userId
	 * @return the records deleted
	 */
	long deleteByOwnerId(String userId);

}
