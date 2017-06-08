package net.aircommunity.platform.model;

import java.io.Serializable;

/**
 * Points exchange.
 * 
 * @author Bin.Zhang
 */
public class PointsExchange implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final PointsExchange NONE = new PointsExchange(0, 0);

	private final long money;
	private final long pointsExchanged;

	public PointsExchange(long money, long pointsExchanged) {
		this.money = money;
		this.pointsExchanged = pointsExchanged;
	}

	public long getMoney() {
		return money;
	}

	public long getPointsExchanged() {
		return pointsExchanged;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PointsExchange [money=").append(money).append(", pointsExchanged=").append(pointsExchanged)
				.append("]");
		return builder.toString();
	}
}
