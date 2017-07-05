package net.aircommunity.platform.service.internal.payment.wechat;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.binarywang.wxpay.bean.WxPayOrderNotifyResponse;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.github.binarywang.wxpay.util.SignUtils;

import io.micro.common.Strings;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Constants;
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
import net.aircommunity.platform.model.domain.StandardOrder;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.internal.payment.AbstractPaymentGateway;

/**
 * Wechat PaymentGateway
 * 
 * @author Bin.Zhang
 */
@Service
public class WechatPaymentGateway extends AbstractPaymentGateway {
	private static final String TRADE_TYPE = "APP";
	private static final String RETURN_CODE_SUCCESS = "SUCCESS"; // SUCCESS/FAIL
	private static final String RETURN_MSG_SUCCESS = "OK";
	private static final String RETURN_MSG_FAIL = "FAIL";
	private static final String CLIENT_NOTIFICATION_ORDERNO = "orderNo";
	private static final String CLIENT_NOTIFICATION_RESULT = "result";
	// orderNo_timestamp (29 in length, max 32 permitted by wechat)
	private static final String OUT_TRADENO_SEPARATOR = "_";
	private static final String OUT_TRADENO_FORMAT = "%s" + OUT_TRADENO_SEPARATOR + "%s";
	// SUCCESS: 退款申请接收成功，结果通过退款查询接口查询, FAIL: 提交业务失败
	private static final String RESULT_CODE_SUCCESS = "SUCCESS"; // SUCCESS/FAIL
	private static final PaymentResponse NOTIFICATION_RESPONSE_SUCCESS = PaymentResponse
			.success(WxPayOrderNotifyResponse.success(RETURN_MSG_SUCCESS));
	private static final PaymentResponse NOTIFICATION_RESPONSE_FAILURE = PaymentResponse
			.failure(WxPayOrderNotifyResponse.fail(RETURN_MSG_FAIL));

	private final WxPayService wxPayService;
	private final WechatConfig config;

	@Autowired
	public WechatPaymentGateway(WechatConfig wechatConfig) {
		config = wechatConfig;
		WxPayConfig wxconfig = new WxPayConfig();
		wxconfig.setAppId(config.getAppId());
		wxconfig.setMchId(config.getMchId());
		wxconfig.setMchKey(config.getMchKey());
		wxconfig.setKeyPath(new File(System.getProperty("user.dir"), config.getKeyPath()).getAbsolutePath());
		wxPayService = new WxPayServiceImpl();
		wxPayService.setConfig(wxconfig);
	}

	@PostConstruct
	private void init() {
		LOG.debug("Wechat config original {}", config);
		if (Strings.isBlank(config.getNotifyUrl())) {
			config.setNotifyUrl(getServerNotifyUrl());
		}
		if (Strings.isBlank(config.getReturnUrl())) {
			config.setReturnUrl(getClientReturnUrl());
		}
		LOG.debug("Wechat config final: {}", config);
	}

	@Override
	public Method getPaymentMethod() {
		return Method.WECHAT;
	}

