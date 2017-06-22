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

import com.google.common.collect.ImmutableMap;

import net.aircommunity.platform.Constants;
import net.aircommunity.platform.Constants.PointRules;
import net.aircommunity.platform.model.PointsExchange;
import net.aircommunity.platform.model.domain.Settings;
import net.aircommunity.platform.service.MemberPointsService;

/**
 * Member points service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class MemberPointsServiceImpl extends AbstractServiceSupport implements MemberPointsService {
	private static final Logger LOG = LoggerFactory.getLogger(MemberPointsServiceImpl.class);

	private static final String CACHE_NAME = "cache.points";
	private static final String CACHE_NAME_DAILY_SIGNIN = "cache.points_daily_signin";

	private static final int DEFAULT_EXCHANGE_RATE = 10;
	private static final int DEFAULT_FIRST_ORDER_PRICE_OFF = 100;
	private static final String POINTS_EXCHANGE_RATE = "exchange_rate";
	private static final String POINT_SETTING_CATEGORY = "points";
	private static final String POINT_RULES_SETTING_CATEGORY = "points.rule";

	@PostConstruct
	private void init() {
		if (!hasSetting(POINTS_EXCHANGE_RATE)) {
			setPointsExchangeRate(DEFAULT_EXCHANGE_RATE);
		}
		if (!hasSetting(Constants.PointRules.ACCOUNT_REGISTRATION)) {
			setEarnPointsForRule(Constants.PointRules.ACCOUNT_REGISTRATION, 0);
		}
		if (!hasSetting(Constants.PointRules.FIRST_ORDER_PRICE_OFF)) {
			setEarnPointsForRule(Constants.PointRules.FIRST_ORDER_PRICE_OFF, DEFAULT_FIRST_ORDER_PRICE_OFF);
		}
		if (!hasSetting(Constants.PointRules.DAILY_SIGNIN_1)) {
			setEarnPointsForRule(Constants.PointRules.ORDER_FINISHED, 0);
			setEarnPointsForRule(Constants.PointRules.DAILY_SIGNIN_1, 1);
			setEarnPointsForRule(Constants.PointRules.DAILY_SIGNIN_3, 3);
			setEarnPointsForRule(Constants.PointRules.DAILY_SIGNIN_5, 5);
			setEarnPointsForRule(Constants.PointRules.DAILY_SIGNIN_7, 7);
		}
	}

	private boolean hasSetting(String name) {
		return settingsRepository.findByName(name) != null;
	}

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
			return 0;
		}
		try {
			return Integer.valueOf(settings.getValue().trim());
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get setting %s, cause:", settings.getName(), e.getMessage()), e);
		}
		return 0;
	}

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
