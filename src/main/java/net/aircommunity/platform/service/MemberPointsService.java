package net.aircommunity.platform.service;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.PointsExchange;

/**
 * Member points service.
 * 
 * @author Bin.Zhang
 */
public interface MemberPointsService {

	/**
	 * Set points exchange rate in percent.
	 * 
	 * @param percent
	 */
	void setPointsExchangeRate(int percent);

	/**
	 * Returns the points exchange rate.
	 * 
	 * @return the points exchange rate
	 */
	int getPointsExchangeRate();

	/**
	 * Set a point rule with the given points.
	 * 
	 * @param pointRule point rule name
	 * @param points points user will earned when this rule matches
	 */
	void setEarnPointsForRule(@Nonnull String pointRule, long points);

	/**
	 * Get the points from the given rule.
	 * 
	 * @param pointRule point rule name
	 * @return points for this rule
	 */
	long getPointsEarnedFromRule(@Nonnull String pointRule);

	/**
	 * Exchange points for money
	 * 
	 * @param points point to exchange
	 * @return exchanged result
	 */
	@Nonnull
	PointsExchange exchangePoints(long points);

}
