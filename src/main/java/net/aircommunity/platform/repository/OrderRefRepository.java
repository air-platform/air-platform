package net.aircommunity.platform.repository;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.QueryHint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import io.micro.common.Strings;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.OrderRef;
import net.aircommunity.platform.model.domain.OrderRef_;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.domain.Trade;

/**
 * Repository interface for {@link OrderRef} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface OrderRefRepository extends JpaRepository<OrderRef, String>, JpaSpecificationExecutor<OrderRef> {

	// probably payment pending --- can be updated to --> PAID
	EnumSet<Status> PAYMENT_PENDING_STATUSES = EnumSet.of(Status.PUBLISHED, Status.CREATED, Status.CUSTOMER_CONFIRMED,
			Status.CONFIRMED, Status.CONTRACT_SIGNED, Status.PAYMENT_IN_PROCESS, Status.PARTIAL_PAID,
			Status.PAYMENT_FAILED);

	// probably refund pending --- can be updated to --> REFUND
	EnumSet<Status> REFUND_PENDING_STATUSES = EnumSet.of(Status.REFUND_REQUESTED, Status.REFUNDING,
			Status.REFUND_FAILED);

	/**
	 * List refs with conditions via Specification
	 */
	default Page<OrderRef> findWithConditions(Status status, Type type, Pageable pageable) {
		return findWithConditions(null, status, type, pageable);
	}

	/**
	 * List refs with conditions via Specification
	 */
	default Page<OrderRef> findWithConditions(String userId, Status status, Type type, Pageable pageable) {
		Specification<OrderRef> spec = new Specification<OrderRef>() {
			@Override
			public Predicate toPredicate(Root<OrderRef> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.conjunction();
				List<Expression<Boolean>> expressions = predicate.getExpressions();
				query.orderBy(cb.desc(root.get(OrderRef_.creationDate)));
				if (Strings.isNotBlank(userId)) {
					expressions.add(cb.equal(root.get(OrderRef_.ownerId), userId));
				}
				if (status != null) {
					expressions.add(cb.equal(root.get(OrderRef_.status), status));
				}
				if (type != null) {
					expressions.add(cb.equal(root.get(OrderRef_.type), type));
				}
				return predicate;
			}
		};
		return findAll(spec, pageable);
	}

	/**
	 * Test if there is any order for user
	 * 
	 * @param userId the userId
	 * @return true if there is an order of this user, false otherwise
	 */
	boolean existsByOwnerId(String userId);

	/**
	 * Find order by order id. (NOTE: same as findOne(id))
	 * 
	 * @param orderId the order id
	 * @return order ref found
	 */
	OrderRef findByOrderId(String orderId);

	/**
	 * Find order by orderNo (fuzzy match)
	 * 
	 * @param orderNo the orderNo
	 * @return order refs list or empty
	 */
	List<OrderRef> findByOrderNoStartingWithIgnoreCase(String orderNo);

	/**
	 * Find order by order number.
	 * 
	 * @param orderId the order number
	 * @return order ref found
	 */
	OrderRef findByOrderNo(String orderNo);

	/**
	 * Find user orders in status (USER)
	 */
	Page<OrderRef> findByOwnerIdAndStatusOrderByCreationDateDesc(String userId, Status status, Pageable pageable);

	/**
	 * Find user orders in status (USER)
	 */
	Page<OrderRef> findByOwnerIdAndStatusInOrderByCreationDateDesc(String userId, Collection<Status> statuses,
			Pageable pageable);

	/**
	 * Find user order in status for history data before the {@code creationDate} (USER)
	 */
	Page<OrderRef> findByOwnerIdAndStatusInAndCreationDateLessThanEqualOrderByCreationDateDesc(String userId,
			Collection<Status> statuses, Date creationDate, Pageable pageable);

	// ( USED RARE, less important)
	/**
	 * Find all user orders (type:ref, Using where) (ADMIN)
	 * 
	 * @param userId the userId
	 * @param status the status
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<OrderRef> findByOwnerIdAndStatusAndTypeOrderByCreationDateDesc(String userId, Status status, Type type,
			Pageable pageable);

	// ( USED RARE, less important)
	/**
	 * Find all user orders (type:ref, Using where) (ADMIN)
	 * 
	 * @param userId the userId
	 * @param type the type
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<OrderRef> findByOwnerIdAndTypeOrderByCreationDateDesc(String userId, Type type, Pageable pageable);

	// ( USED RARE, less important)
	/**
	 * Find all user orders (type:ref, Using index condition; Using where; Using filesort) (ADMIN)
	 * 
	 * @param userId the userId
	 * @param pageable page request
	 * @return a page of orders or empty if none
	 */
	Page<OrderRef> findByOwnerIdOrderByCreationDateDesc(String userId, Pageable pageable);

	/**
	 * Find user orders in statuses for recent N days (USER)
	 */
	Page<OrderRef> findByOwnerIdAndStatusInAndCreationDateGreaterThanEqualOrderByCreationDateDesc(String userId,
			Collection<Status> statuses, Date creationDate, Pageable pageable);

	/**
	 * Stream of orders in PAYMENT pending status (ADMIN)
	 */
	default Stream<OrderRef> findPaymentPendingOrders(Date creationDate) {
		return findByStatusInAndCreationDateGreaterThanEqual(PAYMENT_PENDING_STATUSES, creationDate);
	}

	/**
	 * Stream of orders in REFUND pending status (ADMIN)
	 */
	default Stream<OrderRef> findRefundPendingOrders(Date creationDate) {
		return findByStatusInAndCreationDateGreaterThanEqual(REFUND_PENDING_STATUSES, creationDate);
	}

	/**
	 * (ADMIN)
	 */
	Page<OrderRef> findByTypeOrderByCreationDateDesc(Type type, Pageable pageable);

	/**
	 * Slice of orders in PAYMENT pending status (ADMIN)
	 */
	default Slice<OrderRef> findPaymentPendingOrders(Date sinceDate, Pageable pageable) {
		return findByStatusInAndCreationDateGreaterThanEqual(PAYMENT_PENDING_STATUSES, sinceDate, pageable);
	}

	/**
	 * Slice of orders in REFUND pending status (ADMIN)
	 */
	default Slice<OrderRef> findRefundPendingOrders(Date sinceDate, Pageable pageable) {
		return findByStatusInAndCreationDateGreaterThanEqual(REFUND_PENDING_STATUSES, sinceDate, pageable);
	}

	Slice<OrderRef> findByStatusInAndCreationDateGreaterThanEqual(Collection<Status> statuses, Date sinceDate,
			Pageable pageable);

	/**
	 * Stream of orders in statuses (ADMIN)
	 */
	// it seems that when using MySQL in order to really stream the results we need to satisfy three conditions:
	// 1) Forward-only resultset
	// 2) Read-only statement
	// 3) Fetch-size set to Integer.MIN_VALUE
	@QueryHints(value = @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE))
	Stream<OrderRef> findByStatusInAndCreationDateGreaterThanEqual(Collection<Status> statuses, Date creationDate);

	@Modifying
	@Query("UPDATE #{#entityName} e set e.status = :status where e.orderId = :orderId")
	void updateOrderStatus(@Param("orderId") String orderId, @Param("status") Order.Status status);

	@Modifying
	@Query("UPDATE #{#entityName} e set e.method = :method where e.orderId = :orderId")
	void updateOrderPaymentMethod(@Param("orderId") String orderId, @Param("method") Trade.Method method);

	/**
	 * Delete all the order refs of a user.
	 * 
	 * @param userId the userId
	 * @return the records deleted
	 */
	long deleteByOwnerId(String userId);

}
