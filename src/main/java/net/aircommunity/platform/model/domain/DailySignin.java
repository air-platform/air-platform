package net.aircommunity.platform.model.domain;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * User daily signin.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_daily_signin")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("id")
public class DailySignin extends Persistable {
	private static final long serialVersionUID = 1L;

	// consecutive signin count
	@Column(name = "consecutive_signins")
	private int consecutiveSignins;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "last_signin_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date lastSigninDate;

	// points earned for current signin (FIXME JsonView is NOT working here, still show this prop in getSelfAccount)
	@XmlTransient
	@Transient
	private long pointsEarned;

	@XmlTransient
	@Transient
	private boolean success;

	public DailySignin() {
		lastSigninDate = new Date();
		consecutiveSignins = 0;
	}

	public int getConsecutiveSignins() {
		return consecutiveSignins;
	}

	public void setConsecutiveSignins(int consecutiveSignins) {
		this.consecutiveSignins = consecutiveSignins;
	}

	public void increaseConsecutiveSignins() {
		consecutiveSignins = consecutiveSignins + 1;
	}

	public void resetConsecutiveSignins() {
		consecutiveSignins = 1;
	}

	public Date getLastSigninDate() {
		return lastSigninDate;
	}

	public void setLastSigninDate(Date lastSigninDate) {
		this.lastSigninDate = lastSigninDate;
	}

	public long getPointsEarned() {
		return pointsEarned;
	}

	public void setPointsEarned(long pointsEarned) {
		this.pointsEarned = pointsEarned;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * Test if it's already signin today.
	 * 
	 * @return signin status
	 */
	@XmlElement
	public boolean isSignin() {
		LocalDate lastSignin = lastSigninDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.DAYS.between(lastSignin, LocalDate.now()) == 0;
	}

	/**
	 * Test if it's consecutive signin.
	 * 
	 * @return true if consecutive signin
	 */
	@XmlTransient
	public boolean isConsecutive() {
		LocalDate lastSignin = lastSigninDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.DAYS.between(lastSignin, LocalDate.now()) == 1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserSign [consecutiveSignins=").append(consecutiveSignins).append(", lastSigninDate=")
				.append(lastSigninDate).append(", id=").append(id).append("]");
		return builder.toString();
	}
}
