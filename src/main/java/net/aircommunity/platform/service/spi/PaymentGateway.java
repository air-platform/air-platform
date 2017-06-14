package net.aircommunity.platform.service.spi;

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
 * SPI for payment gateway (e.g. alipay, wechat, newpay etc.).
 * 
 * @author Bin.Zhang
 */
public interface PaymentGateway {

	@Nonnull
	Payment.Method getPaymentMethod();

	@Nonnull
	PaymentRequest createPaymentRequest(@Nonnull Order order);

	@Nonnull
	PaymentVerification verifyClientPaymentNotification(@Nonnull PaymentNotification notification);

	@Nonnull
	PaymentResponse processServerPaymentNotification(@Nonnull PaymentNotification notification);

	@Nonnull
	RefundResponse refundPayment(@Nonnull Order order, @Nonnull BigDecimal refundAmount);

}
