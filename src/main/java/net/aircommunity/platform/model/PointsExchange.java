package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Points exchange.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class PointsExchange implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final PointsExchange NONE = new PointsExchange(0, 0);

	private final long moneyExchanged;
	private final long pointsExchanged;

	public PointsExchange(long moneyExchanged, long pointsExchanged) {
		this.moneyExchanged = moneyExchanged;
		this.pointsExchanged = pointsExchanged;
	}

	public long getMoneyExchanged() {
		return moneyExchanged;
	}

	public long getPointsExchanged() {
		return pointsExchanged;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PointsExchange [moneyExchanged=").append(moneyExchanged).append(", pointsExchanged=")
				.append(pointsExchanged).append("]");
		return builder.toString();
	}
}
