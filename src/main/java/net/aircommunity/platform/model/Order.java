package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Order extends Persistable {
	private static final long serialVersionUID = 1L;

	// Order Number, TODO a reasonable order number generation
	@Column(name = "order_no", nullable = false, unique = true)
	protected String orderNo;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	// customer contact information for this order
	@Embedded
	protected Contact contact;

	// customer extra information for an order
	@Lob
	@Column(name = "note")
	protected String note;

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

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Order status
	 */
	public enum Status {
		PUBLISHED, PENDING, FINISHED, CANCELLED
	}
}
