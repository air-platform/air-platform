package net.aircommunity.platform.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Order.Type;

/**
 * Repository interface for {@link Order} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface BaseOrderRepository<T extends Order> extends JpaRepository<T, String>, JpaSpecificationExecutor<T> {

	/**
	 * Test if there is any order for user
	 * 
	 * @param userId the userId
	 * @return true if there is an order of this user, false otherwise
	 */
	boolean existsByOwnerId(String userId);

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdOrderByCreationDateDesc(String userId, Pageable pageable);

	/**
	 * Find all user orders not status
	 * 
	 * @param userId the userId
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusNotOrderByCreationDateDesc(String userId, Status ignoredStatus, Pageable pageable);

	/**
	 * Find all user orders in status
	 * 
	 * @param userId the userId
	 * @param statuses the expected statuses
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusInOrderByCreationDateDesc(String userId, Collection<Status> statuses,
			Pageable pageable);

	/**
	 * Find all user orders not in status
	 * 
	 * @param userId the userId
	 * @param statuses the expected statuses
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusNotInOrderByCreationDateDesc(String userId, Collection<Status> statuses,
			Pageable pageable);

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param status the status
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusOrderByCreationDateDesc(String userId, Status status, Pageable pageable);

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndTypeOrderByCreationDateDesc(String userId, Type type, Pageable pageable);

	/**
	 * Find all user orders
	 * 
	 * @param userId the userId
	 * @param status the status
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusAndTypeOrderByCreationDateDesc(String userId, Status status, Type type,
			Pageable pageable);

	/**
	 * Find all orders
	 * 
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find all orders filter by status
	 * 
	 * @param status the status
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByStatusOrderByCreationDateDesc(Status status, Pageable pageable);

	/**
	 * Find all orders filter by type
	 * 
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByTypeOrderByCreationDateDesc(Type type, Pageable pageable);

	/**
	 * Find all orders filter by status and type
	 * 
	 * @param status the status
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByStatusAndTypeOrderByCreationDateDesc(Status status, Type type, Pageable pageable);

	/**
	 * Delete all the orders of a user.
	 * 
	 * @param userId the userId
	 * @return the records deleted
	 */
	long deleteByOwnerId(String userId);

	/**
	 * Find by order NO.
	 * 
	 * @param orderNo the order NO
	 * @return order found
	 */
	T findByOrderNo(String orderNo);

	/**
	 * Delete by order NO.
	 * 
	 * @param orderNo the order NO
	 */
	void deleteByOrderNo(String orderNo);

}
