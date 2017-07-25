package net.aircommunity.platform.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.OrderItemCandidate;

/**
 * Repository interface for {@link OrderItemCandidate} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * <p>
 * NOTE: this is NOT a repository bean, only used to share common interface methods for sub-interfaces, because
 * {@code OrderItemCandidate} is NOT an entity.
 * 
 * @author Bin.Zhang
 */
@NoRepositoryBean
public interface OrderItemCandidateRepository<T extends OrderItemCandidate> extends JpaRepository<T, String> {

	/**
	 * Count new order for current month. (e.g. 2017-07-01 00:00:00 - 2017-07-01 23:59:59)
	 * 
	 * @return count of new orders
	 */
	default long countThisMonth() {
		return CountSupport.countThisMonth(this::countByOrderCreationDateBetween);
	}

	/**
	 * Count new order for today. (e.g. 2017-07-11 00:00:00 - 2017-07-11 23:59:59)
	 * 
	 * @return count of new orders
	 */
	default long countToday() {
		return CountSupport.countToday(this::countByOrderCreationDateBetween);
	}

	long countByOrderCreationDateBetween(Date firstDate, Date lastDate);

	Page<T> findDistinctOrderByVendorId(String tenantId, Pageable pageable);

	// TODO: optimize ?
	// NOTE:used in TENANT & ADMIN console, DELETED status is filtered out
	Page<T> findDistinctOrderByVendorIdAndStatusNotOrderByOrderCreationDateDesc(String tenantId,
			OrderItemCandidate.Status status, Pageable pageable);

	Page<T> findDistinctOrderByVendorIdAndStatusNotInOrderByOrderCreationDateDesc(String tenantId,
			Set<OrderItemCandidate.Status> statuses, Pageable pageable);

	// TODO: optimize ?
	Page<T> findDistinctOrderByVendorIdAndOrderStatusOrderByOrderCreationDateDesc(String tenantId, Order.Status status,
			Pageable pageable);

	Page<T> findByVendorId(String tenantId, Pageable pageable);

	Page<T> findByVendorIdAndOrderStatus(String tenantId, Order.Status status, Pageable pageable);

	List<T> findByOrderId(String orderId);

	List<T> findByOrderIdAndStatus(String orderId, OrderItemCandidate.Status status);

	default boolean isCandidateSelected(String orderId) {
		return !findByOrderIdAndStatus(orderId, OrderItemCandidate.Status.SELECTED).isEmpty();
	}

}
