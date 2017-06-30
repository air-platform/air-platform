package net.aircommunity.platform.service.internal.payment.newpay;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Newpay Gateway client.
 * 
 * @author Bin.Zhang
 */
public class NewpayClient {
	private static final Logger LOG = LoggerFactory.getLogger(NewpayClient.class);

	private final static String REFUND_URL = "https://gateway.hnapay.com/website/refund.htm";
	private final NewpayConfig config;
	private final NewpaySignature signature;
	private final OkHttpClient httpClient;

	public NewpayClient(NewpayConfig config, OkHttpClient httpClient) {
		this.config = config;
		this.signature = new NewpaySignature(this.config);
		this.httpClient = httpClient;
	}

	/**
	 * Create payment request for payment gateway.
	 * 
	 * @param model the payment model
	 * @return NewpayPayRequest
	 */
	public NewpayPayRequest createPayRequest(NewpayPayModel model) {
		NewpayPayRequest request = new NewpayPayRequest();
		request.setModel(model);
		// generic
		request.setPartnerId(config.getPartnerId());
		request.setNotifyUrl(config.getNotifyUrl());
		request.setReturnUrl(config.getReturnUrl());
		request.setSignature(signature);
		return request.build();
	}

	/**
	 * Parse payment response from payment gateway and verify RSA signature.
	 * 
	 * @param data the payment response data
	 * @return NewpayPayResponse
	 * @throws NewpayException if failed to parse or RSA signature verification failure
	 * 
	 */
	public NewpayPayResponse parsePayResponse(Map<String, String> data) {
		NewpayPayResponse response = NewpayPayResponse.parse(data);
		response.setSignature(signature);
		response.verifySignature();
		return response;
	}

	/**
	 * Execute refund request and got refund response. (need to wait for async server notification to confirm refund
	 * result)
	 * 
	 * @param model the refund model
	 * @return NewpayRefundResponse
	 */
	public NewpayRefundResponse executeRefundRequest(NewpayRefundModel model) {
		NewpayRefundRequest refundRequest = new NewpayRefundRequest();
		refundRequest.setModel(model);
		// generic
		refundRequest.setPartnerId(config.getPartnerId());
		refundRequest.setNotifyUrl(config.getNotifyUrl());
		refundRequest.setRemark(model.getRefundReason());
		if (model.isPartialRefund()) {
			refundRequest.setRefundType(NewpayRefundType.PARTIAL);
		}
		refundRequest.setSignature(signature);
		// use defaults
		// refundRequest.setDestType(destType);
		LOG.debug("refundRequest: {}", refundRequest);
		Request request = new Request.Builder().url(REFUND_URL).post(refundRequest.build()).build();
		try {
			Response response = httpClient.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new NewpayException(String.format("Refund request HTTP code: %s, message: %s", response.code(),
						response.body().string()));
			}
			Map<String, String> result = NewpayMessage.convertQueryString(response.body().string());
			return NewpayRefundResponse.parse(result);
		}
		catch (Exception e) {
			if (NewpayException.class.isAssignableFrom(e.getClass())) {
				throw (NewpayException) e;
			}
			throw new NewpayException(
					String.format("Failed to execute refund request: %s, cause: %s", refundRequest, e.getMessage()), e);
		}
	}
}
