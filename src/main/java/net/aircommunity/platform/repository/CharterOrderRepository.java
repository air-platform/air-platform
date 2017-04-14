package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Order.Status;

/**
 * Repository interface for {@link CharterOrder} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CharterOrderRepository extends JpaRepository<CharterOrder, String> {

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<CharterOrder> findByOwnerIdOrderByCreationDateDesc(String userId, Pageable pageable);

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param status the status
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<CharterOrder> findByOwnerIdAndStatusOrderByCreationDateDesc(String userId, Status status, Pageable pageable);

	/**
	 * Find all orders
	 * 
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<CharterOrder> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find all orders
	 * 
	 * @param status the status
	 * @param pageable page request
	 * @return a page of FerryFlightOrders or empty if none
	 */
	Page<CharterOrder> findByStatusOrderByCreationDateDesc(Status status, Pageable pageable);

	/**
	 * Delete all the orders of a user.
	 * 
	 * @param userId the userId
	 * @return the records deleted
	 */
	long deleteByOwnerId(String userId);

	CharterOrder findByOrderNo(String orderNo);

	void deleteByOrderNo(String orderNo);

}
