package net.aircommunity.platform.service.internal.payment;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment.Method;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.RefundResponse;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.internal.payment.newpay.NewpayClient;
import net.aircommunity.platform.service.internal.payment.newpay.NewpayConfig;
import net.aircommunity.platform.service.internal.payment.newpay.NewpayPayModel;
import net.aircommunity.platform.service.internal.payment.newpay.NewpayPayRequest;

/**
 * HNA NewPay PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
public class NewpayPaymentGateway extends AbstractPaymentGateway {

	private NewpayClient client;

	@Resource
	private NewpayConfig config;

	@PostConstruct
	private void init() {
		LOG.debug("Configuration {}", config);
		client = new NewpayClient(config);
		// int totalAmount = 100;
		// NewpayPayModel model = new NewpayPayModel();
		// model.setBody("测试机票A");
		// model.setOrderNo("0123456789");
		// model.setQuantity(1);
		// model.setTotalAmount(totalAmount);
		// NewpayPayRequest req = client.createPayRequest(model);
		// try {
		// String json = ObjectMappers.createObjectMapper().writeValueAsString(req);
		// System.out.println(json);
		// }
		// catch (JsonProcessingException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public Method getPaymentMethod() {
		return Method.NEWPAY;
	}

	@Override
	public PaymentRequest createPaymentRequest(Order order) {
		LOG.debug("Create payment request for order: {}", order);
		LOG.info("orderNo: {}", order.getOrderNo());
		try {
			NewpayPayModel model = new NewpayPayModel();
			Product product = order.getProduct();
			model.setBody(M.msg(M.PAYMENT_PRODUCT_DESCRIPTION, product.getName()));
			model.setOrderNo(order.getOrderNo());
			// model.setParterName(order.getProduct().getVendor().getNickName());
			model.setQuantity(order.getQuantity());
			int totalAmount = OrderPrices.convertPrice(order.getTotalPrice());
			model.setTotalAmount(totalAmount);
			NewpayPayRequest orderInfo = client.createPayRequest(model);
			LOG.info("orderInfo: {}", orderInfo);
			return new PaymentRequest(orderInfo);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to create payment info, cause: %s", e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	@Override
	public PaymentVerification verifyClientPaymentNotification(PaymentNotification notification) {
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
