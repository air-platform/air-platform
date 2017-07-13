package net.aircommunity.platform.service;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.PointRule;
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
	 * Set points exchange rate in percent. The percent of points can be used when place on order. e.g. the
	 * pointsCanBeUsed = percent/100 * totalPriceOfOrder
	 * 
	 * @param percent
	 */
	void setPointsExchangePercent(int percent);

	/**
	 * Returns the points exchange percent.
	 * 
	 * @return the points exchange Percent
	 */
	int getPointsExchangePercent();

	/**
	 * Set a point rule with the given points.
	 * 
	 * @param pointRule point rule name
	 * @param points points user will earned when this rule matches
	 */
	void setEarnPointsForRule(@Nonnull String pointRule, long points);

	@Nonnull
	Set<PointRule> setEarnPointRules(@Nonnull Set<PointRule> rules);

	@Nonnull
	Set<PointRule> getEarnPointRules();

	/**
	 * Get the points from the given rule.
	 * 
	 * @param pointRule point rule name
	 * @return points for this rule
	 */
	long getPointsEarnedFromRule(@Nonnull String pointRule);

	/**
	 * Point rules for daily signin.
	 * 
	 * @return point rules for daily signin or empty if none
	 */
	Map<Integer, Long> getDailySigninPointRules();

	/**
	 * Exchange points for money
	 * 
	 * @param points point to exchange
	 * @return exchanged result
	 */
	@Nonnull
	PointsExchange exchangePoints(long points);

}