	@Override
	public PaymentRequest createPaymentRequest(Order order) {
		LOG.debug("Create payment request for order: {}", order);
		LOG.info("orderNo: {}", order.getOrderNo());
		try {
			// 请求对象，注意一些参数如appid、mchid等不用设置，方法内会自动从配置对象中获取到（前提是对应配置中已经设置）
			WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
			Product product = order.getProduct();
			// 商户订单号 (必填) (商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一)
			// Generate OutTradeNo format: orderNo_timestamp, e.g. C32E53EE0C00000_1499185071647 (最简单的方法避免->商户订单号重复)
			// 假如修改价钱，对于同一个orderNo, 就会出现 “商户订单号重复” 错误！
			request.setOutTradeNo(
					String.format(OUT_TRADENO_FORMAT, order.getOrderNo(), Long.toString(System.currentTimeMillis())));
			// 商品描述交易字段 (必填)
			request.setBody(M.msg(M.PAYMENT_PRODUCT_DESCRIPTION, product.getName()));
			// 商品详细列表，使用JSON格式，传输签名前请务必使用CDATA标签将JSON文本串保护起来。
			// request.setDetail(detail);
			// 符合ISO 4217标准的三位字母代码，默认人民币：CNY
			// request.setFeeType(feeType);
			// 订单总金额，单位为分 (需要转换) (必填)
			int totalFee = OrderPrices.convertPrice(order.getTotalPrice());
			request.setTotalFee(totalFee);
			LOG.debug("Totoal Fee (converted): {}", request.getTotalFee());
			// 用户端实际IP (必填)
			String ip = order.getOwner().getLastAccessedIp();
			if (Strings.isBlank(ip)) {
				ip = Constants.IP_UNKNOWN;
			}
			request.setSpbillCreateIp(ip);
			// 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。 (必填)
			request.setNotifyURL(config.getNotifyUrl());
			// 支付类型 (必填)
			request.setTradeType(TRADE_TYPE);
			WxPayUnifiedOrderResult paymentInfo = wxPayService.unifiedOrder(request);
			LOG.debug("Unified order result: {}", paymentInfo);
			Map<String, String> orderInfo = signPrepay(paymentInfo);
			LOG.info("orderInfo: {}", orderInfo);
			return new PaymentRequest(orderInfo);
		}
		catch (Exception e) {
			if (WxErrorException.class.isAssignableFrom(e.getClass())) {
				WxError ex = WxErrorException.class.cast(e).getError();
				LOG.error(String.format("Failed to create payment info, errcode: %s, errmsg: %s,  cause: %s",
						ex.getErrorCode(), ex.getErrorMsg(), e.getMessage()), e);
			}
			else {
				LOG.error(String.format("Failed to create payment info,  cause: %s", e.getMessage()), e);
			}
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	private Map<String, String> signPrepay(WxPayUnifiedOrderResult paymentInfo) {
		SortedMap<String, String> prepayInfo = new TreeMap<>();
		prepayInfo.put("appid", paymentInfo.getAppid());
		prepayInfo.put("partnerid", paymentInfo.getMchId());
		prepayInfo.put("prepayid", paymentInfo.getPrepayId());
		prepayInfo.put("package", "Sign=WXpay");
		prepayInfo.put("noncestr", paymentInfo.getNonceStr());
		prepayInfo.put("timestamp", String.valueOf(System.currentTimeMillis()).substring(0, 10));
		String sign = SignUtils.createSign(prepayInfo, config.getMchKey());
		prepayInfo.put("sign", sign);
		return prepayInfo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaymentVerification verifyClientPaymentNotification(PaymentNotification notification) {
		LOG.info("client notification: {}", notification);
		Map<String, Object> data = (Map<String, Object>) notification.getData();
		String orderNo = (String) data.get(CLIENT_NOTIFICATION_ORDERNO);
		String result = (String) data.get(CLIENT_NOTIFICATION_RESULT);
		// XXX NOTE: we expecting notification signature verification to prevent attack
		// BUT, wechat pay result from APP dosen't have such information as Alipay did
		// we just sign the notification ourself?
		if (RETURN_MSG_SUCCESS.equalsIgnoreCase(result)) {
			Optional<Order> orderRef = commonOrderService.lookupByOrderNo(orderNo);
			if (orderRef.isPresent()) {
				Order order = orderRef.get();
				commonOrderService.updateOrderStatus(order, Order.Status.PAYMENT_IN_PROCESS);
				LOG.debug("Processed client notification for order {}", order);
				return PaymentVerification.SUCCESS;
			}
		}
		return PaymentVerification.FAILURE;
	}

	@Override
	public PaymentResponse processServerPaymentNotification(PaymentNotification notification) {
		LOG.info("Server payment notification: {}", notification);
		try {
			String xmlData = (String) notification.getData();
			// 1) parse and verify result
			WxPayOrderNotifyResult notifyResponse = wxPayService.getOrderNotifyResult(xmlData);
			if (RETURN_CODE_SUCCESS.equals(notifyResponse.getReturnCode())) {
				if (RESULT_CODE_SUCCESS.equals(notifyResponse.getResultCode())) {
					// 2) check APP_ID
					if (!config.getAppId().equals(notifyResponse.getAppid())) {
						LOG.error("APP ID mismatch expected: {}, but was: {}, payment failed", config.getAppId(),
								notifyResponse.getAppid());
						return NOTIFICATION_RESPONSE_FAILURE;
					}
					// 3) check order and update status
					int totalFee = notifyResponse.getTotalFee();
					BigDecimal totalAmount = OrderPrices.convertPrice(totalFee);
					String tradeNo = notifyResponse.getTransactionId(); // 微信订单号 string(28)
					String outTradeNo = notifyResponse.getOutTradeNo();
					String orderNo = outTradeNo.split(OUT_TRADENO_SEPARATOR)[0];
					Date paymentDate = new Date();// 交易付款时间, no date from wechat server response
					PaymentResponse response = doProcessPaymentSuccess(totalAmount, orderNo, tradeNo, paymentDate);
					if (!response.isSuccessful()) {
						tryAutoRefundOnFailure(tradeNo, orderNo, totalFee, response.getBody());
						return response;
					}
					return NOTIFICATION_RESPONSE_SUCCESS;
				}
				LOG.error("Payment failed, errCode: {}, errMsg: {}", notifyResponse.getErrCode(),
						notifyResponse.getErrCodeDes());
				return NOTIFICATION_RESPONSE_FAILURE;
			}
			LOG.error("Payment failed, returnCode: {}, returnMsg: {}", notifyResponse.getReturnCode(),
					notifyResponse.getReturnMsg());
		}
		catch (Exception e) {
			if (WxErrorException.class.isAssignableFrom(e.getClass())) {
				WxError ex = WxErrorException.class.cast(e).getError();
				LOG.error(String.format("Failed to process payment info, errcode: %s, errmsg: %s,  cause: %s",
						ex.getErrorCode(), ex.getErrorMsg(), e.getMessage()), e);
			}
			else {
				LOG.error(String.format("Failed to create payment info,  cause: %s", e.getMessage()), e);
			}
		}
		return NOTIFICATION_RESPONSE_FAILURE;
	}

	private void tryAutoRefundOnFailure(String tradeNo, String orderNo, int refundAmount, String reason) {
		WxPayRefundRequest request = new WxPayRefundRequest();
		request.setTransactionId(tradeNo);
		request.setOutRefundNo(orderNo);
		request.setRefundFee(refundAmount);
		request.setTotalFee(refundAmount);
		try {
			WxPayRefundResult refundResponse = wxPayService.refund(request);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund response: {}", refundResponse.toMap());
			}
			// SUCCESS
			if (RETURN_CODE_SUCCESS.equals(refundResponse.getReturnCode())
					&& RESULT_CODE_SUCCESS.equals(refundResponse.getResultCode())) {
				LOG.info("Final auto refund for tradeNo: {} success, reason: {}", tradeNo, reason);
				return;
			}
		}
		catch (Exception e) {
			if (WxErrorException.class.isAssignableFrom(e.getClass())) {
				WxError ex = WxErrorException.class.cast(e).getError();
				LOG.error(String.format("Auto refund failure for tradeNo: %s, cause: %s", tradeNo,
						M.msg(M.REFUND_FAILURE, ex.getErrorCode(), ex.getErrorMsg())), e);
			}
			else {
				LOG.error(String.format("Auto refund failure for tradeNo: %s, cause: %s", tradeNo, e.getMessage()), e);
			}
		}
	}

	@Override
	public RefundResponse refundPayment(Order order, BigDecimal refundAmount) {
		// XXX NOTE: we only support StandardOrder ATM
		if (!StandardOrder.class.isAssignableFrom(order.getClass())) {
			LOG.error("{} is not a StandardOrder, cannot refund!", order);
			throw new AirException(Codes.INTERNAL_ERROR, M.msg(M.INTERNAL_SERVER_ERROR));
		}
		try {
			StandardOrder theOrder = (StandardOrder) order;
			LOG.info("Refund {}, refundAmount: {}", order, refundAmount);
			WxPayRefundRequest request = new WxPayRefundRequest();
			// NOTE: MUST use TransactionId to refund, because generated orderNo for this trade is not saved
			// 微信订单号
			request.setTransactionId(theOrder.getPayment().getTradeNo());
			// 商户订单号 (必填) (XXX NOTE: CANNOT USE for refund)
			// request.setOutTradeNo(order.getOrderNo());
			// 商户退款单号 (必填) (商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。)
			request.setOutRefundNo(order.getOrderNo());
			// 订单金额 (必填)
			int totalFee = OrderPrices.convertPrice(order.getTotalPrice());
			request.setTotalFee(totalFee);
			// 退款金额 (必填)
			BigDecimal originalRefundAmount = OrderPrices.normalizePrice(order.getTotalPrice());
			refundAmount = OrderPrices.normalizePrice(refundAmount);
			// if refundAmount=0, we will use full amount refund of the order
			BigDecimal actualRefundAmount = (BigDecimal.ZERO == refundAmount) ? originalRefundAmount : refundAmount;
			int refundFee = OrderPrices.convertPrice(actualRefundAmount);
			request.setRefundFee(refundFee);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund request: {}", request.toXML());
			}
			WxPayRefundResult refundResponse = wxPayService.refund(request);
			if (LOG.isInfoEnabled()) {
				LOG.info("Final refund response: {}", refundResponse.toMap());
			}
			// SUCCESS
			if (RETURN_CODE_SUCCESS.equals(refundResponse.getReturnCode())) {
				if (RESULT_CODE_SUCCESS.equals(refundResponse.getResultCode())) {
					Refund refund = new Refund();
					// 退款总金额,单位为分
					String refundFeeReceived = refundResponse.getRefundFee();
					BigDecimal refundAmountReceived = OrderPrices
							.convertPrice(new BigDecimal(refundFeeReceived).intValue());
					LOG.info("Received raw refundFee: {} from webchat server, converted to: {}", refundFeeReceived,
							refundAmountReceived);
					refund.setAmount(refundAmountReceived);
					refund.setMethod(getPaymentMethod());
					// the OutTradeNo returned is the generated one, NOT original orderNo, so need conversion
					String orderNo = refundResponse.getOutTradeNo().split(OUT_TRADENO_SEPARATOR)[0];
					refund.setOrderNo(orderNo);
					refund.setTradeNo(refundResponse.getTransactionId());
					refund.setTimestamp(new Date()); // No refund date from wechat
					refund.setRefundReason(order.getRefundReason());
					refund.setRefundResult(M.msg(M.REFUND_SUCCESS));
					return RefundResponse.success(refund);
				}
				return RefundResponse
						.failure(M.msg(M.REFUND_FAILURE, refundResponse.getErrCode(), refundResponse.getErrCodeDes()));
			}
			return RefundResponse
					.failure(M.msg(M.REFUND_FAILURE, refundResponse.getReturnCode(), refundResponse.getReturnMsg()));
		}
		catch (Exception e) {
			LOG.error(String.format("Refund failure for order: %s, cause: %s", order, e.getMessage()), e);
			String refundFailureCause = e.getLocalizedMessage();
			if (WxErrorException.class.isAssignableFrom(e.getClass())) {
				WxError ex = WxErrorException.class.cast(e).getError();
				refundFailureCause = M.msg(M.REFUND_FAILURE, ex.getErrorCode(), ex.getErrorMsg());
			}
			return RefundResponse.failure(refundFailureCause);
		}
	}
}
