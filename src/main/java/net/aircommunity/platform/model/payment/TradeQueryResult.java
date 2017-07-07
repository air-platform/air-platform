package net.aircommunity.platform.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.model.domain.Trade;

/**
 * The query result(payment or refund) of a trade.
 * 
 * @author Bin.Zhang
 */
public class TradeQueryResult implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private String tradeNo;
	private String orderNo;
	private String requestNo;
	private BigDecimal amount;
	private Trade.Type type;
	private Trade.Method method;
	// not always available (if N/A just use current timestamp)
	private Date timestamp;
	private String note;

	public String getTradeNo() {
		return tradeNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Trade.Type getTradeType() {
		return type;
	}

	public Trade.Method getTradeMethod() {
		return method;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getNote() {
		return note;
	}

	public Refund createRefund() {
		Refund refund = new Refund();
		refund.setAmount(amount);
		refund.setMethod(method);
		refund.setOrderNo(orderNo);
		refund.setRequestNo(requestNo);
		refund.setTimestamp(timestamp);
		refund.setTradeNo(tradeNo);
		refund.setNote(note);
		return refund;
	}

	public Payment createPayment() {
		Payment payment = new Payment();
		payment.setAmount(amount);
		payment.setMethod(method);
		payment.setOrderNo(orderNo);
		payment.setRequestNo(requestNo);
		payment.setTimestamp(timestamp);
		payment.setTradeNo(tradeNo);
		payment.setNote(note);
		return payment;
	}

	@Override
	public TradeQueryResult clone() {
		try {
			return (TradeQueryResult) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeQueryResult [tradeNo=").append(tradeNo).append(", orderNo=").append(orderNo)
				.append(", requestNo=").append(requestNo).append(", amount=").append(amount).append(", type=")
				.append(type).append(", method=").append(method).append(", timestamp=").append(timestamp)
				.append(", note=").append(note).append("]");
		return builder.toString();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final TradeQueryResult info;

		public Builder() {
			info = new TradeQueryResult();
		}

		public Builder setTradeNo(String tradeNo) {
			info.tradeNo = tradeNo;
			return this;
		}

		public Builder setOrderNo(String orderNo) {
			info.orderNo = orderNo;
			return this;
		}

		public Builder setRequestNo(String requestNo) {
			info.requestNo = requestNo;
			return this;
		}

		public Builder setAmount(BigDecimal amount) {
			info.amount = amount;
			return this;
		}

		public Builder setTradeType(Trade.Type type) {
			info.type = type;
			return this;
		}

		public Builder setTradeMethod(Trade.Method method) {
			info.method = method;
			return this;
		}

		public Builder setTimestamp(Date timestamp) {
			info.timestamp = timestamp;
			return this;
		}

		public Builder setNote(String note) {
			info.note = note;
			return this;
		}

		public TradeQueryResult build() {
			return info.clone();
		}
	}

	// public enum Status {
	// NOT_FOUND, SUCCESS;
	// }

}
