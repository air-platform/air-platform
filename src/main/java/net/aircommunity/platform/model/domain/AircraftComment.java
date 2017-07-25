package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.CommenterAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Comment of an Aircraft.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_aircraft_comment", indexes = {
		@Index(name = "idx_aircraft_id_date", columnList = "aircraft_id,date")//
})
@XmlAccessorType(XmlAccessType.FIELD)
public class AircraftComment extends Persistable {
	private static final long serialVersionUID = 1L;

	// user can only rate once per product per order
	// 0 - 5
	@Min(0)
	@Max(5)
	@Column(name = "rate", nullable = false)
	private int rate = 0;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date date;

	@Lob
	@Column(name = "content")
	private String content;

	// make comment on a aircraft
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "aircraft_id", nullable = false)
	private Aircraft aircraft;

	// owner
	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	@XmlJavaTypeAdapter(CommenterAdapter.class)
	private Account owner;

	// reply to
	@ManyToOne
	@JoinColumn(name = "reply_to_id", nullable = true)
	@XmlJavaTypeAdapter(CommenterAdapter.class)
	private Account replyTo;

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Aircraft getAircraft() {
		return aircraft;
	}

	public void setAircraft(Aircraft aircraft) {
		this.aircraft = aircraft;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public Account getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(Account replyTo) {
		this.replyTo = replyTo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AircraftComment [id=").append(id).append(", rate=").append(rate).append(", date=").append(date)
				.append(", content=").append(content).append("]");
		return builder.toString();
	}
}
