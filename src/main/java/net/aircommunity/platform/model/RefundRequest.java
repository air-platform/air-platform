package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Refund request from client.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public final class RefundRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String refundReason;

	public String getRefundReason() {
		return refundReason;
	}

}
