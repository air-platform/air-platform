package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.FleetCandidate;
import net.aircommunity.platform.model.domain.Order;

/**
 * Repository interface for {@link FleetCandidate} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface FleetCandidateRepository extends JpaRepository<FleetCandidate, String> {

	Page<FleetCandidate> findDistinctOrderByVendorId(String tenantId, Pageable pageable);

	Page<FleetCandidate> findDistinctOrderByVendorIdAndStatusNot(String tenantId, FleetCandidate.Status status,
			Pageable pageable);

	Page<FleetCandidate> findDistinctOrderByVendorIdAndOrderStatus(String tenantId, Order.Status status,
			Pageable pageable);

	//
	Page<FleetCandidate> findByVendorId(String tenantId, Pageable pageable);

	Page<FleetCandidate> findByVendorIdAndOrderStatus(String tenantId, Order.Status status, Pageable pageable);

	List<FleetCandidate> findByOrderId(String orderId);

	List<FleetCandidate> findByOrderIdAndStatus(String orderId, FleetCandidate.Status status);

	default boolean isFleetCandidateSelected(String orderId) {
		return !findByOrderIdAndStatus(orderId, FleetCandidate.Status.SELECTED).isEmpty();
	}

}
