package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * Newpay query response
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NewpayQueryResponse extends NewpayResponse {
	private static final long serialVersionUID = 1L;

	// 请求序列号 String(32) - 同请求报文
	@XmlElement(name = "serialID", required = true)
	private String requestId;

	// 1:单笔 2:批量
	@XmlElement(name = "mode", required = true)
	@XmlJavaTypeAdapter(NewpayQueryModeAdapter.class)
	private NewpayQueryMode mode;

	// 1:支付订单 2:退款订单
	@XmlElement(name = "type", required = true)
	@XmlJavaTypeAdapter(NewpayQueryTypeAdapter.class)
	private NewpayQueryType type;

	// String(10)
	@XmlElement(name = "queryDetailsSize", required = true)
	private int queryDetailsSize;

	// String(2048) 查询结果集长度(订单数量) 特殊数值: -1:查询出现异常 0:无查询结果
	@XmlElement(name = "queryDetails", required = true)
	private String queryDetails;

	// NOT newpay field
	private List<NewpayPaymentQueryDetails> paymentQueryDetails = Collections.emptyList();
	private List<NewpayRefundQueryDetails> refundQueryDetails = Collections.emptyList();

	/**
	 * Parse the data to construct NewpayQueryResponse (signature is not verified)
	 * 
	 * @param data the incoming key value data
	 * @return NewpayQueryResponse
	 */
	public static NewpayQueryResponse parse(Map<String, String> data) {
		NewpayQueryResponse response = convert(data, NewpayQueryResponse.class);
		if (response.getType() == NewpayQueryType.PAYMENT) {
			response.parsePaymentQueryDetails();
		}
		if (response.getType() == NewpayQueryType.REFUND) {
			response.parseRefundQueryDetails();
		}
		return response;
	}

	public NewpayQueryType getType() {
		return type;
	}

	public NewpayQueryMode getMode() {
		return mode;
	}

	public int getQueryDetailsSize() {
		return queryDetailsSize;
	}

	public List<NewpayPaymentQueryDetails> getPaymentQueryDetails() {
		return paymentQueryDetails;
	}

	public List<NewpayRefundQueryDetails> getRefundQueryDetails() {
		return refundQueryDetails;
	}

	private String buildSignMsg() {
		// @formatter:off
		return new StringBuilder()
		 .append("serialID=").append(requestId)
		 .append("&mode=").append(mode.getCode())
		 .append("&type=").append(type.getCode())
		 .append("&resultCode=").append(resultCode)
		 .append("&queryDetailsSize=").append(queryDetailsSize)
		 .append("&queryDetails=").append(queryDetails)
		 .append("&partnerID=").append(partnerId)
		 .append("&remark=").append(remark)
		 .append("&charset=").append(charset)
		 .append("&signType=").append(signType)
		 .toString();
		// @formatter:on
	}

	private void parsePaymentQueryDetails() {
		paymentQueryDetails = doParseQueryDetails(NewpayPaymentQueryDetails.class,
				NewpayPaymentQueryDetails.QUERY_DETAILS_PROPS, queryDetails);
	}

	private void parseRefundQueryDetails() {
		refundQueryDetails = doParseQueryDetails(NewpayRefundQueryDetails.class,
				NewpayRefundQueryDetails.QUERY_DETAILS_PROPS, queryDetails);
	}

	private <T> List<T> doParseQueryDetails(Class<T> type, Iterable<String> queryDetailsProps, String queryDetails) {
		ImmutableList.Builder<T> builder = ImmutableList.builder();
		for (String refundInfo : Splitter.on("|").split(queryDetails)) {
			Map<String, String> data = new HashMap<>();
			Iterator<String> iter = queryDetailsProps.iterator();
			StreamSupport.stream(Splitter.on(",").split(refundInfo).spliterator(), false).forEach(info -> {
				data.put(iter.next(), info);
			});
			System.out.println(data);
			builder.add(convert(data, type));
		}
		return builder.build();
	}

	public void verifySignature() {
		String signMsg = buildSignMsg();
		verifyRsaSignature(signMsg, sign, NewpayCharset.fromString(charset));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NewpayQueryResponse [requestId=").append(requestId).append(", mode=").append(mode)
				.append(", type=").append(type).append(", resultCode=").append(resultCode).append(", queryDetailsSize=")
				.append(queryDetailsSize).append(", queryDetails=").append(queryDetails)
				.append(", paymentQueryDetails=").append(paymentQueryDetails).append(", refundQueryDetails=")
				.append(refundQueryDetails).append(", partnerId=").append(partnerId).append(", remark=").append(remark)
				.append(", charset=").append(charset).append(", signType=").append(signType).append(", sign=")
				.append(sign).append("]");
		return builder.toString();
	}

	public static class NewpayQueryModeAdapter extends XmlAdapter<String, NewpayQueryMode> {
		@Override
		public String marshal(NewpayQueryMode mode) throws Exception {
			return mode.getCode();
		}

		@Override
		public NewpayQueryMode unmarshal(String code) throws Exception {
			return NewpayQueryMode.fromString(code);
		}
	}

	public static class NewpayQueryTypeAdapter extends XmlAdapter<String, NewpayQueryType> {
		@Override
		public String marshal(NewpayQueryType type) throws Exception {
			return type.getCode();
		}

		@Override
		public NewpayQueryType unmarshal(String code) throws Exception {
			return NewpayQueryType.fromString(code);
		}
	}
}
