package net.aircommunity.platform.service.internal.payment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.PaymentResponse;
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

	@Resource
	protected Configuration configuration;

	@Resource
	protected CommonOrderService commonOrderService;

	@Resource
	protected RefundRepository refundRepository;

	@Resource
	protected ObjectMapper objectMapper;

	protected abstract PaymentResponse getPaymentFailureResponse();

	protected abstract PaymentResponse getPaymentSuccessResponse();

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
		if (!order.isPayable()) {
			LOG.error("Order status is {}, and it is NOT payable,  payment failed", order.getStatus());
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
