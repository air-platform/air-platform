package net.aircommunity.platform.service.internal.payment.wechat;

/**
 * Trade state (String(32)) of Wxpay
 * 
 * @author Bin.Zhang
 */
public enum WxTradeState {

	/**
	 * 支付成功
	 */
	SUCCESS,

	/**
	 * 转入退款
	 */
	REFUND,

	/**
	 * 未支付
	 */
	NOTPAY,

	/**
	 * 已关闭
	 */
	CLOSED,

	/**
	 * 已撤销（刷卡支付)
	 */
	REVOKED,

	/**
	 * 用户支付中
	 */
	USERPAYING,

	/**
	 * 支付失败(其他原因，如银行返回失败)
	 */
	PAYERROR;

	public static WxTradeState fromString(String state) {
		for (WxTradeState s : values()) {
			if (s.name().equalsIgnoreCase(state)) {
				return s;
			}
		}
		return null;
	}

}
