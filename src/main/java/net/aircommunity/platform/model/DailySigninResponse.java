package net.aircommunity.platform.model;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * User daily signin response.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class DailySigninResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	// consecutive signin count
	private final int consecutiveSignins;

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private final Date lastSigninDate;

	// points earned for this signin
	private final long pointsEarned;

	// whether signin success or not (false, means already signin today)
	private final boolean success;

	public DailySigninResponse(DailySignin dailySignin) {
		this.consecutiveSignins = dailySignin.getConsecutiveSignins();
		this.lastSigninDate = dailySignin.getLastSigninDate();
		this.pointsEarned = dailySignin.getPointsEarned();
		this.success = dailySignin.isSuccess();
	}

	public int getConsecutiveSignins() {
		return consecutiveSignins;
	}

	public Date getLastSigninDate() {
		return lastSigninDate;
	}

	public long getPointsEarned() {
		return pointsEarned;
	}

	public boolean isSuccess() {
		return success;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DailySigninResponse [consecutiveSignins=").append(consecutiveSignins)
				.append(", lastSigninDate=").append(lastSigninDate).append(", pointsEarned=").append(pointsEarned)
				.append(", success=").append(success).append("]");
		return builder.toString();
	}
}
