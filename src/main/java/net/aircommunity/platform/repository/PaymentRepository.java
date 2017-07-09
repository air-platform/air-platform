package net.aircommunity.platform.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.micro.common.Strings;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Trade;

/**
 * Repository interface for {@link Payment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface PaymentRepository extends JpaRepository<Payment, String> {

	Optional<Payment> findByTradeNoAndMethod(String tradeNo, Trade.Method method);

	Optional<Payment> findByOrderNoAndMethod(String orderNo, Trade.Method method);

	Page<Payment> findByOwnerIdAndMethodOrderByTimestampDesc(String userId, Trade.Method method, Pageable pageable);

	Page<Payment> findByVendorIdAndMethodOrderByTimestampDesc(String tenantId, Trade.Method method, Pageable pageable);

	// for metrics

	// ADMIN
	@Query("select r from #{#entityName} r")
	Slice<Payment> findAllWithSlice(Pageable pageable);

	Slice<Payment> findByTimestampBetween(Date start, Date end, Pageable pageable);

	// TENANT
	Slice<Payment> findByVendorId(String tenantId, Pageable pageable);

	Slice<Payment> findByVendorIdAndTimestampBetween(String tenantId, Date start, Date end, Pageable pageable);

	// SUM
	@Nullable
	default BigDecimal revenueOf(@Nullable String tenantId, @Nonnull Trade.Method method, @Nonnull Date start,
			@Nonnull Date end) {
		if (Strings.isBlank(tenantId)) {
			return sumPayments(method, Trade.Status.SUCCESS, start, end);
		}
		return sumVendorPayments(tenantId, method, Trade.Status.SUCCESS, start, end);
	}

	@Query("SELECT SUM(e.amount) from #{#entityName} e WHERE e.method =:method "
			+ "AND e.status =:status AND e.timestamp BETWEEN :startDate AND :endDate")
	BigDecimal sumPayments(@Param("method") Trade.Method method, @Param("status") Trade.Status status,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT SUM(e.amount) from #{#entityName} e WHERE e.vendor.id =:tenantId "
			+ "AND e.method =:method AND e.status =:status AND e.timestamp BETWEEN :startDate AND :endDate")
	BigDecimal sumVendorPayments(@Param("tenantId") String tenantId, @Param("method") Trade.Method method,
			@Param("status") Trade.Status status, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	// XXX NOTE: Will be full table scan for findByMethod() with current index
	// if we add method index, it cannot use right index for findByOwnerIdAndMethodOrderByTimestampDesc, why?

}
