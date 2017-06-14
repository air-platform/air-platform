package net.aircommunity.platform.service.internal;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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

	// TODO REMOVE
	public static void main(String[] args) throws Exception {
		// PaymentServiceImpl p = new PaymentServiceImpl();
		// // OrderPaymentInfo info = p.createPaymentInfo("170609O7FNP0K84400");
		// // System.out.println(info.getPaymentInfo());
		//
		// Map<String, String> result = new HashMap<>();
		// //
		// {"code":"10000","msg":"Success","total_amount":"9.00","app_id":"2014072300007148","trade_no":"2014112400001000340011111118","seller_id":"2088111111116894","out_trade_no":"70501111111S001111119"}
		//
		// result.put("memo", "xxx");
		// result.put("resultStatus", "9000");
		// // " com.alipay.api.AlipayApiException: com.alipay.api.AlipayApiException: sign check fail: check Sign and
		// Data
		// // Fail!
		// result.put("result",
		// "{\"alipay_trade_app_pay_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"app_id\":\"2017060907455116\",\"auth_app_id\":\"2017060907455116\",\"charset\":\"utf-8\",\"timestamp\":\"2017-06-10
		// 12:34:20\",\"total_amount\":\"0.01\",\"trade_no\":\"2017061021001004440265148445\",\"seller_id\":\"2088521250420360\",\"out_trade_no\":\"V0900OLFV7E7O1F\"},\"sign\":\"lrzO3JyTDA8WFdz//LyiS6OKxuucjgKJFBh+uc0XILfZ5t66Flt8GrE9gb6rrcSyxKFmNAeWRiLsBy51omWP6YiLcD5VRV5PTTwzTQQvg1T0qu13UmBYRragbXaCl0eACYC+DP1275blcFtnW+cyShcomJWwI8wUDchXTm/rvmV/q61/U2GJyYIii+nNvqeYJd00D+MC9zsD7evO/fo4G6hytzUkyNL+0/z5S4x8H2EdF0Q8Qb9NWycsGsbBbwEAEQL2QxvgQxujBI3lgEPcCtMuorHy5yL2afBm7U6lAS7hBYvbo80r3SmvlMJjUi3Yq4s6iQ2psZBKCdN2QU5zsA==\",\"sign_type\":\"RSA2\"}");
		// AlipayTradeAppPayResponse response = p.alipayClient.parseAppSyncResult(result,
		// AlipayTradeAppPayRequest.class);
		//
		// ObjectMapper objectMapper = ObjectMappers.createObjectMapper();
		//
		// Map<String, Object> data = objectMapper.readValue(response.getBody(), new TypeReference<Map<String,
		// Object>>() {
		// });
		//
		// Map<String, Object> tradePayResponse = (Map<String, Object>) data.get(PAYMENT_RESULT_TRADE_APP_PAY_RESPONSE);
		//
		// System.out.println(tradePayResponse.get(PAYMENT_RESULT_TRADE_APP_PAY_RESPONSE_APP_ID));
		// System.out.println(response.getBody());
		// System.out.println(response.getTotalAmount());
		// System.out.println(response.getTradeNo());
		// System.out.println(response.getParams());
		// System.out.println(response.getSubCode());
		// System.out.println(response.getCode());
		// System.out.println(response.getSellerId());
		// System.out.println(response.getOutTradeNo());
		// System.out.println(response.getMsg());
		// System.out.println(response.getSubMsg());
		// System.out.println(response.getParams());

	}

}
