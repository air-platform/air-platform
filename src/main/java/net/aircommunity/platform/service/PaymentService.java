package net.aircommunity.platform.service;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.RefundResponse;

/**
 * Payment service facade.
 * 
 * @author Bin.Zhang
 */
public interface PaymentService {

	String LOGGER_NAME = "net.aircommunity.platform.payment";

	/**
	 * Create payment request.
	 * 
	 * @param paymentMethod the payment method
	 * @param order the order to be paid
	 * @return PaymentRequest
	 */
	@Nonnull
	PaymentRequest createPaymentRequest(@Nonnull Payment.Method paymentMethod, @Nonnull Order order);

	/**
	 * Verify client payment notification if needed (optional).
	 * 
	 * @param paymentMethod the payment method
	 * @param notification client payment notification
	 * @return PaymentVerification result
	 */
	@Nonnull
	PaymentVerification verifyClientPaymentNotification(@Nonnull Payment.Method paymentMethod,
			@Nonnull PaymentNotification notification);

	/**
	 * Process server payment notification (required)
	 * 
	 * @param paymentMethod the payment method
	 * @param notification server payment notification
	 * @return PaymentResponse to the payment gateway
	 */
	@Nonnull
	PaymentResponse processServerPaymentNotification(@Nonnull Payment.Method paymentMethod,
			PaymentNotification notification);

	@Nonnull
	RefundResponse refundPayment(@Nonnull Payment.Method paymentMethod, @Nonnull Order order,
			@Nonnull BigDecimal refundAmount);

}
