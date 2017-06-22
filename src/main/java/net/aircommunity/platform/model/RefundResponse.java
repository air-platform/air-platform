package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import net.aircommunity.platform.model.domain.Refund;

/**
 * Refund request.
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class RefundResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int SUCCESS = 0;
	// accept but waiting for confirmation
	public static final int PENDING = 1;
	public static final int FAILURE = 2;

	private final String failureCause;
	private final Refund refund;
	private final int code;

	public static RefundResponse success(Refund refund) {
		return new RefundResponse(SUCCESS, refund, null);
	}

	public static RefundResponse pending() {
		return new RefundResponse(PENDING, null, null);
	}

	public static RefundResponse failure(String failureCause) {
		return new RefundResponse(FAILURE, null, failureCause);
	}

	private RefundResponse(int code, Refund refund, String failureCause) {
		this.code = code;
		this.refund = refund;
		this.failureCause = failureCause;
	}

	public int getCode() {
		return code;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public Refund getRefund() {
		return refund;
	}

	public boolean isSuccessful() {
		return SUCCESS == code;
	}

	public boolean isPending() {
		return PENDING == code;
	}

	public boolean isFailed() {
		return FAILURE == code;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RefundResponse [failureCause=").append(failureCause).append(", refund=").append(refund)
				.append("]");
		return builder.toString();
	}

}
