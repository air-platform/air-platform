package net.aircommunity.platform.service.internal.payment.alipay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.micro.common.DateFormats;
import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.model.domain.StandardOrder;
import net.aircommunity.platform.model.domain.Trade;
import net.aircommunity.platform.model.payment.PaymentNotification;
import net.aircommunity.platform.model.payment.PaymentRequest;
import net.aircommunity.platform.model.payment.PaymentResponse;
import net.aircommunity.platform.model.payment.PaymentVerification;
import net.aircommunity.platform.model.payment.RefundResponse;
import net.aircommunity.platform.model.payment.TradeQueryResult;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.internal.payment.AbstractPaymentGateway;

/**
 * Alipay PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
public class AlipayPaymentGateway extends AbstractPaymentGateway {
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

	private final AlipayClient client;
	private final AlipayConfig config;

	@Inject
	public AlipayPaymentGateway(AlipayConfig alipayConfig) {
		config = alipayConfig;
		client = new DefaultAlipayClient(ALIPAY_GATEWAY_URL, config.getAppId(), config.getAppPrivateKey(),
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
	public Trade.Method getPaymentMethod() {
		return Trade.Method.ALIPAY;
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
			AlipayTradeAppPayResponse response = client.sdkExecute(request);
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
	public PaymentVerification verifyClientNotification(PaymentNotification notification) {
		Map<String, String> result = (Map<String, String>) notification.getData();
		LOG.info("client notification: {}", result);
		String resultStatus = result.get(NOTIFICATION_RESULT_STATUS);
		if (PAYMENT_STATUS_SUCCESS.equals(resultStatus)) {
			try {
				// 1) parse result (it also will verified sign)
				AlipayTradeAppPayResponse response = client.parseAppSyncResult(result, AlipayTradeAppPayRequest.class);
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

	@Override
	@SuppressWarnings("unchecked")
	public PaymentResponse processServerNotification(PaymentNotification notification) {
		// NOTE:
		// 程序执行完后必须打印输出“success”（不包含引号）。如果商户反馈给支付宝的字符不是success这7个字符，
		// 支付宝服务器会不断重发通知，直到超过24小时22分钟。
		// 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）；
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
				LOG.warn("AppId mismatch expected: {}, but was: {}, payment notifcation ignored", config.getAppId(),
						appId);
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
			AlipayTradeRefundResponse refundResponse = client.execute(alipayRequest);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund response: {}", toJson(refundResponse));
			}
			if (refundResponse.isSuccess()) {
				commonOrderService.handleOrderPaymentFailure(orderNo, M.msg(M.PAYMENT_AUTO_REFUND_ON_FAILURE, reason));
				LOG.info("Final auto refund for tradeNo: {}, orderNo: {} success, reason: {}", tradeNo, orderNo,
						reason);
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
		StandardOrder theOrder = convertOrder(order);
		//
		AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		// 商户订单号和支付宝交易号不能同时为空 (二选一), trade_no、 out_trade_no如果同时存在优先取trade_no
		model.setOutTradeNo(order.getOrderNo());
		// 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
		String requestNo = theOrder.getRefund().getRequestNo();
		model.setOutRequestNo(requestNo);
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
			AlipayTradeRefundResponse refundResponse = client.execute(alipayRequest);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund response: {}", toJson(refundResponse));
			}
			if (refundResponse.isSuccess()) {
				Refund refund = new Refund();
				refund.setAmount(actualRefundAmount);
				refund.setMethod(getPaymentMethod());
				refund.setOrderNo(refundResponse.getOutTradeNo());
				refund.setTradeNo(refundResponse.getTradeNo());
				refund.setRequestNo(requestNo);
				refund.setTimestamp(refundResponse.getGmtRefundPay());
				refund.setRefundReason(order.getRefundReason());
				refund.setRefundResult(M.msg(M.REFUND_SUCCESS));
				refund.setStatus(Refund.Status.SUCCESS);
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

	@Override
	public Optional<TradeQueryResult> queryPayment(Order order) {
		StandardOrder theOrder = convertOrder(order);
		Payment payment = theOrder.getPayment();
		if (payment == null) {
			LOG.warn("There is no payment for ({}){}", order.getType(), order.getOrderNo());
			return Optional.empty();
		}
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.setOutTradeNo(order.getOrderNo());
		request.setBizModel(model);
		try {
			AlipayTradeQueryResponse response = client.execute(request);
			// 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
			// TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
			LOG.debug("Payment query response: {}", response.getBody());
			if (response.isSuccess()) {
				return Optional.of(TradeQueryResult.builder().setTradeNo(response.getTradeNo())
						.setOrderNo(response.getOutTradeNo()).setRequestNo(payment.getRequestNo())
						.setTradeMethod(Payment.Method.ALIPAY).setTradeType(Trade.Type.PAYMENT)
						.setAmount(new BigDecimal(response.getTotalAmount())).setTimestamp(response.getSendPayDate())
						.build());
			}
		}
		catch (Exception e) {
			LOG.error("Got error when query payment for order: " + order, e);
		}
		return Optional.empty();
	}

	@Override
	public Optional<TradeQueryResult> queryRefund(Order order) {
		StandardOrder theOrder = convertOrder(order);
		Refund refund = theOrder.getRefund();
		if (refund == null) {
			LOG.warn("There is no refund for ({}){}", order.getType(), order.getOrderNo());
			return Optional.empty();
		}
		AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
		AlipayTradeFastpayRefundQueryModel refundQuery = new AlipayTradeFastpayRefundQueryModel();
		// 商户订单号和支付宝交易号不能同时为空。 trade_no、 out_trade_no如果同时存在优先取trade_no, 商户订单号，和支付宝交易号二选一
		refundQuery.setOutTradeNo(theOrder.getOrderNo());
		String requestNo = refund.getRequestNo();
		refundQuery.setOutRequestNo(requestNo);
		request.setBizModel(refundQuery);
		try {
			AlipayTradeFastpayRefundQueryResponse response = client.execute(request);
			LOG.debug("Refund query response: {}", response.getBody());
			if (response.isSuccess()) {
				Date timestamp = new Date(); // XXX NOTE: there is no refund time returned
				return Optional.of(TradeQueryResult.builder().setRequestNo(requestNo).setTradeNo(response.getTradeNo())
						.setOrderNo(response.getOutTradeNo()).setTradeMethod(Payment.Method.ALIPAY)
						.setTradeType(Trade.Type.REFUND).setAmount(new BigDecimal(response.getRefundAmount()))
						.setTimestamp(timestamp).build());
			}
		}
		catch (Exception e) {
			LOG.error("Got error when query refund for order: " + order, e);
		}
		return Optional.empty();
	}

	private String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		}
		catch (JsonProcessingException ignore) {
		}
		return null;
	}
}
