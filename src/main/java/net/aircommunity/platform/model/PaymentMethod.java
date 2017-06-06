package net.aircommunity.platform.model;

import javax.persistence.Column;

/**
 * Payment Method (Alipay, Wechat, NewPay etc.) of platform. TODO
 * 
 * @author Bin.Zhang
 */
public class PaymentMethod extends Persistable {
	private static final long serialVersionUID = 1L;

	// private String type; // alipay, wechat

	// alipay, wechat
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	// private String appId;
	// private String sellerId;

}
