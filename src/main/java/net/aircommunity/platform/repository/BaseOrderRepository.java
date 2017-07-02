package net.aircommunity.platform.repository;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Product.Type;

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
	 * Find all user orders in status (order in 1 month, 3 months, 6 months) (type: range, Using index condition; Using
	 * filesort )
	 * 
	 * @param userId the userId
	 * @param statuses the expected statuses (all except deleted status)
	 * @param creationDate the creationDate
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusInAndCreationDateLessThanEqualOrderByCreationDateDesc(String userId,
			Collection<Status> statuses, Date creationDate, Pageable pageable);

	/**
	 * Find all user orders not status (type: range, Using index condition; Using filesort)
	 * 
	 * @param userId the userId
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusNotOrderByCreationDateDesc(String userId, Status ignoredStatus, Pageable pageable);

	// (NOTE: used more often)
	/**
	 * Find all user orders in status (type: range, Using index condition; Using filesort)
	 * 
	 * @param userId the userId
	 * @param statuses the expected statuses
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndStatusInOrderByCreationDateDesc(String userId, Collection<Status> statuses,
			Pageable pageable);

	/**
	 * Find all user orders not in status (type: range, Using index condition; Using filesort)
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
	 * Find all user orders ( USED RARE, less important) (type:ref, Using index condition; Using where; Using filesort)
	 * 
	 * @param userId the userId
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByOwnerIdAndTypeOrderByCreationDateDesc(String userId, Type type, Pageable pageable);

	/**
	 * Find all user orders (type:ref, Using where)
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
	 * Find all orders (ADMIN) (XXX NOT GOOD) (type: all, Using filesort)
	 * 
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findAllByOrderByCreationDateDesc(Pageable pageable);

	/**
	 * Find all orders filter by status (ADMIN) (XXX NOT GOOD) (type: all, Using where; Using filesort)
	 * 
	 * @param status the status
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByStatusOrderByCreationDateDesc(Status status, Pageable pageable);

	// (NOTE: used more often)
	/**
	 * Find all orders filter by type (ADMIN) (XXX NOT GOOD) (type: all, Using where; Using filesort)
	 * 
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<T> findByTypeOrderByCreationDateDesc(Type type, Pageable pageable);

	/**
	 * Find all orders filter by status and type (ADMIN) (XXX NOT GOOD) (type: all, Using where; Using filesort)
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
