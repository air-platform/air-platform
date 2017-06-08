package net.aircommunity.platform.service.internal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.PointsExchange;
import net.aircommunity.platform.model.Settings;
import net.aircommunity.platform.repository.SettingsRepository;
import net.aircommunity.platform.service.MemberPointsService;

/**
 * Member points service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class MemberPointsServiceImpl implements MemberPointsService {
	private static final Logger LOG = LoggerFactory.getLogger(MemberPointsServiceImpl.class);

	private static final int DEFAULT_EXCHANGE_RATE = 10;
	private static final String POINTS_EXCHANGE_RATE = "points.exchange_rate";
	private static final String POINTS_RULE_FORMAT = "points.rule.%s";

	@Resource
	private SettingsRepository settingsRepository;

	@PostConstruct
	private void init() {
		setPointsExchangeRate(DEFAULT_EXCHANGE_RATE);
		setEarnPointsForRule(Constants.POINTS_RULE_ACCOUNT_REGISTRATION, 0);
		setEarnPointsForRule(Constants.POINTS_RULE_ORDER_FINISHED, 0);
	}

	@Override
	public void setPointsExchangeRate(int percent) {
		Settings settings = settingsRepository.findByName(POINTS_EXCHANGE_RATE);
		if (settings == null) {
			settings = Settings.newUserSettings();
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

	@Override
	public void setEarnPointsForRule(String pointRule, long points) {
		String ruleName = String.format(POINTS_RULE_FORMAT, pointRule);
		Settings settings = settingsRepository.findByName(ruleName);
		if (settings == null) {
			settings = Settings.newUserSettings();
			settings.setName(ruleName);
		}
		LOG.debug("Set point earning setting with name: {}, value: {}", ruleName, points);
		settings.setValue(String.valueOf(points));
		settingsRepository.save(settings);
	}

	@Override
	public long getPointsEarnedFromRule(String pointRule) {
		String ruleName = String.format(POINTS_RULE_FORMAT, pointRule);
		Settings settings = settingsRepository.findByName(ruleName);
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
