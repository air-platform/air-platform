package net.aircommunity.platform.service.internal.payment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.repository.RefundRepository;
import net.aircommunity.platform.service.CommonOrderService;
import net.aircommunity.platform.service.PaymentService;
import net.aircommunity.platform.service.spi.PaymentGateway;

/**
 * Alipay PaymentGateway
 * 
 * @author Bin.Zhang
 */
public abstract class AbstractPaymentGateway implements PaymentGateway {
	protected static final Logger LOG = LoggerFactory.getLogger(PaymentService.LOGGER_NAME);

	// .e.g http://aircommunity.net/api/v2/payment/alipay/notify
	private static final String PAYMENT_BASE_URI = "/payment/";
	private static final String PAYMENT_SERVER_NOTIFY_URI = "/notify";
	private static final String PAYMENT_CLIENT_NOTIFY_URI = "/client/notify";
	private static final String PAYMENT_CLIENT_RETURN_URI = "/client/return";
	protected static final PaymentResponse NOTIFICATION_RESPONSE_FAILURE = new PaymentResponse("failure");
	protected static final PaymentResponse NOTIFICATION_RESPONSE_SUCCESS = new PaymentResponse("success");

	@Resource
	protected Configuration configuration;

	@Resource
	protected CommonOrderService commonOrderService;

	@Resource
	protected RefundRepository refundRepository;

	@Resource
	protected ObjectMapper objectMapper;

	protected PaymentResponse getPaymentFailureResponse() {
		return NOTIFICATION_RESPONSE_FAILURE;
	}

	protected PaymentResponse getPaymentSuccessResponse() {
		return NOTIFICATION_RESPONSE_SUCCESS;
	}

	// server async notification
	protected String getServerNotifyUrl() {
		StringBuilder builder = new StringBuilder().append(configuration.getApiBaseUrl()).append(PAYMENT_BASE_URI)
				.append(getPaymentMethod().name().toLowerCase(Locale.ENGLISH)).append(PAYMENT_SERVER_NOTIFY_URI);
		return builder.toString();
	}

	// client post sync notification result to this URL
	protected String getClientNotifyUrl() {
		StringBuilder builder = new StringBuilder().append(configuration.getApiBaseUrl()).append(PAYMENT_BASE_URI)
				.append(getPaymentMethod().name().toLowerCase(Locale.ENGLISH)).append(PAYMENT_CLIENT_NOTIFY_URI);
		return builder.toString();
	}

	// client redirect
	protected String getClientReturnUrl() {
		StringBuilder builder = new StringBuilder().append(configuration.getApiBaseUrl()).append(PAYMENT_BASE_URI)
				.append(getPaymentMethod().name().toLowerCase(Locale.ENGLISH)).append(PAYMENT_CLIENT_RETURN_URI);
		return builder.toString();
	}

	protected PaymentResponse doProcessPaymentNotification(BigDecimal totalAmount, String orderNo, String tradeNo,
			Date paymentDate) {
		// 3) check orderNo existence
		Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
		if (!orderRef.isPresent()) {
			LOG.error("OrderNo: {} not exists, payment failed", orderNo);
			return getPaymentFailureResponse();
		}

		// 4) check total amount
		Order order = orderRef.get();
		if (!OrderPrices.priceMatches(totalAmount, order.getTotalPrice())) {
			LOG.error("Order total amount mismatch, expected: {}, but was: {},  payment failed", order.getTotalPrice(),
					totalAmount);
			return getPaymentFailureResponse();
		}

		// 5) check order status
		if (!order.canFinishPayment()) {
			LOG.error("Order status is {}, and it is NOT ready to finish,  payment failed", order.getStatus());
			return getPaymentFailureResponse();
		}

		// 6) check if same tradeNo to avoid pay multiple time
		Optional<Payment> paymentRef = commonOrderService.findPaymentByTradeNo(getPaymentMethod(), tradeNo);
		if (paymentRef.isPresent()) {
			LOG.warn("OrderNo: {} is already paid with {} with tradeNo: {}, notification ignored", orderNo,
					getPaymentMethod(), tradeNo);
			return getPaymentSuccessResponse();
		}

		// 7) handle payment
		Payment payment = new Payment();
		payment.setAmount(totalAmount);
		payment.setMethod(getPaymentMethod());
		payment.setOrderNo(orderNo);
		payment.setTradeNo(tradeNo);
		payment.setTimestamp(paymentDate);
		order = commonOrderService.payOrder(order.getId(), payment);
		LOG.debug("Payment success, updated order: {}", order);
		return getPaymentSuccessResponse();
	}

}
