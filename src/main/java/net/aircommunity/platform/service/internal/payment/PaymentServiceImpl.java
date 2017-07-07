package net.aircommunity.platform.service.internal.payment;

import static net.aircommunity.platform.model.payment.Payments.ACTION_PAY_REQUEST;
import static net.aircommunity.platform.model.payment.Payments.ACTION_PROCESS_SERVER_NOTIFY;
import static net.aircommunity.platform.model.payment.Payments.ACTION_QUERY_PAYMENT;
import static net.aircommunity.platform.model.payment.Payments.ACTION_QUERY_REFUND;
import static net.aircommunity.platform.model.payment.Payments.ACTION_REFUND;
import static net.aircommunity.platform.model.payment.Payments.ACTION_VERIFY_CLIENT_NOTIFY;
import static net.aircommunity.platform.model.payment.Payments.KEY_PAYMENT_ACTION;
import static net.aircommunity.platform.model.payment.Payments.KEY_PAYMENT_METHOD;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Trade;
import net.aircommunity.platform.model.payment.PaymentNotification;
import net.aircommunity.platform.model.payment.PaymentRequest;
import net.aircommunity.platform.model.payment.PaymentResponse;
import net.aircommunity.platform.model.payment.PaymentVerification;
import net.aircommunity.platform.model.payment.Payments;
import net.aircommunity.platform.model.payment.RefundResponse;
import net.aircommunity.platform.model.payment.TradeQueryResult;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.payment.PaymentService;
import net.aircommunity.platform.service.spi.PaymentGateway;

/**
 * Payment service facade implementation
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
	private static final Logger LOG = LoggerFactory.getLogger(Payments.LOGGER_NAME);

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
		LOG.info("Loaded payment gateways: {}", paymentGatewayRegistry);
	}

	// NOTE: order status is not checked (whether payable or not)
	@Override
	public PaymentRequest createPaymentRequest(Trade.Method method, Order order) {
		// TODO check if payment is available? order.getPayment!=null ?
		PaymentGateway paymentGateway = getPaymentGateway(method);
		return call(method, ACTION_PAY_REQUEST, () -> paymentGateway.createPaymentRequest(order));
	}

	@Transactional
	@Override
	public PaymentVerification verifyClientNotification(Trade.Method method, PaymentNotification notification) {
		PaymentGateway paymentGateway = getPaymentGateway(method);
		return call(method, ACTION_VERIFY_CLIENT_NOTIFY, () -> paymentGateway.verifyClientNotification(notification));
	}

	@Transactional
	@Override
	public PaymentResponse processServerNotification(Trade.Method method, PaymentNotification notification) {
		PaymentGateway paymentGateway = getPaymentGateway(method);
		return call(method, ACTION_PROCESS_SERVER_NOTIFY, () -> paymentGateway.processServerNotification(notification));
	}

	@Transactional
	@Override
	public RefundResponse refundPayment(Trade.Method method, Order order, BigDecimal refundAmount) {
		PaymentGateway paymentGateway = getPaymentGateway(method);
		return call(method, ACTION_REFUND, () -> paymentGateway.refundPayment(order, refundAmount));
	}

	@Override
	public Optional<TradeQueryResult> queryPayment(Trade.Method method, Order order) {
		PaymentGateway paymentGateway = getPaymentGateway(method);
		return call(method, ACTION_QUERY_PAYMENT, () -> paymentGateway.queryPayment(order));
	}

	@Override
	public Optional<TradeQueryResult> queryRefund(Trade.Method method, Order order) {
		PaymentGateway paymentGateway = getPaymentGateway(method);
		return call(method, ACTION_QUERY_REFUND, () -> paymentGateway.queryRefund(order));
	}

	private PaymentGateway getPaymentGateway(Trade.Method method) {
		PaymentGateway paymentGateway = paymentGatewayRegistry.get(method);
		if (paymentGateway == null) {
			LOG.error("Payment gateway not found for payment method: {}", method);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.PAYMENT_SERVICE_UNAVAILABLE));
		}
		return paymentGateway;
	}

	private <T> T call(Trade.Method method, String action, Callable<T> call) {
		MDC.put(KEY_PAYMENT_METHOD, method.name());
		MDC.put(KEY_PAYMENT_ACTION, action);
		try {
			T result = call.call();
			LOG.info("Result: {}", result);
			return result;
		}
		catch (Exception e) {
			LOG.error("Error: " + e.getMessage(), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.PAYMENT_SERVICE_UNAVAILABLE));
		}
		finally {
			MDC.remove(KEY_PAYMENT_ACTION);
			MDC.remove(KEY_PAYMENT_METHOD);
		}
	}

}
