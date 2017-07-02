package net.aircommunity.platform.service.internal;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import net.aircommunity.platform.model.PointRules;
import net.aircommunity.platform.model.PointsExchange;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Settings;
import net.aircommunity.platform.service.MemberPointsService;

/**
 * Member points service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class MemberPointsServiceImpl extends AbstractServiceSupport implements MemberPointsService {
	private static final Logger LOG = LoggerFactory.getLogger(MemberPointsServiceImpl.class);

	private static final String CACHE_NAME = "cache.points";
	private static final String CACHE_NAME_DAILY_SIGNIN = "cache.points_daily_signin";
	private static final int DEFAULT_ACCOUNT_REGISTRATION_POINTS = 100;
	private static final int DEFAULT_EXCHANGE_RATE = 10; // 50%
	private static final int DEFAULT_FIRST_ORDER_PRICE_OFF = 0;
	private static final int DEFAULT_EXCHANGE_PERCENT = 50; // 50%
	private static final String POINTS_INITIALIZED = "initialized";
	// rate of exchange for money
	private static final String POINTS_EXCHANGE_RATE = "exchange_rate";
	// the percent of points can be used when place on order
	// the pointsCanBeUsed = percent/100 * totalPriceOfOrder
	private static final String POINTS_EXCHANGE_PERCENT = "exchange_percent";
	private static final String POINT_SETTING_CATEGORY = "points";
	private static final String POINT_RULES_SETTING_CATEGORY = "points.rule";
	private static Map<Integer, Integer> DAILY_SIGNIN_POINTS;
	private static Map<Category, Integer> ORDER_FINISHED_POINTS;

	@PostConstruct
	private void init() {
		// @formatter:off
		ORDER_FINISHED_POINTS = ImmutableMap.of(
			Category.AIR_JET, 5, 		// 5% of order
			Category.AIR_TRAINING, 5, 	// 5% of order
			Category.AIR_TAXI, 10, 		// 10% of order
			Category.AIR_TRANS, 10, 	// 10% of order
			Category.AIR_TOUR, 10 		// 10% of order
		);
		DAILY_SIGNIN_POINTS = ImmutableMap.of(
			 1, 5, 		// consecutive signins -> 5 pt
			 5, 10, 	// consecutive signins -> 10 pt
			 15, 15, 	// consecutive signins -> 15 pt
			 20, 20, 	// consecutive signins -> 20 pt
			 25, 25 	// consecutive signins -> 25 pt
		);
		// @formatter:on
		if (isPointRulesInitialized()) {
			LOG.warn("Point rules are already initialized, skipped initialization.");
			return;
		}
		// exchange rate
		setPointsExchangeRate(DEFAULT_EXCHANGE_RATE);
		// exchange percent
		setPointsExchangePercent(DEFAULT_EXCHANGE_PERCENT);
		// registration
		setEarnPointsForRule(PointRules.ACCOUNT_REGISTRATION, DEFAULT_ACCOUNT_REGISTRATION_POINTS);
		// first order price off
		setEarnPointsForRule(PointRules.FIRST_ORDER_PRICE_OFF, DEFAULT_FIRST_ORDER_PRICE_OFF);
		// order finished
		Category.ALL.forEach(category -> setEarnPointsForRule(PointRules.getOrderFinishedRule(category),
				ORDER_FINISHED_POINTS.get(category)));
		// daily signin
		DAILY_SIGNIN_POINTS.entrySet().stream()
				.forEach(e -> setEarnPointsForRule(PointRules.getDailySigninRule(e.getKey()), e.getValue()));

		// mark as initialized
		pointRulesInitialized();
	}

	private boolean isPointRulesInitialized() {
		Settings settings = settingsRepository.findByName(POINTS_INITIALIZED);
		return settings != null && Boolean.valueOf(settings.getValue());
	}

	private void pointRulesInitialized() {
		Settings settings = settingsRepository.findByName(POINTS_INITIALIZED);
		if (settings == null) {
			settings = Settings.newSettings(POINT_SETTING_CATEGORY);
			settings.setName(POINTS_INITIALIZED);
		}
		settings.setValue(Boolean.TRUE.toString());
		settingsRepository.save(settings);
	}

	@Transactional
	@Override
	public void setPointsExchangeRate(int percent) {
		Settings settings = settingsRepository.findByName(POINTS_EXCHANGE_RATE);
		if (settings == null) {
			settings = Settings.newSettings(POINT_SETTING_CATEGORY);
			settings.setName(POINTS_EXCHANGE_RATE);
		}
		LOG.debug("Set name: {}, value: {}", settings.getName(), percent);
		settings.setValue(String.valueOf(percent));
		settingsRepository.save(settings);
	}

	@Override
	public int getPointsExchangeRate() {
		Settings settings = settingsRepository.findByName(POINTS_EXCHANGE_RATE);
		if (settings == null) {
			return DEFAULT_EXCHANGE_RATE;
		}
		try {
			return Integer.valueOf(settings.getValue().trim());
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get setting %s, cause:", settings.getName(), e.getMessage()), e);
		}
		return DEFAULT_EXCHANGE_RATE;
	}

	@Transactional
	@Override
	public void setPointsExchangePercent(int percent) {
		Settings settings = settingsRepository.findByName(POINTS_EXCHANGE_PERCENT);
		if (settings == null) {
			settings = Settings.newSettings(POINT_SETTING_CATEGORY);
			settings.setName(POINTS_EXCHANGE_PERCENT);
		}
		LOG.debug("Set name: {}, value: {}", settings.getName(), percent);
		settings.setValue(String.valueOf(percent));
		settingsRepository.save(settings);
	}

	@Override
	public int getPointsExchangePercent() {
		Settings settings = settingsRepository.findByName(POINTS_EXCHANGE_PERCENT);
		if (settings == null) {
			return DEFAULT_EXCHANGE_PERCENT;
		}
		try {
			return Integer.valueOf(settings.getValue().trim());
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get setting %s, cause:", settings.getName(), e.getMessage()), e);
		}
		return DEFAULT_EXCHANGE_PERCENT;
	}

	@Transactional
	@Caching(put = @CachePut(cacheNames = CACHE_NAME, key = "#pointRule"), evict = @CacheEvict(cacheNames = CACHE_NAME_DAILY_SIGNIN))
	@Override
	public void setEarnPointsForRule(String pointRule, long points) {
		Settings settings = settingsRepository.findByName(pointRule);
		if (settings == null) {
			settings = Settings.newSettings(POINT_RULES_SETTING_CATEGORY);
			settings.setName(pointRule);
		}
		LOG.debug("Set point earning setting with name: {}, value: {}", pointRule, points);
		settings.setValue(String.valueOf(points));
		settingsRepository.save(settings);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public long getPointsEarnedFromRule(String pointRule) {
		Settings settings = settingsRepository.findByName(pointRule);
		if (settings == null) {
			return 0;
		}
		try {
			return Long.valueOf(settings.getValue().trim());
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get point rule %s, cause:", pointRule, e.getMessage()), e);
		}
		return 0;
	}

	@Cacheable(cacheNames = CACHE_NAME_DAILY_SIGNIN)
	@Override
	public Map<Integer, Long> getDailySigninPointRules() {
		List<Settings> settings = settingsRepository.findByNameStartingWith(PointRules.DAILY_SIGNIN_PREFIX);
		ImmutableMap.Builder<Integer, Long> signinRules = ImmutableMap.builder();
		settings.stream().forEach(setting -> {
			String signinRule = setting.getName();
			int signinCount = Integer.valueOf(signinRule.replace(PointRules.DAILY_SIGNIN_PREFIX, ""));
			long points = Long.valueOf(setting.getValue());
			signinRules.put(signinCount, points);
		});
		return signinRules.build();
	}

	@Override
	public PointsExchange exchangePoints(long points) {
		if (points <= 0) {
			return PointsExchange.NONE;
		}
		int rate = getPointsExchangeRate();
		if (rate <= 0) {
			return PointsExchange.NONE;
		}
		long money = points / rate;
		long pointsExchanged = money * rate;
		return new PointsExchange(money, pointsExchanged);
	}

}
