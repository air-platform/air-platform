package net.aircommunity.platform.service;

/**
 * Generic application params.
 *
 * @author Xiangwen.Kong
 */
public interface ApplicationParamService {

	/**
	 * Clear specific cache.
	 *
	 * @param cacheName the cache name
	 */
	void clearCache(String cacheName);

	/**
	 * Clear all caches.
	 */
	void clearAllCaches();


	void setQuickflightSpeed(int speed);

	int getQuickflightSpeed();

	void setQuickflightUnitPrice(int price);

	public int getQuickflightUnitPrice();

	void setQuickflightDepartureTime(int time);

	int getQuickflightDepartureTime();

}
