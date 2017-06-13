package net.aircommunity.platform.service.internal.payment;

import org.springframework.stereotype.Service;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment.Method;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.RefundResponse;
import net.aircommunity.platform.service.spi.PaymentGateway;

/**
 * Wechat PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
public class WechatPaymentGateway implements PaymentGateway {

	@Override
	public Method getPaymentMethod() {
		return Method.WECHAT;
	}

	@Override
	public PaymentRequest createPaymentRequest(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentVerification verifyClientPaymentNotification(PaymentNotification notification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentResponse processServerPaymentNotification(PaymentNotification notification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RefundResponse refundPayment(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

}
