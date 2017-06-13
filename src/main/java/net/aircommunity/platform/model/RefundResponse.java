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
public final class RefundResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String failureCause;
	private final Refund refund;

	public static RefundResponse success(Refund refund) {
		return new RefundResponse(refund, null);
	}

	public static RefundResponse failure(String failureCause) {
		return new RefundResponse(null, failureCause);
	}

	private RefundResponse(Refund refund, String failureCause) {
		this.refund = refund;
		this.failureCause = failureCause;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public Refund getRefund() {
		return refund;
	}

	public boolean isSuccess() {
		return refund != null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RefundResponse [failureCause=").append(failureCause).append(", refund=").append(refund)
				.append("]");
		return builder.toString();
	}

}
