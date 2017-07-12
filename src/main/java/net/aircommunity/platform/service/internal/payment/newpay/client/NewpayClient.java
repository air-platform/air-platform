package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.service.internal.payment.newpay.NewpayConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Newpay gateway client (XXX NOTE: NOT an official implementation, there is NO server SDK provided by NEWPAY at the
 * time of implementation ).
 * 
 * @author Bin.Zhang
 */
public class NewpayClient {
	private static final Logger LOG = LoggerFactory.getLogger(NewpayClient.class);

	private final static String REFUND_URL = "https://gateway.hnapay.com/website/refund.htm";
	private final static String QUERY_URL = "https://gateway.hnapay.com/website/queryOrderResult.htm";
	private final NewpayConfig config;
	private final NewpaySignature signature;
	private final OkHttpClient httpClient;

	public NewpayClient(@Nonnull NewpayConfig newpayConfig, @Nonnull OkHttpClient okHttpClient) {
		config = Objects.requireNonNull(newpayConfig, "newpayConfig cannot be null");
		signature = new NewpaySignature(config);
		httpClient = Objects.requireNonNull(okHttpClient, "okHttpClient cannot be null");
	}

	/**
	 * Create pay request.
	 * 
	 * @param model the pay model
	 * @return NewpayPayRequest
	 */
	@Nonnull
	public NewpayPayRequest createPayRequest(@Nonnull NewpayPayModel model) {
		NewpayPayRequest request = new NewpayPayRequest();
		request.setModel(Objects.requireNonNull(model, "payModel cannot be null"));
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
	 * Parse refund response from payment gateway and verify RSA signature.
	 * 
	 * @param data the refund response data
	 * @return NewpayRefundResponse
	 * @throws NewpayException if failed to parse or RSA signature verification failure
	 * 
	 */
	public NewpayRefundResponse parseRefundResponse(Map<String, String> data) {
		NewpayRefundResponse response = NewpayRefundResponse.parse(data);
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
			return parseRefundResponse(result);
		}
		catch (Exception e) {
			if (NewpayException.class.isAssignableFrom(e.getClass())) {
				throw (NewpayException) e;
			}
			throw new NewpayException(
					String.format("Failed to execute refund request: %s, cause: %s", refundRequest, e.getMessage()), e);
		}
	}

	/**
	 * Execute query request.
	 * 
	 * @param model the query model
	 * @return NewpayQueryResponse
	 */
	@Nonnull
	public NewpayQueryResponse executeQueryRequest(@Nonnull NewpayQueryModel model) {
		NewpayQueryRequest queryRequest = new NewpayQueryRequest(model);
		queryRequest.setPartnerId(config.getPartnerId());
		queryRequest.setSignature(signature);
		RequestBody body = queryRequest.build();
		LOG.debug("queryRequest: {}", queryRequest);
		Request request = new Request.Builder().url(QUERY_URL).post(body).build();
		try {
			Response response = httpClient.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new NewpayException(String.format("Query request HTTP code: %s, message: %s", response.code(),
						response.body().string()));
			}
			Map<String, String> data = NewpayMessage.convertQueryString(response.body().string());
			NewpayQueryResponse result = NewpayQueryResponse.parse(data);
			result.setSignature(signature);
			result.verifySignature();
			return result;
		}
		catch (Exception e) {
			if (NewpayException.class.isAssignableFrom(e.getClass())) {
				throw (NewpayException) e;
			}
			throw new NewpayException(
					String.format("Failed to execute query request: %s, cause: %s", queryRequest, e.getMessage()), e);
		}
	}

}
