package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

import io.micro.common.DateFormats;
import io.micro.common.Strings;
import io.micro.common.UUIDs;
import io.micro.support.ObjectMappers;

/**
 * Newpay base message object.
 * 
 * @author Bin.Zhang
 */
public class NewpayMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	protected static final String VERSION = "2.6";
	protected static final String RC_SUCCESS = "0000";
	protected static final ObjectMapper MAPPER = ObjectMappers.getObjectMapper();
	protected static final SimpleDateFormat TIME_FORMATTER = DateFormats.simple("yyyyMMddHHmmss");

	protected transient NewpaySignature signature;

	public static <T> T convert(Map<String, String> data, Class<T> type) {
		try {
			String str = MAPPER.writeValueAsString(data);
			return MAPPER.readValue(str, type);
		}
		catch (Exception e) {
			throw new NewpayException(String.format("Failed to convert %s to %s", data, type), e);
		}
	}

	public static String newRequestId() {
		return UUIDs.shortTimebased();
	}

	public static String currentTimestamp() {
		return TIME_FORMATTER.format(new Date());
	}

	public static String formatTimestamp(Date timestamp) {
		if (timestamp == null) {
			return "";
		}
		return TIME_FORMATTER.format(timestamp);
	}

	public static Map<String, String> convertQueryString(String queryString) {
		return Splitter.on("&").withKeyValueSeparator("=").split(queryString);
	}

	public static String getResultMessage(String resultCode) {
		return RESULT_CODES.get(resultCode);
	}

	void setSignature(NewpaySignature signature) {
		this.signature = signature;
	}

	/**
	 * Sign the signMsg with RSA Signature
	 * 
	 * @param signMsg message to be signed
	 * @param charset
	 * @return RSA signature
	 */
	protected String signRsaSignature(String signMsg, NewpayCharset charset) {
		return signature.signWithRSA(signMsg, charset);
	}

	/**
	 * Verify RSA Signature
	 * 
	 * @param signMsg message to be signed
	 * @param sign the signature
	 * @param charset
	 * @return true if valid
	 */
	protected void verifyRsaSignature(String signMsg, String sign, NewpayCharset charset) {
		if (signature == null) {
			throw new NewpayException("Signature of this message is not set, cannot verify RSA signature");
		}
		if (!signature.verifyRsaSignature(signMsg, sign, charset)) {
			throw new NewpayException(String.format("Invalid RSA Signature: %s for msg: %s", sign, signMsg));
		}
	}

	/**
	 * Assert that an string is not null or empty (only spaces is also considered as empty).
	 * 
	 * @param str the string to check
	 * @param message the message to use if the assertion fails
	 * @throws IllegalArgumentException if the string is null or empty
	 */
	protected static String notNullOrEmpty(String str, String message) {
		if (str == null || str.trim().equals("")) {
			throw new IllegalArgumentException(message);
		}
		return str;
	}

	public static class TimeAdapter extends XmlAdapter<String, Date> {
		@Override
		public String marshal(Date date) throws Exception {
			if (date == null) {
				return "";
			}
			return TIME_FORMATTER.format(date);
		}

		@Override
		public Date unmarshal(String date) throws Exception {
			if (Strings.isBlank(date)) {
				return null;
			}
			return TIME_FORMATTER.parse(date);
		}
	}

	private static final Map<String, String> RESULT_CODES = new HashMap<>();
	static {
		RESULT_CODES.put("0000", "成功");
		RESULT_CODES.put("0009", "查询出的交易记录为空");
		RESULT_CODES.put("0301", "退款历史记录失败");
		RESULT_CODES.put("0302", "退款商户不存在");
		RESULT_CODES.put("0303", "报文必填参数为空");
		RESULT_CODES.put("0304", "商户退款订单时间不正确,以 YYYYMMDDHHMMSS 格式的时间字符串");
		RESULT_CODES.put("0306", "退款交易对应的原订单不存在");
		RESULT_CODES.put("0307", "即时交易:订单状态不为已成功,不能进行退款");
		RESULT_CODES.put("0308", "商户退款订单处理完成,请勿重复退款");
		RESULT_CODES.put("0309", "商户退款订单金额不正确,以分为单位的金额,最大长度不超过 10");
		RESULT_CODES.put("0310", "全额退款类型:退款金额不符");
		RESULT_CODES.put("0311", "退款失败,退款金额大于支付余额");
		RESULT_CODES.put("0312", "退款请求序列号已经存在");
		RESULT_CODES.put("0313", "商户退款订单号已经存在");
		RESULT_CODES.put("0314", "报文验签失败");
		RESULT_CODES.put("0315", "付款方帐户止入");
		RESULT_CODES.put("0316", "商户止入或者止出");
		RESULT_CODES.put("0317", "退款逻辑验证失败");
		RESULT_CODES.put("0318", "更新余额异常");
		RESULT_CODES.put("0319", "手续费用计算失败");
		RESULT_CODES.put("0320", "退款订单创建失败");
		RESULT_CODES.put("0321", "帐务退款失败");
		RESULT_CODES.put("0322", "网关订单异常");
		RESULT_CODES.put("0323", "充退异常");
		RESULT_CODES.put("0324", "构建退款订单异常");
		RESULT_CODES.put("0325", "网关订单记帐成功修改退款订单失败");
		RESULT_CODES.put("0326", "报文参数错误");
		RESULT_CODES.put("0327", "退款失败,退款金额大于商户可用余额");
		RESULT_CODES.put("0328", "退款金额小于等于 0");
		RESULT_CODES.put("0329", "商户退款订单处理完成,请勿重复退款");
		RESULT_CODES.put("0330", "网关订单非交易完成状态,不可以进行退款");
		RESULT_CODES.put("0331", "易卡退付款方手续费失败");
		RESULT_CODES.put("0332", "请求版本不正确");
		RESULT_CODES.put("0401", "基本数据校验失败");
		RESULT_CODES.put("0402", "校验业务参数失败");
		RESULT_CODES.put("0403", "查询商户请求网关历史记录失败");
		RESULT_CODES.put("0404", "查询API-失败");
		RESULT_CODES.put("0405", "时间验证失败");
		RESULT_CODES.put("0406", "时间转换类型失败");
		RESULT_CODES.put("0407", "开始时间不能大于结束时间");
		RESULT_CODES.put("0408", "查询时间最大范围只能15天以内");
		RESULT_CODES.put("0409", "创建网关返回商户结果失败");
		RESULT_CODES.put("0410", "查询验签失败");
		RESULT_CODES.put("0411", "查询订单不存在");
	}

}
