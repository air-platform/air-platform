package net.aircommunity.platform.service.internal.payment;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment.Method;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.RefundResponse;

/**
 * HNA NewPay PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
public class NewpayPaymentGateway extends AbstractPaymentGateway {

	@Override
	public Method getPaymentMethod() {
		return Method.NEWPAY;
	}

	@Override
	public PaymentRequest createPaymentRequest(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentVerification verifyClientPaymentNotification(PaymentNotification notification) {
		// TODO Auto-generated method stub
		return PaymentVerification.SUCCESS;
	}

	@Override
	public PaymentResponse processServerPaymentNotification(PaymentNotification notification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefundResponse refundPayment(Order order, BigDecimal refundAmount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PaymentResponse getPaymentFailureResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PaymentResponse getPaymentSuccessResponse() {
		// TODO Auto-generated method stub
		return null;
	}

}
