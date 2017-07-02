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
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.RefundRepository;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.payment.PaymentService;
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

	@Resource
	protected Configuration configuration;

	@Resource
	protected CommonOrderService commonOrderService;

	@Resource
	protected RefundRepository refundRepository;

	@Resource
	protected ObjectMapper objectMapper;

	// protected PaymentResponse getPaymentFailureResponse() {
	// return NOTIFICATION_RESPONSE_FAILURE;
	// }
	//
	// protected PaymentResponse getPaymentSuccessResponse() {
	// return NOTIFICATION_RESPONSE_SUCCESS;
	// }

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

	protected PaymentResponse doProcessPaymentSuccess(BigDecimal totalAmount, String orderNo, String tradeNo,
			Date paymentDate) {
		// 1) check if same tradeNo to avoid pay multiple time
		Optional<Payment> paymentRef = commonOrderService.findPaymentByTradeNo(getPaymentMethod(), tradeNo);
		if (paymentRef.isPresent()) {
			LOG.warn("Order No: {} is already paid with {} with tradeNo: {}, notification ignored", orderNo,
					getPaymentMethod(), tradeNo);
			return PaymentResponse.success();
		}

		// 2) check orderNo existence
		Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
		if (!orderRef.isPresent()) {
			LOG.error("OrderNo: {} not exists, payment failed", orderNo);
			return PaymentResponse.failure(PaymentResponse.Status.ORDER_NOT_FOUND, M.msg(M.ORDER_NOT_FOUND, orderNo));
		}

		// handle payment failure: set status to PAYMENT_FAILED, except for order not found

		// 3) check total amount
		Order order = orderRef.get();
		if (!OrderPrices.priceMatches(totalAmount, order.getTotalPrice())) {
			LOG.error("Order total amount mismatch, expected: {}, but was: {},  payment failed", order.getTotalPrice(),
					totalAmount);
			commonOrderService.handleOrderPaymentFailure(order, M.msg(M.PAYMENT_SERVER_NOTIFY_BIZ_FAILURE,
					M.msg(M.ORDER_TOTAL_AMOUNT_MISMATCH, orderNo, totalAmount, order.getTotalPrice())));
			return PaymentResponse.failure(PaymentResponse.Status.TOTAL_AMOUNT_MISMATCH,
					M.msg(M.ORDER_TOTAL_AMOUNT_MISMATCH, orderNo, totalAmount, order.getTotalPrice()));

		}

		// 4) check order status
		if (!order.canFinishPayment()) {
			LOG.error("Order status is {}, and it is NOT ready to finish,  payment failed", order.getStatus());
			commonOrderService.handleOrderPaymentFailure(order,
					M.msg(M.PAYMENT_SERVER_NOTIFY_BIZ_FAILURE, M.msg(M.ORDER_ILLEGAL_STATUS)));
			return PaymentResponse.failure(PaymentResponse.Status.ORDER_ILLEGAL_STATUS, M.msg(M.ORDER_ILLEGAL_STATUS));
		}

		// 5) handle payment
		Payment payment = new Payment();
		payment.setAmount(totalAmount);
		payment.setMethod(getPaymentMethod());
		payment.setOrderNo(orderNo);
		payment.setTradeNo(tradeNo);
		payment.setTimestamp(paymentDate);
		payment.setNote(M.msg(M.PAYMENT_SUCCESS_SERVER_NOTIFY));
		order = commonOrderService.payOrder(order, payment);
		LOG.debug("Payment success, order is updated: {}", order);
		return PaymentResponse.success();
	}

}
