package net.aircommunity.platform.service.internal.payment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micro.common.DateFormats;
import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.Payment.Method;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Refund;
import net.aircommunity.platform.model.RefundResponse;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.RefundRepository;
import net.aircommunity.platform.service.CommonOrderService;
import net.aircommunity.platform.service.spi.PaymentGateway;

/**
 * Alipay PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
public class AlipayPaymentGateway implements PaymentGateway {
	private static final Logger LOG = LoggerFactory.getLogger(AlipayPaymentGateway.class);

	private static final String APP_ID = "2017060907455116";
	private static final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmD0tN9FnXpnbA7KbdxTKHOm1k+HKveD5skZQ/r2ZV43rQFHOtQQEY2sdvNgBeUKAnv15PDf6uFyD9VpqtDzhSR9uFDLF7Gz6BVNByEKno5on1FDT1loUHz0/yXMxfapYpk5SLgXowI0cmn2vIzhxeHF4zLK9oU39c4bpmtvIDA7NHS2LuQmuCoLVuwtsuMaqFuJ3pMG9oZywC8Z5EVd3ogjyyxDbKgY897jO0hhv71/SfZ9GOAgrFNBpGMZO3nUtrpk7H7Yx1E3EuzKryplf8EdUs/WgfmSfhRw6yIKMaW0hM9/GdG670SkwEwuvrpDdReiYROUDtbCihioi5y+2/AgMBAAECggEACvTPXyFUHCpbg3cZu2AbaVithw/tYS5pz/f69Ai6k8gifkAbMb8sN1uX9Pp3I8HmDzlNG6Isv4e/IXfpVKnAaY1cKncf7qNpiFb0OdJno3oyd/0RUXLQ7Cb9e1wsD8+UgMG/90Rfr3VkaGP0VJdkv8DXZkD4gcWgjZxHFCfV5+TnScLQqgWmByu5pJKPuQX4XnWajexM1nJQm2Go1OTC8zEChBc3whSZyYqnBAQU4rS7Pz83seTUDFmPAG7xWKnBLT/jy9TpwOyHpMsHFismLJXCZHF+aGq7Wyx+Xemv4r5a6UEBfHSY3eACl2mElYESyi6CQkoNH2PaGdtNSLOcQQKBgQDhpQ2wXl+q3w5G4UV6eSBX4frG3bSxiI5HM+xMsE+kQhu99wf3Ri8P8MhUBmtKnKl4j4KgEZt0kmxzlFlT8dQ7Tj7bO5x4auCZi2YmKq/37x0iFrUQiXfyego1FZxUeNTcKWiHHi5db3OCc+fwI8YnzdOJ2KKRDXdi6zhw7kuhWQKBgQC8ZjObionxX/mi3y4GvbrNSSkO9jzSUJ9nMeBcy1hpBC695gXoSoR5QQtRMdQxWtK4c6+8MgJMPdCwSUsAF4pvI0LrZPaFIUWEb9pUZkLY1+mhMf87xAFAa78CM5p2J25n5jq1t6PTn0+JbCRlsjgIsM5emecAx/rXX1NT8klM1wKBgC8OP5uPIr48g/quEdInnmIVYznDlGINizY4EsgvYHxtuOFVudiMT1YwrWYwbIGDyCe3LdN5uISH4Iv93N8PqGWxvJP1i3zlNO9wTZ4Z+tZmjBnGyH2pXVU4tBY76n0HMcSz8fNzjNG5Y0pKJ41BuJomZz3w6n37Y/FCAmQynZ0JAoGBALPkms6ggIr8a6/7j0VckSxH+W6R7Q2dcjflRikU+bx9A+zL4UQnM0tcsmO7QrRF1wPNYzY+QjdupwBNW9IgqEzqzJFcfJAubuTAsSb55kaMFEeZJ+93fwJ2X5LIl2rOx/tpuRGe4k3FxvqfSjnY7OxPdx6Zshvq2Dgii7ySky9NAoGBAMtDw4cQ+RLZxf9dAoJO6QmdmJKFgI+UZP/oLsNtqa3eqQU7W2/jS2pVm5bC3uebjCs+keKad9ESQ3ddiULThe1UfHelbpLSpSmTTxwcx29qtAgrQ+jlIoJmKfSMGR0Y6ahQaCCjXKsLs24TCOlbqJtT0Sk5aRiMffkX+A2X9nSb";
	private static final String ALIPAY_GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
	private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAooqAPK0uB0LBA9RjaTNgD1NK5h/ZYbuVteNhVMyeUcJQUXrOWB8Fbpv/uhJoqjtZlXud4svJ8jJ9xx2H2LJqW4eGnAekyI848T/C99X6z67R0QBzyZHjFejUxvv6GbDyF80eMn4MJFslaS51iBhZQC3ilCzgBQ4jjBRwCMP3tPVgoMpYV0vo6QJ9pTFojs84fe23lNcd0fA5rfRWJsai4F7oBD0odG77SVPbohuPefj3BHYppItCSFdwmjLXdGFsi2X+KIvkDQCsHcwhbjJvp9A8SWU0vUAcpakE5dvp2zoN5mYQJDR/CePiEEe7i32CtwJ0Uni5eqIKsSmmMbOIEQIDAQAB";
	private static final String ALIPAY_SIGN_TYPE = "RSA2";
	private static final String ALIPAY_PAY_FORMAT = "json";
	private static final String ALIPAY_PAY_CHARSET = "utf-8";
	private static final String ALIPAY_PRODUCT_CODE = "QUICK_MSECURITY_PAY";

	// TRADE_FINISHED: 交易结束，不可退款, TRADE_SUCCESS: 交易支付成功
	private static final String TRADE_FINISHED = "TRADE_FINISHED";
	private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
	private static final PaymentResponse PAYMENT_RESPONSE_FAILURE = new PaymentResponse("failure");
	private static final PaymentResponse PAYMENT_RESPONSE_SUCCESS = new PaymentResponse("success");
	private static final SimpleDateFormat PAYMENT_TIMESTAMP_FORMATTER = DateFormats.simple("yyyy-MM-dd HH:mm:ss");
	private static final String PAYMENT_RESULT_TRADE_APP_PAY_RESPONSE = "alipay_trade_app_pay_response";
	private static final String PAYMENT_RESULT_STATUS = "resultStatus";
	private static final String PAYMENT_RESULT_OUT_TRADE_NO = "out_trade_no";
	private static final String PAYMENT_RESULT_TRADE_NO = "trade_no";
	private static final String PAYMENT_RESULT_TRADE_STATUS = "trade_status";
	private static final String PAYMENT_RESULT_APP_ID = "app_id";
	private static final String PAYMENT_RESULT_GMT_PAYMENT = "gmt_payment";
	private static final String PAYMENT_RESULT_TOTAL_AMOUNT = "total_amount";
	private static final String PAYMENT_STATUS_SUCCESS = "9000"; // code: 9000, 订单支付成功

	private final AlipayClient alipayClient;

	@Resource
	private Configuration configuration;

	@Resource
	private CommonOrderService commonOrderService;

	@Resource
	private RefundRepository refundRepository;

	@Resource
	private ObjectMapper objectMapper;

	public AlipayPaymentGateway() {
		alipayClient = new DefaultAlipayClient(ALIPAY_GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, ALIPAY_PAY_FORMAT,
				ALIPAY_PAY_CHARSET, ALIPAY_PUBLIC_KEY, ALIPAY_SIGN_TYPE);
	}

	@Override
	public Method getPaymentMethod() {
		return Method.ALIPAY;
	}

	@Override
	public PaymentRequest createPaymentRequest(Order order) {
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		// SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		Product product = order.getProduct();
		String body = Strings.isBlank(product.getDescription()) ? product.getName() : product.getDescription();
		// 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body, e.g. iPhone6S 16G
		model.setBody(body);
		// 商品的标题/交易标题/订单标题/订单关键字等
		model.setSubject(product.getName());
		// 商户网站唯一订单号
		model.setOutTradeNo(order.getOrderNo());
		// 设置未付款支付宝交易的超时时间，一旦超时，该笔交易就会自动被关闭。
		// 当用户进入支付宝收银台页面（不包括登录页面），会触发即刻创建支付宝交易，此时开始计时。
		// 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
		model.setTimeoutExpress("2h");
		// 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
		double totalAmount = OrderPrices.normalizePrice(order.getTotalPrice()).doubleValue();
		model.setTotalAmount(String.valueOf(totalAmount));
		model.setProductCode(ALIPAY_PRODUCT_CODE);
		request.setBizModel(model);
		request.setNotifyUrl(configuration.getAlipayNotifyUrl());
		LOG.debug("Alipay notify url: {}", configuration.getAlipayNotifyUrl());
		try {
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			// 就是orderString 可以直接给客户端请求，无需再做处理
			String orderString = response.getBody();
			LOG.debug("Alipay orderString: {}", orderString);
			return new PaymentRequest(orderString);
		}
		catch (AlipayApiException e) {
			LOG.error(String.format("Failed to create payment info, errcode: %s, errmsg: %s, cause: %s", e.getErrCode(),
					e.getErrMsg(), e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	/**
	 * The Alipay result example:
	 * 
	 * <pre>
	 * All:
	 * {
	 * 	"memo": "",
	 * 	"result": "{\"alipay_trade_app_pay_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"app_id\":\"2017060907455116\",\"auth_app_id\":\"2017060907455116\",\"charset\":\"utf-8\",\"timestamp\":\"2017-06-10 12:34:20\",\"total_amount\":\"0.01\",\"trade_no\":\"2017061021001004440265148445\",\"seller_id\":\"2088521250420360\",\"out_trade_no\":\"V0900OLFV7E7O1F\"},\"sign\":\"lrzO3JyTDA8WFdz//LyiS6OKxuucjgKJFBh+uc0XILfZ5t66Flt8GrE9gb6rrcSyxKFmNAeWRiLsBy51omWP6YiLcD5VRV5PTTwzTQQvg1T0qu13UmBYRragbXaCl0eACYC+DP1275blcFtnW+cyShcomJWwI8wUDchXTm/rvmV/q61/U2GJyYIii+nNvqeYJd00D+MC9zsD7evO/fo4G6hytzUkyNL+0/z5S4x8H2EdF0Q8Qb9NWycsGsbBbwEAEQL2QxvgQxujBI3lgEPcCtMuorHy5yL2afBm7U6lAS7hBYvbo80r3SmvlMJjUi3Yq4s6iQ2psZBKCdN2QU5zsA==\",\"sign_type\":\"RSA2\"}",
	 * 	"resultStatus": "9000"
	 * }
	 * 
	 * Result:
	 * {
	 *	"alipay_trade_app_pay_response": {
	 *	      "code": "10000",
	 *	      "msg": "Success",
	 *	      "app_id": "2017060907455116",
	 *	      "auth_app_id": "2017060907455116",
	 *	      "charset": "utf-8",
	 *	      "timestamp": "2017-06-10 12:34:20",
	 *	      "total_amount": "0.01",
	 *	      "trade_no": "2017061021001004440265148445",
	 *	      "seller_id": "2088521250420360",
	 *	      "out_trade_no": "V0900OLFV7E7O1F"
	 *		},
	 *	"sign": "lrzO3JyTDA8WFdz//LyiS6OKxuucjgKJFBh+uc0XILfZ5t66Flt8GrE9gb6rrcSyxKFmNAeWRiLsBy51omWP6YiLcD5VRV5PTTwzTQQvg1T0qu13UmBYRragbXaCl0eACYC+DP1275blcFtnW+cyShcomJWwI8wUDchXTm/rvmV/q61/U2GJyYIii+nNvqeYJd00D+MC9zsD7evO/fo4G6hytzUkyNL+0/z5S4x8H2EdF0Q8Qb9NWycsGsbBbwEAEQL2QxvgQxujBI3lgEPcCtMuorHy5yL2afBm7U6lAS7hBYvbo80r3SmvlMJjUi3Yq4s6iQ2psZBKCdN2QU5zsA==",
	 *	"sign_type": "RSA2"
	 *	}
	 * 
	 * </pre>
	 * 
	 * @param result
	 * @return verification result
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PaymentVerification verifyClientPaymentNotification(PaymentNotification notification) {
		Map<String, String> result = (Map<String, String>) notification.getData();
		LOG.debug("{} client notification: {}", getPaymentMethod(), result);
		String resultStatus = result.get(PAYMENT_RESULT_STATUS);
		if (PAYMENT_STATUS_SUCCESS.equals(resultStatus)) {
			try {
				// 1) parse result (it also will verified sign)
				AlipayTradeAppPayResponse response = alipayClient.parseAppSyncResult(result,
						AlipayTradeAppPayRequest.class);
				Map<String, Object> resultBody = objectMapper.readValue(response.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				Map<String, Object> tradePayResponse = (Map<String, Object>) resultBody
						.get(PAYMENT_RESULT_TRADE_APP_PAY_RESPONSE);

				// 2) check APP_ID
				String appId = (String) tradePayResponse.get(PAYMENT_RESULT_APP_ID);
				if (APP_ID.equals(appId)) {
					String orderNo = response.getOutTradeNo();
					BigDecimal totalAmount = new BigDecimal(response.getTotalAmount());
					// 3) check orderNo existence
					Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
					if (orderRef.isPresent()) {
						Order order = orderRef.get();
						// 4) check total_amount matches the total price of the order and check order status?
						if (OrderPrices.priceMatches(totalAmount, order.getTotalPrice()) && order.isProbablyPaid()) {
							// it may still FAILURE because of async notification from payment system, just a guess
							return PaymentVerification.SUCCESS;
						}
					}
				}
			}
			catch (AlipayApiException e) {
				LOG.error(String.format("Payment result verification failure, errcode: %s, errmsg: %s, cause: %s",
						e.getErrCode(), e.getErrMsg(), e.getMessage()), e);
			}
			catch (Exception e) {
				LOG.error(String.format("Payment result verification failure, cause:" + e.getMessage()), e);
			}
		}
		return PaymentVerification.FAILURE;
	}

	@Transactional
	@Override
	@SuppressWarnings("unchecked")
	public PaymentResponse processServerPaymentNotification(PaymentNotification notification) {
		try {
			Map<String, String> params = (Map<String, String>) notification.getData();
			String orderNo = params.get(PAYMENT_RESULT_OUT_TRADE_NO); // 商户订单号
			String tradeNo = params.get(PAYMENT_RESULT_TRADE_NO); // 流水号
			String tradeStatus = params.get(PAYMENT_RESULT_TRADE_STATUS); // 交易状态
			String appId = params.get(PAYMENT_RESULT_APP_ID); // 支付宝分配给开发者的应用Id
			Date paymentTimestamp = PAYMENT_TIMESTAMP_FORMATTER.parse(params.get(PAYMENT_RESULT_GMT_PAYMENT)); // 交易付款时间
			BigDecimal totalAmount = new BigDecimal(params.get(PAYMENT_RESULT_TOTAL_AMOUNT)); // 订单金额

			// 1) check trade status
			if (!(tradeStatus.equals(TRADE_FINISHED) || tradeStatus.equals(TRADE_SUCCESS))) {
				LOG.error("Payment trade status {} is not success, payment failed", tradeStatus);
				return PAYMENT_RESPONSE_FAILURE;
			}

			// 2) verify sign
			boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, ALIPAY_PAY_CHARSET,
					ALIPAY_SIGN_TYPE);
			// boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, ALIPAY_PAY_CHARSET);
			if (!signVerified) {
				LOG.error("Signature verification failure, payment failed");
				return PAYMENT_RESPONSE_FAILURE;
			}

			// 3) check APP_ID
			if (!APP_ID.equals(appId)) {
				LOG.error("APP ID mismatch expected: {}, but was: {}, payment failed", APP_ID, appId);
				return PAYMENT_RESPONSE_FAILURE;
			}

			// 3) check orderNo existence
			Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
			if (!orderRef.isPresent()) {
				LOG.error("OrderNo: {} not exists, payment failed", orderNo);
				return PAYMENT_RESPONSE_FAILURE;
			}

			// 4) check total amount
			Order order = orderRef.get();
			if (!OrderPrices.priceMatches(totalAmount, order.getTotalPrice())) {
				LOG.error("Order total amount mismatch, expected: {}, but was: {},  payment failed",
						order.getTotalPrice(), totalAmount);
				return PAYMENT_RESPONSE_FAILURE;
			}

			// 5) check order status
			if (!order.isPayable()) {
				LOG.error("Order status is {}, and it is NOT payable,  payment failed", order.getStatus());
				return PAYMENT_RESPONSE_FAILURE;
			}

			// 6) check if same tradeNo to avoid pay multiple time
			Optional<Payment> paymentRef = commonOrderService.findPaymentByTradeNo(Payment.Method.ALIPAY, tradeNo);
			if (paymentRef.isPresent()) {
				LOG.warn("OrderNo: {} is already paid with {} with tradeNo: {}", orderNo, Payment.Method.ALIPAY,
						tradeNo);
				return PAYMENT_RESPONSE_SUCCESS;
			}

			// 7) handle payment
			Payment payment = new Payment();
			payment.setAmount(totalAmount);
			payment.setMethod(Payment.Method.ALIPAY);
			payment.setOrderNo(orderNo);
			payment.setTradeNo(tradeNo);
			payment.setTimestamp(paymentTimestamp);
			order = commonOrderService.payOrder(order.getId(), payment);
			LOG.debug("Payment success, updated order: {}", order);
			return PAYMENT_RESPONSE_SUCCESS;
		}
		catch (AlipayApiException e) {
			LOG.error(String.format("Process payment result failure, errcode: %s, errmsg: %s, cause: %s",
					e.getErrCode(), e.getErrMsg(), e.getMessage()), e);
		}
		catch (Exception e) {
			LOG.error(String.format("Process payment result failure, cause: %s", e.getMessage()), e);
		}
		return PAYMENT_RESPONSE_FAILURE;
	}

	@Override
	public RefundResponse refundPayment(Order order) {
		AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		// 商户订单号和支付宝交易号不能同时为空 (二选一), trade_no、 out_trade_no如果同时存在优先取trade_no
		model.setOutTradeNo(order.getOrderNo());
		// model.setTradeNo(tradeNo);
		// model.setOutRequestNo(out_request_no);
		BigDecimal refundAmount = OrderPrices.normalizePrice(order.getTotalPrice());
		model.setRefundAmount(String.valueOf(refundAmount.doubleValue()));
		model.setRefundReason(order.getRefundReason());
		alipayRequest.setBizModel(model);
		try {
			AlipayTradeRefundResponse refundResponse = alipayClient.execute(alipayRequest);
			if (refundResponse.isSuccess()) {
				Refund refund = new Refund();
				refund.setAmount(refundAmount);
				refund.setMethod(Payment.Method.ALIPAY);
				refund.setOrderNo(refundResponse.getOutTradeNo());
				refund.setTradeNo(refundResponse.getTradeNo());
				refund.setTimestamp(refundResponse.getGmtRefundPay());
				refund.setRefundReason(order.getRefundReason());
				refund.setRefundResult(M.msg(M.REFUND_SUCCESS));
				return RefundResponse.success(refund);
			}
			return RefundResponse.failure(M.msg(M.REFUND_FAILURE, refundResponse.getCode(), refundResponse.getMsg()));
		}
		catch (Exception e) {
			LOG.error(String.format("Refund failure for order: %s, cause: %s", order, e.getMessage()), e);
			String refundFailureCause = e.getLocalizedMessage();
			if (AlipayApiException.class.isAssignableFrom(e.getClass())) {
				AlipayApiException ex = AlipayApiException.class.cast(e);
				refundFailureCause = M.msg(M.REFUND_FAILURE, ex.getErrCode(), ex.getErrMsg());
			}
			return RefundResponse.failure(refundFailureCause);
		}
	}

	// WORKS
	// private void testQuery() {
	// AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
	// AlipayTradeQueryModel model = new AlipayTradeQueryModel();
	// // model.setOutTradeNo(out_trade_no);
	// model.setTradeNo("2017061321001004040275979115");
	// alipayRequest.setBizModel(model);
	// try {
	// AlipayTradeQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
	// System.out.println(alipayResponse.getBody());
	// }
	// catch (AlipayApiException e) {
	// e.printStackTrace();
	// }
	// }

	// WORKS
	// private void testQueryRefund() {
	// AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
	// AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
	// // 商户订单号和支付宝交易号不能同时为空。 trade_no、 out_trade_no如果同时存在优先取trade_no
	// // 商户订单号，和支付宝交易号二选一
	// // model.setOutTradeNo("170612O8H9NFUC4400");
	// model.setTradeNo("2017061321001004040275979115");
	// // 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
	// model.setOutRequestNo("170612O8H9NFUC4400"); // OrderNo.
	// alipayRequest.setBizModel(model);
	// try {
	// AlipayTradeFastpayRefundQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
	// System.out.println(alipayResponse.getBody());
	// }
	// catch (AlipayApiException e) {
	// e.printStackTrace();
	// }
	// }

	// public static void main(String arg[]) throws AlipayApiException {
	// AlipayPaymentGateway g = new AlipayPaymentGateway();
	// RefundRequest request = new RefundRequest();
	// request.setRefundReason("测试退款");
	// // g.refund(request);
	// g.testQueryRefund();
	// g.testQuery();
	// //
	// {"alipay_trade_refund_response":{"code":"10000","msg":"Success","buyer_logon_id":"gem***@gmail.com","buyer_user_id":"2088502878657042","fund_change":"Y","gmt_refund_pay":"2017-06-13
	// //
	// 00:54:07","open_id":"20881009806334552586332510413604","out_trade_no":"170612O8H9NFUC4400","refund_fee":"0.01","send_back_fee":"0.00","trade_no":"2017061321001004040275979115"},"sign":"DVo5iJv6rHhIQwlH83NZnymTevEWxckYtndYS61U7OgvBZKiRBhtsWwKPx1nZSkAApXeH45k/kKQ8GrJ+th0YWN4KZrhNGHAksy2D25ezpyPt4FpufS5FRMsoDkp6t3Y4fFhhjFCkGclHoF8BBtsPbPJRDUDjEw+fpxBS48xfOg8GyrVVDR7pdBLPfOiTOOEQUB3mYVfLgk7TGb7KRVumyNozf21rDWV61IFY4nWLT8cCmSurKPqHzp+VjioI/zpFozKaAqtZI0KaVkM6FYKHXOgjJLO2LhhrTzJZXONxlj3m8IQt54vbFTLhW9YH7Lci9+4AgUY2OlIQcEbozBWsg=="}
	// }
}
