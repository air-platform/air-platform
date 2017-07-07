package net.aircommunity.platform.service.spi;

import java.math.BigDecimal;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Trade;
import net.aircommunity.platform.model.payment.PaymentNotification;
import net.aircommunity.platform.model.payment.PaymentRequest;
import net.aircommunity.platform.model.payment.PaymentResponse;
import net.aircommunity.platform.model.payment.PaymentVerification;
import net.aircommunity.platform.model.payment.RefundResponse;
import net.aircommunity.platform.model.payment.TradeQueryResult;

/**
 * SPI for payment gateway (e.g. alipay, wechat, newpay etc.).
 * 
 * @author Bin.Zhang
 */
public interface PaymentGateway {

	/**
	 * The supported payment method (e.g. alipay, wechat, newpay etc.) of this gateway
	 * 
	 * @return payment method
	 */
	@Nonnull
	Trade.Method getPaymentMethod();

	/**
	 * Create a payment request for the given order.
	 * 
	 * @param order the order to pay
	 * @return payment request
	 */
	@Nonnull
	PaymentRequest createPaymentRequest(@Nonnull Order order);

	/**
	 * Verify client notification on payment finished (SYNC).
	 * 
	 * @param notification client payment notification
	 * @return verification result
	 */
	@Nonnull
	PaymentVerification verifyClientNotification(@Nonnull PaymentNotification notification);

	/**
	 * Process server notification on payment finished (ASYNC).
	 * 
	 * @param notification server payment notification
	 * @return payment response
	 */
	@Nonnull
	PaymentResponse processServerNotification(@Nonnull PaymentNotification notification);

	/**
	 * Refund payment for an order. (NOTE: DO NOT throw exception)
	 * 
	 * @param order the order to refund
	 * @param refundAmount the refund amount in basic currency unit (YUAN for CNY)
	 * @return refund response
	 */
	@Nonnull
	RefundResponse refundPayment(@Nonnull Order order, @Nonnull BigDecimal refundAmount);

	/**
	 * Query payment result.
	 * 
	 * @param order the order to be queried
	 * @return the query result
	 */
	@Nonnull
	Optional<TradeQueryResult> queryPayment(@Nonnull Order order);

	/**
	 * Query refund result.
	 * 
	 * @param order the order to be queried
	 * @return the query result
	 */
	@Nonnull
	Optional<TradeQueryResult> queryRefund(@Nonnull Order order);

}
