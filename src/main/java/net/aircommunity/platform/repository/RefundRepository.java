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
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.model.domain.Trade;

/**
 * Repository interface for {@link Refund} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface RefundRepository extends JpaRepository<Refund, String> {

	Optional<Refund> findByTradeNoAndMethod(String tradeNo, Trade.Method method);

	Optional<Refund> findByOrderNoAndMethod(String orderNo, Trade.Method method);

	Page<Refund> findByOwnerIdAndMethodOrderByTimestampDesc(String userId, Trade.Method method, Pageable pageable);

	Page<Refund> findByVendorIdAndMethodOrderByTimestampDesc(String tenantId, Trade.Method method, Pageable pageable);

	// for metrics

	// ADMIN
	@Query("select r from #{#entityName} r")
	Slice<Refund> findAllWithSlice(Pageable pageable);

	Slice<Refund> findByTimestampBetween(Date start, Date end, Pageable pageable);

	// TENANT
	Slice<Refund> findByVendorId(String tenantId, Pageable pageable);

	Slice<Refund> findByVendorIdAndTimestampBetween(String tenantId, Date start, Date end, Pageable pageable);

	// SUM
	@Nullable
	default BigDecimal expenseOf(@Nullable String tenantId, @Nonnull Trade.Method method, @Nonnull Date start,
			@Nonnull Date end) {
		if (Strings.isBlank(tenantId)) {
			return sumRefunds(method, Trade.Status.SUCCESS, start, end);
		}
		return sumVendorRefunds(tenantId, method, Trade.Status.SUCCESS, start, end);
	}

	@Query("SELECT SUM(e.amount) from #{#entityName} e WHERE e.method =:method "
			+ "AND e.status =:status AND e.timestamp BETWEEN :startDate AND :endDate")
	BigDecimal sumRefunds(@Param("method") Trade.Method method, @Param("status") Trade.Status status,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("SELECT SUM(e.amount) from #{#entityName} e WHERE e.vendor.id =:tenantId "
			+ "AND e.method =:method AND e.status =:status AND e.timestamp BETWEEN :startDate AND :endDate")
	BigDecimal sumVendorRefunds(@Param("tenantId") String tenantId, @Param("method") Trade.Method method,
			@Param("status") Trade.Status status, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
