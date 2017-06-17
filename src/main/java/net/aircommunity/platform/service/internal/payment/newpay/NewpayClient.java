package net.aircommunity.platform.service.internal.payment.newpay;

/**
 * Newpay Gateway client.
 * 
 * @author Bin.Zhang
 */
public class NewpayClient {

	private final NewpayConfig config;
	private final NewpaySignature signature;

	public NewpayClient(NewpayConfig configuration) {
		config = configuration;
		signature = new NewpaySignature(config);
	}

	public NewpayPayRequest createPayRequest(NewpayPayModel model) {
		return NewpayPayRequest.builder((msg, charset) -> signature.signWithRSA(msg, charset))
				.notifyUrl(config.getNotifyUrl()).returnUrl(config.getReturnUrl()).partnerId(config.getPartnerId())
				.model(model).build();
	}

	public static void main(String[] args) {
		String serialID = "dde03550527111e7d2395f079d75b256";
		String submitTime = "20170616165746";
		System.out.println(serialID);
		System.out.println(submitTime);
		int totalAmount = 100;
		String returnUrl = "http://localhost/returnUrl.jsp";
		String notifyUrl = "http://localhost/noticeUrl.jsp";
		String partnerId = "11000002981";
		String orderNo = "0123456789";

		// int totalAmount = 100;
		// TradePayModel model = new TradePayModel();
		// model.setBody("测试机票A");
		// model.setOrderNo(orderNo);
		// model.setParterName("");
		// model.setQuantity(1);
		// model.setTotalAmount(totalAmount);
		// TradePayRequest req =
		// TradePayRequest.builder().notifyUrl(notifyUrl).returnUrl(returnUrl).partnerId(partnerId)
		// .model(model).build();
		// String json = ObjectMappers.createObjectMapper().writeValueAsString(req);
		//
		// System.out.println(req.sign);
		// System.out.println(json);
	}

}
