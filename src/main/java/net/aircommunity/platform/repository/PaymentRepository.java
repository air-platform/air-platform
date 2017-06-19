package net.aircommunity.platform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Payment;

/**
 * Repository interface for {@link Payment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface PaymentRepository extends JpaRepository<Payment, String> {

	Optional<Payment> findByMethodAndTradeNo(Payment.Method method, String tradeNo);
}
