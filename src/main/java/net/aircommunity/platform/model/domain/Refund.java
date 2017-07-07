package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Refund result info of an order.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_order_refund", indexes = { //
		@Index(name = "idx_method_trade_no", columnList = "trade_no,method", unique = true),
		@Index(name = "idx_method_order_no", columnList = "order_no,method", unique = true),
		@Index(name = "idx_user_id_method", columnList = "user_id,method,timestamp"),
		@Index(name = "idx_tenant_id_method", columnList = "tenant_id,method,timestamp") //
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Refund extends Trade {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "refund_reason", nullable = false)
	private String refundReason;

	// refund result note
	@NotNull
	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "refund_result")
	private String refundResult;

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public String getRefundResult() {
		return refundResult;
	}

	public void setRefundResult(String refundResult) {
		this.refundResult = refundResult;
	}

	public void update(Refund another) {
		if (another != null) {
			super.update(another);
			refundReason = another.refundReason;
			refundResult = another.refundResult;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Refund [id=").append(id).append(", tradeNo=").append(tradeNo).append(", orderNo=")
				.append(orderNo).append(", requestNo=").append(requestNo).append(", refundReason=").append(refundReason)
				.append(", refundResult=").append(refundResult).append(", amount=").append(amount).append(", method=")
				.append(method).append(", status=").append(status).append(", timestamp=").append(timestamp).append("]");
		return builder.toString();
	}
}
