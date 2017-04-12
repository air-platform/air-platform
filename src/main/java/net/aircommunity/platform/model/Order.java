package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Base Order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Order extends Persistable {
	private static final long serialVersionUID = 1L;

	// Order Number
	@Column(name = "order_no", nullable = false, unique = true)
	protected String orderNo;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	protected Date creationDate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "payment_date", nullable = false)
	protected Date paymentDate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "finished_date", nullable = false)
	protected Date finishedDate;

	// customer extra information for an order
	@Lob
	@Column(name = "note")
	protected String note;

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	protected User owner;

	@PrePersist
	private void generateOrderNo() {
		if (orderNo == null) {
			// TODO a reasonable order number generation
			// orderNo = "";
		}
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * Order status
	 */
	public enum Status {
		PUBLISHED, PENDING, FINISHED, CANCELLED
	}
}
