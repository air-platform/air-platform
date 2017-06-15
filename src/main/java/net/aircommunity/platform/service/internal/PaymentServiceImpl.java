package net.aircommunity.platform.service.internal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.RefundResponse;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.CommonOrderService;
import net.aircommunity.platform.service.PaymentService;
import net.aircommunity.platform.service.spi.PaymentGateway;

/**
 * Payment service facade implementation
 * 
 * @author Bin.Zhang
 */
@Service
public class PaymentServiceImpl implements PaymentService {
	private static final Logger LOG = LoggerFactory.getLogger(LOGGER_NAME);

	private static final String KEY_PAYMENT_METHOD = "paymentMethod";
	private static final String KEY_PAYMENT_ACTION = "paymentAction";

	private static final String ACTION_PAYMENT = "PAYMENT";
	private static final String ACTION_REFUND = "REFUND";
	private static final String ACTION_VERIFY_CLIENT_NOTIFY = "CLT_NOTIFY";
	private static final String ACTION_PROCESS_SERVER_NOTIFY = "SRV_NOTIFY";
	private final Map<Payment.Method, PaymentGateway> paymentGatewayRegistry;

	@Resource
	private CommonOrderService commonOrderService;

	@Autowired
	public PaymentServiceImpl(List<PaymentGateway> paymentGateways) {
		ImmutableMap.Builder<Payment.Method, PaymentGateway> builder = ImmutableMap.builder();
		paymentGateways.stream()
				.forEach(paymentGateway -> builder.put(paymentGateway.getPaymentMethod(), paymentGateway));
		paymentGatewayRegistry = builder.build();
	}

	@PostConstruct
	private void init() {
		LOG.info("Loaded payment gateway: {}", paymentGatewayRegistry);
	}

	@Override
	public PaymentRequest createPaymentRequest(Payment.Method paymentMethod, Order order) {
		// Order order = commonOrderService.findByOrderNo(orderNo);
		if (!order.isPayable()) {
			LOG.error("Order [{}] is not ready to pay", order.getOrderNo());
			throw new AirException(Codes.ORDER_NOT_PAYABLE, M.msg(M.ORDER_NOT_PAYABLE, order.getOrderNo()));
		}
		PaymentGateway paymentGateway = getPaymentGateway(paymentMethod);
		return call(paymentMethod, ACTION_PAYMENT, () -> paymentGateway.createPaymentRequest(order));
	}

	@Override
	public PaymentVerification verifyClientPaymentNotification(Payment.Method paymentMethod,
			PaymentNotification notification) {
		PaymentGateway paymentGateway = getPaymentGateway(paymentMethod);
		return call(paymentMethod, ACTION_VERIFY_CLIENT_NOTIFY,
				() -> paymentGateway.verifyClientPaymentNotification(notification));
	}

	@Transactional
	@Override
	public PaymentResponse processServerPaymentNotification(Payment.Method paymentMethod,
			PaymentNotification notification) {
		PaymentGateway paymentGateway = getPaymentGateway(paymentMethod);
		return call(paymentMethod, ACTION_PROCESS_SERVER_NOTIFY,
				() -> paymentGateway.processServerPaymentNotification(notification));
	}

	@Override
	public RefundResponse refundPayment(Payment.Method paymentMethod, Order order, BigDecimal refundAmount) {
		PaymentGateway paymentGateway = getPaymentGateway(paymentMethod);
		return call(paymentMethod, ACTION_REFUND, () -> paymentGateway.refundPayment(order, refundAmount));
	}

	private PaymentGateway getPaymentGateway(Payment.Method paymentMethod) {
		PaymentGateway paymentGateway = paymentGatewayRegistry.get(paymentMethod);
		if (paymentGateway == null) {
			LOG.error("Payment gateway not found for payment method: {}", paymentMethod);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
		return paymentGateway;
	}

	private <T> T call(Payment.Method paymentMethod, String action, Callable<T> call) {
		MDC.put(KEY_PAYMENT_METHOD, paymentMethod.name());
		MDC.put(KEY_PAYMENT_ACTION, action);
		try {
			T result = call.call();
			LOG.info("Result: {}", result);
			return result;
		}
		catch (Exception e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
		MDC.remove(KEY_PAYMENT_ACTION);
		MDC.remove(KEY_PAYMENT_METHOD);
		return null;
	}
}
