package net.aircommunity.platform.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Refund;

/**
 * Repository interface for {@link Refund} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface RefundRepository extends JpaRepository<Refund, String> {

	Optional<Refund> findByTradeNoAndMethod(String tradeNo, Payment.Method method);

	Optional<Refund> findByOrderNoAndMethod(String orderNo, Payment.Method method);

	Page<Refund> findByOwnerIdAndMethodOrderByTimestampDesc(String userId, Payment.Method method, Pageable pageable);

	Page<Refund> findByVendorIdAndMethodOrderByTimestampDesc(String tenantId, Payment.Method method, Pageable pageable);
}
