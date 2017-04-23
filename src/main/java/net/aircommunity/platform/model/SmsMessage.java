package net.aircommunity.platform.model;

import java.io.Serializable;

/**
 * Created by kongxiangwen on 2017/4/11.
 */
public class SmsMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private String msgtype;

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SmsMessage [msgtype=").append(msgtype).append("]");
		return builder.toString();
	}
}
