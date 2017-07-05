package net.aircommunity.platform.service.internal.payment.alipay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.micro.common.DateFormats;
import io.micro.common.Strings;
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

/**
 * Alipay PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
public class AlipayPaymentGateway extends AbstractPaymentGateway {
	// private static final String APP_ID = "2017060907455116";
	// private static final String APP_PRIVATE_KEY =
	// "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmD0tN9FnXpnbA7KbdxTKHOm1k+HKveD5skZQ/r2ZV43rQFHOtQQEY2sdvNgBeUKAnv15PDf6uFyD9VpqtDzhSR9uFDLF7Gz6BVNByEKno5on1FDT1loUHz0/yXMxfapYpk5SLgXowI0cmn2vIzhxeHF4zLK9oU39c4bpmtvIDA7NHS2LuQmuCoLVuwtsuMaqFuJ3pMG9oZywC8Z5EVd3ogjyyxDbKgY897jO0hhv71/SfZ9GOAgrFNBpGMZO3nUtrpk7H7Yx1E3EuzKryplf8EdUs/WgfmSfhRw6yIKMaW0hM9/GdG670SkwEwuvrpDdReiYROUDtbCihioi5y+2/AgMBAAECggEACvTPXyFUHCpbg3cZu2AbaVithw/tYS5pz/f69Ai6k8gifkAbMb8sN1uX9Pp3I8HmDzlNG6Isv4e/IXfpVKnAaY1cKncf7qNpiFb0OdJno3oyd/0RUXLQ7Cb9e1wsD8+UgMG/90Rfr3VkaGP0VJdkv8DXZkD4gcWgjZxHFCfV5+TnScLQqgWmByu5pJKPuQX4XnWajexM1nJQm2Go1OTC8zEChBc3whSZyYqnBAQU4rS7Pz83seTUDFmPAG7xWKnBLT/jy9TpwOyHpMsHFismLJXCZHF+aGq7Wyx+Xemv4r5a6UEBfHSY3eACl2mElYESyi6CQkoNH2PaGdtNSLOcQQKBgQDhpQ2wXl+q3w5G4UV6eSBX4frG3bSxiI5HM+xMsE+kQhu99wf3Ri8P8MhUBmtKnKl4j4KgEZt0kmxzlFlT8dQ7Tj7bO5x4auCZi2YmKq/37x0iFrUQiXfyego1FZxUeNTcKWiHHi5db3OCc+fwI8YnzdOJ2KKRDXdi6zhw7kuhWQKBgQC8ZjObionxX/mi3y4GvbrNSSkO9jzSUJ9nMeBcy1hpBC695gXoSoR5QQtRMdQxWtK4c6+8MgJMPdCwSUsAF4pvI0LrZPaFIUWEb9pUZkLY1+mhMf87xAFAa78CM5p2J25n5jq1t6PTn0+JbCRlsjgIsM5emecAx/rXX1NT8klM1wKBgC8OP5uPIr48g/quEdInnmIVYznDlGINizY4EsgvYHxtuOFVudiMT1YwrWYwbIGDyCe3LdN5uISH4Iv93N8PqGWxvJP1i3zlNO9wTZ4Z+tZmjBnGyH2pXVU4tBY76n0HMcSz8fNzjNG5Y0pKJ41BuJomZz3w6n37Y/FCAmQynZ0JAoGBALPkms6ggIr8a6/7j0VckSxH+W6R7Q2dcjflRikU+bx9A+zL4UQnM0tcsmO7QrRF1wPNYzY+QjdupwBNW9IgqEzqzJFcfJAubuTAsSb55kaMFEeZJ+93fwJ2X5LIl2rOx/tpuRGe4k3FxvqfSjnY7OxPdx6Zshvq2Dgii7ySky9NAoGBAMtDw4cQ+RLZxf9dAoJO6QmdmJKFgI+UZP/oLsNtqa3eqQU7W2/jS2pVm5bC3uebjCs+keKad9ESQ3ddiULThe1UfHelbpLSpSmTTxwcx29qtAgrQ+jlIoJmKfSMGR0Y6ahQaCCjXKsLs24TCOlbqJtT0Sk5aRiMffkX+A2X9nSb";
	// private static final String ALIPAY_PUBLIC_KEY =
	// "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAooqAPK0uB0LBA9RjaTNgD1NK5h/ZYbuVteNhVMyeUcJQUXrOWB8Fbpv/uhJoqjtZlXud4svJ8jJ9xx2H2LJqW4eGnAekyI848T/C99X6z67R0QBzyZHjFejUxvv6GbDyF80eMn4MJFslaS51iBhZQC3ilCzgBQ4jjBRwCMP3tPVgoMpYV0vo6QJ9pTFojs84fe23lNcd0fA5rfRWJsai4F7oBD0odG77SVPbohuPefj3BHYppItCSFdwmjLXdGFsi2X+KIvkDQCsHcwhbjJvp9A8SWU0vUAcpakE5dvp2zoN5mYQJDR/CePiEEe7i32CtwJ0Uni5eqIKsSmmMbOIEQIDAQAB";
	private static final String ALIPAY_GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
	private static final String ALIPAY_SIGN_TYPE = "RSA2";
	private static final String ALIPAY_PAY_FORMAT = "json";
	private static final String ALIPAY_PAY_CHARSET = "utf-8";
	private static final String ALIPAY_PRODUCT_CODE = "QUICK_MSECURITY_PAY";

	// TRADE_FINISHED: 交易结束，不可退款, TRADE_SUCCESS: 交易支付成功
	private static final String TRADE_FINISHED = "TRADE_FINISHED";
	private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
	// TRADE_CLOSED: 未付款交易超时关闭，或支付完成后全额退款
	private static final String TRADE_CLOSED = "TRADE_CLOSED";
	private static final SimpleDateFormat PAYMENT_TIMESTAMP_FORMATTER = DateFormats.simple("yyyy-MM-dd HH:mm:ss");
	private static final String NOTIFICATION_RESULT_TRADE_APP_PAY_RESPONSE = "alipay_trade_app_pay_response";
	private static final String NOTIFICATION_RESULT_STATUS = "resultStatus";
	private static final String NOTIFICATION_RESULT_OUT_TRADE_NO = "out_trade_no";
	private static final String NOTIFICATION_RESULT_TRADE_NO = "trade_no";
	private static final String NOTIFICATION_RESULT_TRADE_STATUS = "trade_status";
	private static final String NOTIFICATION_RESULT_APP_ID = "app_id";
	private static final String NOTIFICATION_RESULT_GMT_PAYMENT = "gmt_payment";
	private static final String NOTIFICATION_RESULT_TOTAL_AMOUNT = "total_amount";
	// private static final String NOTIFICATION_RESULT_GMT_REFUND = "gmt_refund";
	// private static final String NOTIFICATION_RESULT_REFUND_FEE = "refund_fee";
	private static final String PAYMENT_STATUS_SUCCESS = "9000"; // code: 9000, 订单支付成功

	private final AlipayClient alipayClient;

	private AlipayConfig config;

	@Autowired
	public AlipayPaymentGateway(AlipayConfig alipayConfig) {
		config = alipayConfig;
		alipayClient = new DefaultAlipayClient(ALIPAY_GATEWAY_URL, config.getAppId(), config.getAppPrivateKey(),
				ALIPAY_PAY_FORMAT, ALIPAY_PAY_CHARSET, config.getPublicKey(), ALIPAY_SIGN_TYPE);
	}

	@PostConstruct
	private void init() {
		LOG.debug("Alipay config original {}", config);
		if (Strings.isBlank(config.getNotifyUrl())) {
			config.setNotifyUrl(getServerNotifyUrl());
		}
		if (Strings.isBlank(config.getReturnUrl())) {
			config.setReturnUrl(getClientReturnUrl());
		}
		LOG.debug("Alipay config final: {}", config);
	}

	@Override
	public Method getPaymentMethod() {
		return Method.ALIPAY;
	}

	@Override
	public PaymentRequest createPaymentRequest(Order order) {
		LOG.debug("Create payment request for order: {}", order);
		LOG.info("Order No: {}", order.getOrderNo());
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		// SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		Product product = order.getProduct();
		// String body = Strings.isBlank(product.getDescription()) ? product.getName() : product.getDescription();
		// 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body, e.g. iPhone6S 16G
		model.setBody(M.msg(M.PAYMENT_PRODUCT_DESCRIPTION, product.getName()));
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
		request.setNotifyUrl(config.getNotifyUrl());
		request.setReturnUrl(config.getReturnUrl());
		LOG.debug("Notify url: {}", request.getNotifyUrl());
		LOG.debug("Return url: {}", request.getReturnUrl());
		try {
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			// 就是orderString 可以直接给客户端请求，无需再做处理
			String orderString = response.getBody();
			LOG.info("Order info: {}", orderString);
			return new PaymentRequest(orderString);
		}
		catch (Exception e) {
			if (AlipayApiException.class.isAssignableFrom(e.getClass())) {
				AlipayApiException ex = AlipayApiException.class.cast(e);
				LOG.error(String.format("Failed to create payment info, errcode: %s, errmsg: %s, cause: %s",
						ex.getErrCode(), ex.getErrMsg(), ex.getMessage()), ex);
			}
			else {
				LOG.error(String.format("Failed to create payment info, cause: %s", e.getMessage()), e);
			}
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
		LOG.info("client notification: {}", result);
		String resultStatus = result.get(NOTIFICATION_RESULT_STATUS);
		if (PAYMENT_STATUS_SUCCESS.equals(resultStatus)) {
			try {
				// 1) parse result (it also will verified sign)
				AlipayTradeAppPayResponse response = alipayClient.parseAppSyncResult(result,
						AlipayTradeAppPayRequest.class);
				Map<String, Object> resultBody = objectMapper.readValue(response.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				Map<String, Object> tradePayResponse = (Map<String, Object>) resultBody
						.get(NOTIFICATION_RESULT_TRADE_APP_PAY_RESPONSE);

				// 2) check APP_ID
				String appId = (String) tradePayResponse.get(NOTIFICATION_RESULT_APP_ID);
				if (config.getAppId().equals(appId)) {
					String orderNo = response.getOutTradeNo();
					BigDecimal totalAmount = new BigDecimal(response.getTotalAmount());
					// 3) check orderNo existence
					Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
					if (orderRef.isPresent()) {
						Order order = orderRef.get();
						// 4) check total_amount matches the total price of the order and check order status?
						if (OrderPrices.priceMatches(totalAmount, order.getTotalPrice()) && order.isProbablyPaid()) {
							// it may still FAILURE because of async notification from payment system, just a guess
							commonOrderService.updateOrderStatus(order, Order.Status.PAYMENT_IN_PROCESS);
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

	// 程序执行完后必须打印输出“success”（不包含引号）。如果商户反馈给支付宝的字符不是success这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。
	// 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）；
	@Override
	@SuppressWarnings("unchecked")
	public PaymentResponse processServerPaymentNotification(PaymentNotification notification) {
		try {
			Map<String, String> params = (Map<String, String>) notification.getData();
			LOG.info("server notificaion: {}", params);
			// 1) verify sign
			boolean signVerified = AlipaySignature.rsaCheckV1(params, config.getPublicKey(), ALIPAY_PAY_CHARSET,
					ALIPAY_SIGN_TYPE);
			if (!signVerified) {
				LOG.error("Signature verification failure, payment failed");
				return PaymentResponse.failure(PaymentResponse.Status.INVALID_SIGNATURE);
			}

			String orderNo = params.get(NOTIFICATION_RESULT_OUT_TRADE_NO); // 商户订单号
			String tradeNo = params.get(NOTIFICATION_RESULT_TRADE_NO); // 流水号
			String tradeStatus = params.get(NOTIFICATION_RESULT_TRADE_STATUS); // 交易状态
			String appId = params.get(NOTIFICATION_RESULT_APP_ID); // 支付宝分配给开发者的应用Id
			BigDecimal totalAmount = new BigDecimal(params.get(NOTIFICATION_RESULT_TOTAL_AMOUNT)); // 订单金额

			// 2) check APP_ID
			if (!config.getAppId().equals(appId)) {
				LOG.error("AppId mismatch expected: {}, but was: {}, payment failed", config.getAppId(), appId);
				return PaymentResponse.failure(PaymentResponse.Status.APP_ID_MISMATCH);
			}

			// 3) check trade status
			switch (tradeStatus) {
			// Available status for TRADE REFUND action:
			// NOTE: refund success or payment timeout and closed, we do nothing with our order (because refund is SYNC
			// operation)
			// TRADE_CLOSED: 在指定时间段内未支付时关闭的交易； 在交易完成全额退款成功时关闭的交易。
			case TRADE_CLOSED:
				// 交易退款时间
				// Date refundTimestamp = PAYMENT_TIMESTAMP_FORMATTER.parse(params.get(NOTIFICATION_RESULT_GMT_REFUND));
				// 总退款金额
				// String refundFee = params.get(NOTIFICATION_RESULT_REFUND_FEE);
				if (LOG.isDebugEnabled()) {
					Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
					if (orderRef.isPresent()) {
						Order order = orderRef.get();
						LOG.debug("[{}]: {}", tradeStatus, order);
					}
				}
				// just success anyway because AlipaySignature.rsaCheckV1 is already done (no order check ATM)
				return PaymentResponse.success();

			// Available status for TRADE PAY action:
			// 1) TRADE_FINISHED 交易成功
			// 2) TRADE_SUCCESS 支付成功
			// 3) WAIT_BUYER_PAY 交易创建
			// payment success
			case TRADE_FINISHED:
			case TRADE_SUCCESS:
				// 交易付款时间
				Date paymentDate = PAYMENT_TIMESTAMP_FORMATTER.parse(params.get(NOTIFICATION_RESULT_GMT_PAYMENT));
				PaymentResponse response = doProcessPaymentSuccess(totalAmount, orderNo, tradeNo, paymentDate);
				if (!response.isSuccessful()) {
					tryAutoRefundOnFailure(tradeNo, orderNo, totalAmount, response.getBody());
				}
				return response;

			default:
				// no payment failure notification
				LOG.error("Trade status {} is not processed, just considered as failed", tradeStatus);
				Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
				if (orderRef.isPresent()) {
					commonOrderService.handleOrderPaymentFailure(orderRef.get(),
							M.msg(M.PAYMENT_SERVER_NOTIFY_TRADE_FAILURE, tradeStatus));
				}
				return PaymentResponse.failure(PaymentResponse.Status.UNSUCCESS_TRADE_STATUS,
						M.msg(M.PAYMENT_SERVER_NOTIFY_TRADE_FAILURE, tradeStatus));
			}
		}
		catch (AlipayApiException e) {
			LOG.error(String.format("Process trade notification result failure, errcode: %s, errmsg: %s, cause: %s",
					e.getErrCode(), e.getErrMsg(), e.getMessage()), e);
		}
		catch (Exception e) {
			LOG.error(String.format("Process trade notification result failure, cause: %s", e.getMessage()), e);
		}
		return PaymentResponse.failure(PaymentResponse.Status.UNKNOWN);
	}

	/**
	 * Try refund on handle notification failure, and just ignored if refund failed
	 */
	private void tryAutoRefundOnFailure(String tradeNo, String orderNo, BigDecimal refundAmount, String reason) {
		AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		model.setTradeNo(tradeNo);
		model.setRefundAmount(String.valueOf(refundAmount));
		model.setRefundReason(M.msg(M.REFUND_ON_PAYMENT_NOTIFY_FAILURE, tradeNo, orderNo, reason));
		alipayRequest.setBizModel(model);
		if (LOG.isInfoEnabled()) {
			LOG.info("Final auto refund on failure: {}", toJson(alipayRequest));
		}
		try {
			AlipayTradeRefundResponse refundResponse = alipayClient.execute(alipayRequest);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund response: {}", toJson(refundResponse));
			}
			if (refundResponse.isSuccess()) {
				LOG.info("Final auto refund for tradeNo: {} success", tradeNo);
				return;
			}
			LOG.error(M.msg(M.REFUND_FAILURE, refundResponse.getCode(), refundResponse.getMsg()));
		}
		catch (Exception e) {
			if (AlipayApiException.class.isAssignableFrom(e.getClass())) {
				AlipayApiException ex = AlipayApiException.class.cast(e);
				LOG.error(String.format("Auto refund failure for tradeNo: %s, cause: %s", tradeNo,
						M.msg(M.REFUND_FAILURE, ex.getErrCode(), ex.getErrMsg())), e);
			}
			else {
				LOG.error(String.format("Auto refund failure for tradeNo: %s, cause: %s", tradeNo, e.getMessage()), e);
			}
		}
	}

	@Override
	public RefundResponse refundPayment(Order order, BigDecimal refundAmount) {
		LOG.info("Refund {}, refundAmount: {}", order, refundAmount);
		AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		// 商户订单号和支付宝交易号不能同时为空 (二选一), trade_no、 out_trade_no如果同时存在优先取trade_no
		model.setOutTradeNo(order.getOrderNo());
		// model.setTradeNo(tradeNo);
		// model.setOutRequestNo(out_request_no);
		BigDecimal originalRefundAmount = OrderPrices.normalizePrice(order.getTotalPrice());
		refundAmount = OrderPrices.normalizePrice(refundAmount);
		// if refundAmount=0, we will use full amount refund of the order
		BigDecimal actualRefundAmount = (BigDecimal.ZERO == refundAmount) ? originalRefundAmount : refundAmount;
		model.setRefundAmount(String.valueOf(actualRefundAmount.doubleValue()));
		model.setRefundReason(order.getRefundReason());
		alipayRequest.setBizModel(model);
		if (LOG.isInfoEnabled()) {
			LOG.info("Final refund request: {}", toJson(alipayRequest));
		}
		try {
			// REFUND_SUCCESS: 退款成功
			// 全额退款情况：trade_status= TRADE_CLOSED，而refund_status=REFUND_SUCCESS；
			// 非全额退款情况：trade_status= TRADE_SUCCESS，而refund_status=REFUND_SUCCESS
			// REFUND_CLOSED: 退款关闭
			AlipayTradeRefundResponse refundResponse = alipayClient.execute(alipayRequest);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund response: {}", toJson(refundResponse));
			}
			if (refundResponse.isSuccess()) {
				Refund refund = new Refund();
				refund.setAmount(actualRefundAmount);
				refund.setMethod(getPaymentMethod());
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

	private String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		}
		catch (JsonProcessingException ignore) {
		}
		return null;
	}

	private void testQuery(Order order) {
		AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.setOutTradeNo(order.getOrderNo());
		alipayRequest.setBizModel(model);
		try {
			AlipayTradeQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
			// 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
			if (alipayResponse.isSuccess()) {

			}
			System.out.println(alipayResponse.getBody());
		}
		catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	public static void main(String arg[]) throws Exception {
		AlipayConfig alipayConfig = new AlipayConfig();
		alipayConfig.setAppId("2017060907455116");
		alipayConfig.setAppPrivateKey(
				"MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQCzd1f1XztJ4AH6psb69aLgdecrikGiQzM4MFf9fyzgxE2AdTgMgSSTvzhHpiT1+Cr4ksMqsHMO2hrTaHLTuCKhb4HYq44VKib7NrF1XRq5h9PNOBAR6bV3QRbcFN3QZDR/GZMOqQOGMsarT87L2PXsEuCcTDJnYOqaWwGgWPTq1xV8O+xvBBYTHNZtRTpLpxCd1XMelXk9QDZyNtSPuekMOzKFxcJvPYFNSLck0alMdnSWqest8WDq1isae4bBzMQeL6k3UcDBJG0XSm4mMpTCePxt9ai4pOlhwxQ+OlMzGesa9lCSPPfXHCHT3XM+T09uuCCfN5Y193XcklmKwn4NAgMBAAECggEBAKafhGPFgjdpqoy8MXwpeYqrDPFI0P1FJRXjFJ7AZ7tbppVAv8QkVwByBl/HawOP4N2e7WMCJiFA8K0diZb2m/iEneq8BROzajPNKN/NjJV6/XPIvGrVMO1C2mWFXhgwIOlspjNzSllbZUcCtv5eNp1zPWT//cArpEWhfxYP4Xzymy+jO6fKm1c8Omn1mJyOEQeneh5OwoBYEV32rX/mpBVmKnpVP6HsVxsU/U05bB2eTUyKsKCtzPhhDZoRwbsxqI7V+MaYdz613muHh8OIZHtMS+SF5fw+O5ZWJAWsP2Cxta602BHqascADaTQ3tgbaPv+/I8YrDU++KnuLHvKkMUCgYEA64o9yUw4BZgBFhcN8/q5GHJjD3T7VvcX9E3oD/dEiejXd9RHFJqV47DEDSO+XrKI/Shk/U6WiRSmqksXhVw0ynf8NYDrjbBjPIXTIqNyIuKRkZw+DCoAb3QkVLPzTIUUd2D1+zPhNi12k6fyRYhlgait5ZlN6HKE9nvmcOm+GhcCgYEAww4s7NQNfKefiHR5fqjIc2BvcOMKUUPApJEQxqm/8hTRmCZG/MvZWpvZTwERb0AsDNGu16b+Dq+mqnYshD0zwkvW0WBsLKpac6W3P53UP5cwypPYj49x9dNo8a7no3j1c0DsEnpoAVI5dqyq4VYHSKZHhREKQjRw9ljdDmxN03sCgYEAh40jSbmHdBCqb2ANM5/S7fLGd5rHGqFRM9Ox/Z4733IUrm2ICp98K3ELItSzNiRhGfApTm3vzCwKTm6wtpr99pdemhv7c6tTMP2DKKgPg2wIglf8jVuOrJWWYvi8yAi+YoV2in6s2VUIrKk2kDWS1S+SBFRZtbBSPNfJIqoiMTkCgYEAksM0vFFlgHijScnRrKKUiHN0Bm1eUvz2kxxvkfshaKWPerq6SPWcqld/b7lvA9U2D8MpmiuVFznE3peiMTHXowbrMIkre4QGIOP8eIpprBs3ZAVQOdyFs6CJYufmdJLLpBeiSNj/LpdOk2OiA4B2ZIxwXcgPfvb0U/dTBMoq2McCgYEA1QeHkDYsF3szmY2w9AeGw/lgIQGHtL+5Ir32x4t8B/EsuWpNF1jFAzx/shoWFqw+oCcA6mDfTaUgankpYKYGnUXkP0aHZNCN1+V+MuSfBq+RFRCqiMFT0C9k/bn47nW/qg2q2DJGw0LAJJ0upqIsYs391UJwW0xzwLUXmT40RTA=");
		alipayConfig.setPublicKey(
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAooqAPK0uB0LBA9RjaTNgD1NK5h/ZYbuVteNhVMyeUcJQUXrOWB8Fbpv/uhJoqjtZlXud4svJ8jJ9xx2H2LJqW4eGnAekyI848T/C99X6z67R0QBzyZHjFejUxvv6GbDyF80eMn4MJFslaS51iBhZQC3ilCzgBQ4jjBRwCMP3tPVgoMpYV0vo6QJ9pTFojs84fe23lNcd0fA5rfRWJsai4F7oBD0odG77SVPbohuPefj3BHYppItCSFdwmjLXdGFsi2X+KIvkDQCsHcwhbjJvp9A8SWU0vUAcpakE5dvp2zoN5mYQJDR/CePiEEe7i32CtwJ0Uni5eqIKsSmmMbOIEQIDAQAB");
		AlipayPaymentGateway g = new AlipayPaymentGateway(alipayConfig);
		// g.init();
		AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.getOutTradeNo();
		// model.setOutTradeNo(out_trade_no);
		model.setTradeNo("2017061321001004040275979115");
		model.setTradeNo("2017062921001004450274967595");
		alipayRequest.setBizModel(model);
		try {
			AlipayTradeQueryResponse alipayResponse = g.alipayClient.execute(alipayRequest);
			if (alipayResponse.isSuccess()) {

			}
			// TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED
			alipayResponse.getTradeStatus();

			System.out.println(alipayResponse.getBody());
		}
		catch (AlipayApiException e) {
			e.printStackTrace();
		}

	}
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
