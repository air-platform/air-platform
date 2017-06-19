package net.aircommunity.platform.model.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.OrderAdapter;

/**
 * Instalment info of an {@code InstalmentOrder}. (NOT USED ATM) TODO, Course need instalments.
 * 
 * @author Bin.Zhang
 */
// @Entity
@Table(name = "air_platfrom_order_instalment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Instalment extends Persistable {
	private static final long serialVersionUID = 1L;

	// typical stages, but NOT limited to
	public static final int STAGE_INITAIL = 1;
	public static final int STAGE_MIDDLE = 2;
	public static final int STAGE_END = 3;

	// Consecutive positive int (1,2,3,4...)
	@Column(name = "stage", nullable = false)
	private int stage = 0;

	// amount defined by of this installment for an order (used to generate paymentRequest as a total amount)
	// we need to check this amount MATCHES the payment amount when payment system notification arrives
	// otherwise consider the payment as failure
	@Column(name = "amount", nullable = false)
	private BigDecimal amount = BigDecimal.ZERO;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	// TODO and also Refund refund ?

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	@XmlJavaTypeAdapter(OrderAdapter.class)
	private InstalmentOrder order;

	public boolean isPaid() {
		return payment != null;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public InstalmentOrder getOrder() {
		return order;
	}

	public void setOrder(InstalmentOrder order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + stage;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instalment other = (Instalment) obj;
		if (stage != other.stage)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Instalment [stage=").append(stage).append(", amount=").append(amount).append(", id=").append(id)
				.append("]");
		return builder.toString();
	}

}
