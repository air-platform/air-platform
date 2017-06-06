package net.aircommunity.platform.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.nls.M;

/**
 * Payment RESTful API
 * 
 * http://blog.csdn.net/u010399316/article/details/53008248
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("payment")
public class PaymentResource {
	private static final Logger LOG = LoggerFactory.getLogger(PaymentResource.class);

	private static final String APP_PRIVATE_KEY = ""; // TODO
	private static final String ALIPAY_PUBLIC_KEY = "";// TODO
	private static final String APP_ID = "";
	private static final String PAY_METHOD = "alipay.trade.app.pay";
	private static final String SIGN_TYPE = "RSA2";
	private static final String PAY_VERSION = "1.0";
	private static final String PAY_FORMAT = "json";
	private static final String PAY_CHARSET = "utf-8";
	private static final String PAY_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// String content = "app_id=" + APP_ID + "&biz_content=" + bizContent + "&charset=" + PAY_CHARSET + "&method="
	// + PAY_METHOD + "notify_url=" + (notify_url) + "&sign_type=" + SIGN_TYPE + "timestamp=" + timestamp
	// + "&version=" + PAY_VERSION;

	@Resource
	private ObjectMapper objectMapper;

	/**
	 * 对支付宝支付信息进行签名<br>
	 * 
	 * a) 请求参数的sign字段请务必在服务端完成签名生成（不要在客户端本地签名）<br>
	 * b）支付请求中的订单金额total_amount，请务必依赖服务端，不要轻信客户端上行的数据（客户端本地上行数据在用户手机环境中无法确保一定安全）<br>
	 */
	@POST
	@Path("alipay/sign")
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed(Roles.ROLE_ADMIN)
	public JsonObject signPaymentRequest(@NotNull @QueryParam("orderNo") String orderNo) {
		try {
			// 1)
			ImmutableMap.Builder<String, Object> bizData = ImmutableMap.builder();
			// 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。 e.g. Iphone6 16G
			bizData.put("body", "xxx"); // TODO
			// 商品的标题/交易标题/订单标题/订单关键字等
			bizData.put("subject", "xxx"); // TODO
			// 商户网站唯一订单号
			bizData.put("out_trade_no", orderNo); // TODO
			// 设置未付款支付宝交易的超时时间，一旦超时，该笔交易就会自动被关闭。
			// 当用户进入支付宝收银台页面（不包括登录页面），会触发即刻创建支付宝交易，此时开始计时。
			// 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
			// 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
			bizData.put("timeout_express", "2h");
			// 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
			bizData.put("total_amount", "1.00");
			// 收款支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
			bizData.put("seller_id", "xxxx"); // TODO
			// 销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
			bizData.put("product_code", "QUICK_MSECURITY_PAY");
			String bizContent = objectMapper.writeValueAsString(bizData.build());

			// 2)
			// 增加支付异步通知回调,记住上下notify_url的位置,全在sign_type之前,很重要,同样放在最后都不行
			String notifyUrl = "https://pay.ytbapp.com/payment/notify"; // TODO
			SimpleDateFormat sdf = new SimpleDateFormat(PAY_TIMESTAMP_FORMAT);
			String timestamp = sdf.format(new Date());
			ImmutableMap.Builder<String, Object> params = ImmutableMap.builder();
			params.put("app_id", APP_ID);
			params.put("biz_content", bizContent);
			params.put("charset", PAY_CHARSET);
			params.put("format", PAY_FORMAT);
			params.put("method", PAY_METHOD);
			params.put("notify_url", notifyUrl);
			params.put("sign_type", SIGN_TYPE);
			params.put("timestamp", timestamp);
			params.put("version", PAY_VERSION);
			String sign = AlipaySignature.rsaSign(encodeParams(params.build()), APP_PRIVATE_KEY, PAY_CHARSET);
			params.put("sign", sign);
			String signature = objectMapper.writeValueAsString(params.build());
			return Json.createObjectBuilder().add("signature", signature).build(); // TODO
		}
		catch (Exception e) {
			LOG.error("Failed to sign payment request:" + e.getMessage(), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	private static String encodeParams(Map<String, Object> params) {
		return new StringBuilder().append(Joiner.on("&").join(params.entrySet().stream().map(e -> {
			Object value = (e.getValue() == null) ? "" : encode(String.valueOf(e.getValue()));
			return new StringBuilder().append(encode(e.getKey())).append("=").append(value).toString();
		}).collect(Collectors.toList()))).toString();
	}

	private static String encode(String str) {
		try {
			return URLEncoder.encode(str, StandardCharsets.UTF_8.name()).replace("+", "%20");
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unsupported Encoding utf8", e);
		}
	}

	/**
	 * 支付宝支付成功后. 会回调该接口
	 */
	@POST
	@Path("alipay/notify")
	@Consumes(MediaType.TEXT_PLAIN)
	@RolesAllowed(Roles.ROLE_ADMIN)
	public String notifyPayment(HttpServletRequest request) throws Exception {
		Map<String, String> params = new HashMap<>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		// 支付宝交易号
		String out_trade_no = request.getParameter("out_trade_no");// 商户订单号
		// 交易状态
		String trade_status = request.getParameter("trade_status");
		double total_amount = Double.valueOf(request.getParameter("total_amount"));
		boolean signVerified = false;
		try {
			signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, PAY_CHARSET);
		}
		catch (AlipayApiException e) {
			e.printStackTrace();
			return "fail";// 验签发生异常,则直接返回失败
		}
		// 调用SDK验证签名
		if (signVerified) {
			// TODO 验签成功后
//			// 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
//			String result = updateALiPayOrderStatus(out_trade_no);
//			System.out.println("验证成功,去更新状态 \t订单号:" + out_trade_no + "来自支付宝支付,更新结果:" + result);
//			BaseResponse baseResponse = GsonUtils.getGson().fromJson(result, BaseResponse.class);
//			if (null != baseResponse && baseResponse.isSucceeded) {
//				return "success";
//			}
//			else {
//				return "fail";// 更新状态失败
//			}
			return null;
		}
		else {
			// TODO 验签失败则记录异常日志，并在response中返回failure.
			System.out.println("验证失败,不去更新状态");
			return "fail";
		}
	}

}
