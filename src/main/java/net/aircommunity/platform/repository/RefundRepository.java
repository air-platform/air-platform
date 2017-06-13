package net.aircommunity.platform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.Refund;

/**
 * Repository interface for {@link Refund} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface RefundRepository extends JpaRepository<Refund, String> {

	Optional<Refund> findByMethodAndTradeNo(Payment.Method method, String tradeNo);
}
