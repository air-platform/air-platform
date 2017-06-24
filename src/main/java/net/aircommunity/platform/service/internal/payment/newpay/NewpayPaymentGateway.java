package net.aircommunity.platform.service.internal.payment.newpay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.RefundResponse;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment.Method;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.internal.payment.AbstractPaymentGateway;
import okhttp3.OkHttpClient;

/**
 * HNA NewPay PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class NewpayPaymentGateway extends AbstractPaymentGateway {
	// used to distinguish payment or refund notification. WTF!!!
	// WHY not provide an official field, e.g. notificationType
	private static final String PAY_KEYWORD = "payAmount";
	private static final String REFUND_KEYWORD = "refundAmount";
	private static final PaymentResponse NOTIFICATION_RESPONSE_FAILURE = new PaymentResponse(409, "failure");

	private final NewpayClient client;
	private final NewpayConfig config;

	@Autowired
	public NewpayPaymentGateway(NewpayConfig config, OkHttpClient httpClient) {
		this.config = config;
		this.client = new NewpayClient(this.config, httpClient);
	}

	@PostConstruct
	private void init() {
		LOG.debug("Newpay config {}", config);
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

	@SuppressWarnings("unchecked")
	@Override
	public PaymentVerification verifyClientPaymentNotification(PaymentNotification notification) {
		Map<String, String> params = (Map<String, String>) notification.getData();
		LOG.info("client notificaion params: {}", params);
		try {
			NewpayPayResponse response = NewpayPayResponse.parse(params);
			LOG.info("client notificaion response: {}", response);
			if (response.isTradeSuccessful()) {
				String orderNo = response.getOrderNo();
				Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
				if (!orderRef.isPresent()) {
					LOG.error("OrderNo: {} not exists, payment failed", orderNo);
					return PaymentVerification.FAILURE;
				}
				commonOrderService.updateOrderStatus(orderRef.get().getId(), Order.Status.PAYMENT_IN_PROCESS);
				return PaymentVerification.SUCCESS;
			}
		}
		catch (Exception e) {
			LOG.error(String.format("Payment result verification failure, cause:" + e.getMessage()), e);
		}
		return PaymentVerification.FAILURE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaymentResponse processServerPaymentNotification(PaymentNotification notification) {
		Map<String, String> params = (Map<String, String>) notification.getData();
		LOG.info("server notificaion: {}", params);
		// PAYMENT
		if (params.containsKey(PAY_KEYWORD)) {
			NewpayPayResponse response = NewpayPayResponse.parse(params);
			String orderNo = response.getOrderNo();
			if (response.isTradeSuccessful()) {
				String partnerId = response.getPartnerId();
				// 2) check APP_ID
				if (!config.getPartnerId().equals(partnerId)) {
					LOG.error("Partner ID mismatch expected: {}, but was: {}, payment failed", config.getPartnerId(),
							partnerId);
					return NOTIFICATION_RESPONSE_FAILURE;
				}

				// 3) check order and update status
				BigDecimal totalAmount = OrderPrices.convertPrice(response.getPayAmount());
				String tradeNo = response.getTradeNo();
				Date paymentDate = response.getCompleteTime();// 交易处理完成时间
				return doProcessPaymentNotification(totalAmount, orderNo, tradeNo, paymentDate);
			}

			Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
			if (!orderRef.isPresent()) {
				LOG.error("Order {} not found", orderNo);
				return NOTIFICATION_RESPONSE_FAILURE;
			}

			// update order status, TODO: we save failure payment?
			commonOrderService.updateOrderStatus(orderRef.get().getId(), Order.Status.PAYMENT_FAILED);
			LOG.error("Order {} payment failure, response: {}", response.getOrderNo(), response);
		}
		// REFUND
		else if (params.containsKey(REFUND_KEYWORD)) {
			NewpayRefundResponse response = NewpayRefundResponse.parse(params);
			Optional<Order> orderRef = commonOrderService.lookupByOrderNo(response.getOrderNo());
			if (!orderRef.isPresent()) {
				LOG.error("Order {} not found", response.getOrderNo());
				return NOTIFICATION_RESPONSE_FAILURE;
			}

			Order order = orderRef.get();
			// check if success
			if (response.isTradeSuccessful()) {
				// 退款总金额,单位为分
				int refundAmountReceived = response.getRefundAmount();
				BigDecimal finalRefundAmountReceived = OrderPrices.convertPrice(refundAmountReceived);
				LOG.info("Received raw refundAmount: {} from newpay server, converted to: {}", refundAmountReceived,
						finalRefundAmountReceived);
				Refund refund = new Refund();
				refund.setAmount(finalRefundAmountReceived);
				refund.setMethod(getPaymentMethod());
				refund.setOrderNo(response.getOrderNo());
				refund.setTradeNo(response.getTradeNo());// 新生订单号
				refund.setTimestamp(response.getCompleteTime());
				refund.setRefundReason(order.getRefundReason());
				refund.setRefundResult(M.msg(M.REFUND_SUCCESS));
				commonOrderService.acceptOrderRefund(order.getId(), refund);
				return NOTIFICATION_RESPONSE_SUCCESS;
			}

			// TODO: we save failure refund?
			response.getResultCode();
			response.getStateCode();

			commonOrderService.handleOrderRefundFailure(order.getId(), response.getResultMessage());
			LOG.error("Order {} refund failure, response: {}", response.getOrderNo(), response);
		}
		return NOTIFICATION_RESPONSE_FAILURE;
	}

	@Override
	public RefundResponse refundPayment(Order order, BigDecimal refundAmount) {
		LOG.info("Refund {}, refundAmount: {}", order, refundAmount);
		// if refundAmount=0, we will use full amount refund of the order
		BigDecimal originalRefundAmount = OrderPrices.normalizePrice(order.getTotalPrice());
		refundAmount = OrderPrices.normalizePrice(refundAmount);
		// if refundAmount=0, we will use full amount refund of the order
		BigDecimal actualRefundAmount = (BigDecimal.ZERO == refundAmount) ? originalRefundAmount : refundAmount;
		int finalRefundAmount = OrderPrices.convertPrice(actualRefundAmount);
		NewpayRefundModel model = new NewpayRefundModel();
		model.setOrderNo(order.getOrderNo());
		model.setRefundReason(order.getRefundReason());
		model.setRefundAmount(finalRefundAmount);
		model.setPartialRefund(actualRefundAmount.compareTo(originalRefundAmount) != 0);
		if (LOG.isInfoEnabled()) {
			LOG.info("Final refund request model: {}", model);
		}
		try {
			NewpayRefundResponse response = client.executeRefundRequest(model);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund response: {}", response);
			}
			if (response.isRequestSuccessful()) {
				// 退款总金额,单位为分
				int refundAmountReceived = response.getRefundAmount();
				BigDecimal finalRefundAmountReceived = OrderPrices.convertPrice(refundAmountReceived);
				LOG.info("Received raw refundAmount: {} from newpay server, converted to: {}", refundAmountReceived,
						finalRefundAmountReceived);
				// Refund refund = new Refund();
				// refund.setAmount(finalRefundAmountReceived);
				// refund.setMethod(getPaymentMethod());
				// refund.setOrderNo(response.getOrderNo());
				// refund.setTradeNo(response.getTradeNo());// 新生订单号
				// refund.setTimestamp(response.getCompleteTime());
				// refund.setRefundReason(order.getRefundReason());
				// refund.setRefundResult(M.msg(M.REFUND_SUCCESS));
				// return RefundResponse.success(refund);
				// XXX: because newpay need to use the result of async notification
				// TODO: we just save a pending refund? and update its status after success notification
				return RefundResponse.pending();
			}
			return RefundResponse
					.failure(M.msg(M.REFUND_FAILURE, response.getResultCode(), response.getResultMessage()));
		}
		catch (Exception e) {
			LOG.error(String.format("Refund failure for order: %s, cause: %s", order, e.getMessage()), e);
			return RefundResponse.failure(e.getLocalizedMessage());
		}
	}

}