package net.aircommunity.platform.service.internal;

import static net.aircommunity.platform.model.PointRules.DEFAULT_ACCOUNT_REGISTRATION_POINTS;
import static net.aircommunity.platform.model.PointRules.DEFAULT_EXCHANGE_PERCENT;
import static net.aircommunity.platform.model.PointRules.DEFAULT_EXCHANGE_RATE;
import static net.aircommunity.platform.model.PointRules.DEFAULT_FIRST_ORDER_PRICE_OFF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import net.aircommunity.platform.model.PointRule;
import net.aircommunity.platform.model.PointRules;
import net.aircommunity.platform.model.PointsExchange;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.Setting;
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
		ORDER_FINISHED_POINTS = ImmutableMap.<Category, Integer>builder()
			.put(Category.AIR_JET, 5)		// 5% of order
			.put(Category.AIR_TRAINING, 5)	// 5% of order
			.put(Category.AIR_TAXI, 10)		// 10% of order
			.put(Category.AIR_TRANS, 10)	// 10% of order
			.put(Category.AIR_TOUR, 10)		// 10% of order
			.put(Category.AIR_QUICKFLIGHT, 10)		// 10% of order
			.build();
		
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
		Setting settings = settingRepository.findByName(POINTS_INITIALIZED);
		return settings != null && Boolean.valueOf(settings.getValue());
	}

	private void pointRulesInitialized() {
		Setting setting = settingRepository.findByName(POINTS_INITIALIZED);
		if (setting == null) {
			setting = Setting.newSetting(POINT_SETTING_CATEGORY);
			setting.setName(POINTS_INITIALIZED);
		}
		setting.setValue(Boolean.TRUE.toString());
		settingRepository.save(setting);
	}

	@Transactional
	@Override
	public void setPointsExchangeRate(int percent) {
		Setting setting = settingRepository.findByName(POINTS_EXCHANGE_RATE);
		if (setting == null) {
			setting = Setting.newSetting(POINT_SETTING_CATEGORY);
			setting.setName(POINTS_EXCHANGE_RATE);
		}
		LOG.debug("Set name: {}, value: {}", setting.getName(), percent);
		setting.setValue(String.valueOf(percent));
		settingRepository.save(setting);
	}

	@Override
	public int getPointsExchangeRate() {
		Setting settings = settingRepository.findByName(POINTS_EXCHANGE_RATE);
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
		Setting setting = settingRepository.findByName(POINTS_EXCHANGE_PERCENT);
		if (setting == null) {
			setting = Setting.newSetting(POINT_SETTING_CATEGORY);
			setting.setName(POINTS_EXCHANGE_PERCENT);
		}
		LOG.debug("Set name: {}, value: {}", setting.getName(), percent);
		setting.setValue(String.valueOf(percent));
		settingRepository.save(setting);
	}

	@Override
	public int getPointsExchangePercent() {
		Setting settings = settingRepository.findByName(POINTS_EXCHANGE_PERCENT);
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
		Setting setting = settingRepository.findByName(pointRule);
		if (setting == null) {
			setting = Setting.newSetting(POINT_RULES_SETTING_CATEGORY);
			setting.setName(pointRule);
		}
		LOG.debug("Set point earning setting with name: {}, value: {}", pointRule, points);
		setting.setValue(String.valueOf(points));
		settingRepository.save(setting);
	}

	@Override
	public Set<PointRule> getEarnPointRules() {
		List<Setting> settings = settingRepository.findByCategory(POINT_RULES_SETTING_CATEGORY);
		return settings.stream().map(setting -> new PointRule(setting.getName(), Long.valueOf(setting.getValue())))
				.collect(Collectors.toSet());
	}

	@Transactional
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_DAILY_SIGNIN })
	@Override
	public Set<PointRule> setEarnPointRules(Set<PointRule> rules) {
		if (rules == null || rules.isEmpty()) {
			return Collections.emptySet();
		}
		List<Setting> settings = new ArrayList<>();
		rules.stream().forEach(rule -> {
			Setting setting = settingRepository.findByName(rule.getName());
			if (setting == null) {
				setting = Setting.newSetting(POINT_RULES_SETTING_CATEGORY);
				setting.setName(rule.getName());
			}
			LOG.debug("Set point earning rule: {}", rule);
			setting.setValue(String.valueOf(rule.getValue()));
			settings.add(setting);
		});
		List<Setting> settingsSaved = settingRepository.save(settings);
		return settingsSaved.stream().map(setting -> new PointRule(setting.getName(), Long.valueOf(setting.getValue())))
				.collect(Collectors.toSet());
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public long getPointsEarnedFromRule(String pointRule) {
		Setting settings = settingRepository.findByName(pointRule);
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
		List<Setting> settings = settingRepository.findByNameStartingWith(PointRules.DAILY_SIGNIN_PREFIX);
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
