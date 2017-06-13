package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Refund request.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class RefundRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	// private String orderNo;

	private String refundReason;

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

}
