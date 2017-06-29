package net.aircommunity.platform.service.internal.payment.wechat;

import java.math.BigDecimal;
import java.util.Date;

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
	// SUCCESS: 退款申请接收成功，结果通过退款查询接口查询, FAIL: 提交业务失败
	private static final String RESULT_CODE_SUCCESS = "SUCCESS"; // SUCCESS/FAIL
	private static final PaymentResponse NOTIFICATION_RESPONSE_SUCCESS = new PaymentResponse(
			WxPayOrderNotifyResponse.success(RETURN_MSG_SUCCESS));
	private static final PaymentResponse NOTIFICATION_RESPONSE_FAILURE = new PaymentResponse(
			WxPayOrderNotifyResponse.fail(RETURN_MSG_FAIL));

	private final WxPayService wxPayService;
	private final WechatConfig config;

	@Autowired
	public WechatPaymentGateway(WechatConfig config) {
		this.config = config;
		WxPayConfig wxconfig = new WxPayConfig();
		wxconfig.setAppId(config.getAppId());
		wxconfig.setMchId(config.getMchId());
		wxconfig.setMchKey(config.getMchKey());
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
			request.setOutTradeNo(order.getOrderNo());
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
			// 用户端实际ip (必填)
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
			LOG.info("orderInfo: {}", paymentInfo.getXmlString());
			return new PaymentRequest(paymentInfo.getXmlString());
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

	@Override
	public PaymentVerification verifyClientPaymentNotification(PaymentNotification notification) {
		// noops for webchat payment
		return PaymentVerification.SUCCESS;
	}

	@Override
	public PaymentResponse processServerPaymentNotification(PaymentNotification notification) {
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
					BigDecimal totalAmount = OrderPrices.convertPrice(notifyResponse.getTotalFee());
					String tradeNo = notifyResponse.getTransactionId();
					String orderNo = notifyResponse.getOutTradeNo();
					Date paymentDate = new Date();// 交易付款时间, no date from wechat server response
					return doProcessPaymentNotification(totalAmount, orderNo, tradeNo, paymentDate);
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
				LOG.error(String.format("Failed to create payment info, errcode: %s, errmsg: %s,  cause: %s",
						ex.getErrorCode(), ex.getErrorMsg(), e.getMessage()), e);
			}
			else {
				LOG.error(String.format("Failed to create payment info,  cause: %s", e.getMessage()), e);
			}
		}
		return NOTIFICATION_RESPONSE_FAILURE;
	}

	@Override
	public RefundResponse refundPayment(Order order, BigDecimal refundAmount) {
		try {
			LOG.info("Refund {}, refundAmount: {}", order, refundAmount);
			WxPayRefundRequest request = new WxPayRefundRequest();
			// 商户订单号 (必填)
			request.setOutTradeNo(order.getOrderNo());
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
					refund.setOrderNo(refundResponse.getOutTradeNo());
					refund.setTradeNo(refundResponse.getTransactionId());// 微信订单号
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

	@Override
	protected PaymentResponse getPaymentFailureResponse() {
		return NOTIFICATION_RESPONSE_FAILURE;
	}

	@Override
	protected PaymentResponse getPaymentSuccessResponse() {
		return NOTIFICATION_RESPONSE_SUCCESS;
	}
}
