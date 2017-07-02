package net.aircommunity.platform.repository;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.domain.OrderRef;

/**
 * Repository interface for {@link OrderRef} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface OrderRefRepository extends JpaRepository<OrderRef, String> {

	// probably payment pending --- can be updated to --> PAID
	EnumSet<Status> PAYMENT_PENDING_STATUSES = EnumSet.of(Status.PUBLISHED, Status.CREATED, Status.CUSTOMER_CONFIRMED,
			Status.CONFIRMED, Status.CONTRACT_SIGNED, Status.TICKET_RELEASED, Status.PAYMENT_IN_PROCESS,
			Status.PARTIAL_PAID, Status.PAYMENT_FAILED);

	// probably refund pending --- can be updated to --> REFUND
	EnumSet<Status> REFUND_PENDING_STATUSES = EnumSet.of(Status.REFUND_REQUESTED, Status.REFUNDING,
			Status.REFUND_FAILED);

	OrderRef findByOrderId(String orderId);

	OrderRef findByOrderNo(String orderNo);

	/**
	 * Find user orders in statuses (USER)
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

	/**
	 * Delete all the order refs of a user.
	 * 
	 * @param userId the userId
	 * @return the records deleted
	 */
	long deleteByOwnerId(String userId);

}
