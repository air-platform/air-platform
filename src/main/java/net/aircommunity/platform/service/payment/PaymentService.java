package net.aircommunity.platform.service.payment;

import java.math.BigDecimal;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Trade;
import net.aircommunity.platform.model.metrics.TradeMetrics;
import net.aircommunity.platform.model.payment.PaymentNotification;
import net.aircommunity.platform.model.payment.PaymentRequest;
import net.aircommunity.platform.model.payment.PaymentResponse;
import net.aircommunity.platform.model.payment.PaymentVerification;
import net.aircommunity.platform.model.payment.RefundResponse;
import net.aircommunity.platform.model.payment.TradeQueryResult;

/**
 * Payment service facade.
 * 
 * @author Bin.Zhang
 */
public interface PaymentService {

	/**
	 * Create payment request. (should be called within orderService)
	 * 
	 * @param method the payment method
	 * @param order the order to be paid
	 * @return PaymentRequest
	 */
	@Nonnull
	PaymentRequest createPaymentRequest(@Nonnull Trade.Method method, @Nonnull Order order);

	/**
	 * Verify client payment notification if needed (optional).
	 * 
	 * @param method the payment method
	 * @param notification client payment notification
	 * @return PaymentVerification result
	 */
	@Nonnull
	PaymentVerification verifyClientNotification(@Nonnull Trade.Method method,
			@Nonnull PaymentNotification notification);

	/**
	 * Process server payment notification (required)
	 * 
	 * @param paymentMethod the payment method
	 * @param notification server payment notification
	 * @return PaymentResponse to the payment gateway
	 */
	@Nonnull
	PaymentResponse processServerNotification(@Nonnull Trade.Method method, @Nonnull PaymentNotification notification);

	/**
	 * Refund payment for an order.
	 * 
	 * @param method the payment method
	 * @param order the order to refund
	 * @param refundAmount the refund amount in basic currency unit (YUAN for CNY)
	 * @return refund response
	 */
	@Nonnull
	RefundResponse refundPayment(@Nonnull Trade.Method method, @Nonnull Order order, @Nonnull BigDecimal refundAmount);

	/**
	 * Query payment result.
	 * 
	 * @param method the payment method
	 * @param order the order to be queried
	 * @return the query result
	 */
	@Nonnull
	Optional<TradeQueryResult> queryPayment(@Nullable Trade.Method method, @Nonnull Order order);

	/**
	 * Query refund result.
	 * 
	 * @param method the payment method
	 * @param order the order to be queried
	 * @return the query result
	 */
	@Nonnull
	Optional<TradeQueryResult> queryRefund(@Nullable Trade.Method method, @Nonnull Order order);

	/**
	 * Get the trade metrics for the platform.
	 * 
	 * @return trade metrics
	 */
	@Nonnull
	TradeMetrics getTradeMetrics();

	/**
	 * Get the trade metrics for a tenant.
	 * 
	 * @param tenantId the tenantId
	 * @return trade metrics
	 */
	@Nonnull
	TradeMetrics getTradeMetrics(@Nonnull String tenantId);

}
