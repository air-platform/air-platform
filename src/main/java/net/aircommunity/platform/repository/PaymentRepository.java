package net.aircommunity.platform.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Payment;

/**
 * Repository interface for {@link Payment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface PaymentRepository extends JpaRepository<Payment, String> {

	Optional<Payment> findByTradeNoAndMethod(String tradeNo, Payment.Method method);

	Optional<Payment> findByOrderNoAndMethod(String orderNo, Payment.Method method);

	Page<Payment> findByOwnerIdAndMethodOrderByTimestampDesc(String userId, Payment.Method method, Pageable pageable);

	Page<Payment> findByVendorIdAndMethodOrderByTimestampDesc(String tenantId, Payment.Method method,
			Pageable pageable);

	// XXX NOTE: Will be full table scan for findByMethod() with current index
	// if we add method index, it cannot use right index for findByOwnerIdAndMethodOrderByTimestampDesc, why?

}
