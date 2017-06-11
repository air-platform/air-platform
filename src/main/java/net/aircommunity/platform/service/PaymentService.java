package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;

/**
 * Payment service facade.
 * 
 * @author Bin.Zhang
 */
public interface PaymentService {

	/**
	 * 
	 * @param paymentMethod
	 * @param orderNo
	 * @return
	 */
	@Nonnull
	PaymentRequest createPaymentRequest(@Nonnull Payment.Method paymentMethod, @Nonnull String orderNo);

	@Nonnull
	PaymentVerification verifyClientPaymentNotification(@Nonnull Payment.Method paymentMethod,
			@Nonnull PaymentNotification notification);

	@Nonnull
	PaymentResponse processServerPaymentNotification(@Nonnull Payment.Method paymentMethod,
			PaymentNotification notification);

}
