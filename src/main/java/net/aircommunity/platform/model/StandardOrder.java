package net.aircommunity.platform.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Standard Order model with a single payment.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class StandardOrder extends Order {
	private static final long serialVersionUID = 1L;

	// single payment
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "payment_id")
	protected Payment payment;

	// single refund
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "refund_id")
	protected Refund refund;

	// Bidirectional
	// @XmlTransient
	// @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval = true)
	// protected List<Payment> payments = new ArrayList<>();

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

}
