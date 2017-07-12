package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.google.common.collect.ImmutableMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Newpay query request for payment and refund
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayQueryRequest extends NewpayRequest {
	private static final long serialVersionUID = 1L;

	private final NewpayQueryModel model;

	NewpayQueryRequest(NewpayQueryModel model) {
		this.model = Objects.requireNonNull(model, "model cannot be null");
	}

	private String buildSignMsg() {
		// @formatter:off
		return new StringBuilder()
		 .append("version=").append(version)
		 .append("&serialID=").append(requestId)
		 .append("&mode=").append(model.getMode().getCode())
		 .append("&type=").append(model.getType().getCode())
		 .append("&orderID=").append(model.getOrderNo())
		 .append("&beginTime=").append(formatTimestamp(model.getBeginTime()))
		 .append("&endTime=").append(formatTimestamp(model.getEndTime()))
		 .append("&partnerID=").append(partnerId)
		 .append("&remark=").append(model.getRemark())
		 .append("&charset=").append(charset)
		 .append("&signType=").append(signType)
		 .toString();
		// @formatter:on
	}

	private Map<String, String> buildParams() {
		if (signature == null) {
			throw new NewpayException("Signature of this request is not set");
		}
		requestId = newRequestId();
		String signMsg = buildSignMsg();
		sign = signRsaSignature(signMsg, NewpayCharset.fromString(charset));
		Map<String, String> result = convertQueryString(signMsg);
		return ImmutableMap.<String, String> builder().putAll(result).put("signMsg", sign).build();
	}

	RequestBody build() {
		Map<String, String> params = buildParams();
		FormBody.Builder builder = new FormBody.Builder();
		params.entrySet().stream().forEach(e -> {
			builder.add(e.getKey(), e.getValue());
		});
		return builder.build();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayQueryRequest [version=").append(version).append(", requestId=").append(requestId)
				.append(", partnerId=").append(partnerId).append(", charset=").append(charset).append(", signType=")
				.append(signType).append(", sign=").append(sign).append(", signature=").append(signature)
				.append(", model=").append(model).append("]");
		return builder.toString();
	}

}
