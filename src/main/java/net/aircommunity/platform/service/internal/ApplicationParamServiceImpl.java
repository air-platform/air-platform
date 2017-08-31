package net.aircommunity.platform.service.internal;

import javax.annotation.PostConstruct;
import net.aircommunity.platform.model.ApplicationParams;
import net.aircommunity.platform.model.domain.Setting;
import net.aircommunity.platform.service.ApplicationParamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic application param implementation.
 *
 * @author Xiangwen.Kong
 */
@Service
@Transactional(readOnly = true)
public class ApplicationParamServiceImpl extends AbstractServiceSupport implements ApplicationParamService {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationParamServiceImpl.class);

	private static final String CACHE_NAME = "cache.applicationparam";

	private static final String APPLICATION_PARAM_CATEGORY = "application";
	private static final String QUICKFLIGHT_SPEED = "quickflight.speed";
	private static final String QUICKFLIGHT_UNIT_TIME_PRICE = "quickflight.unit_time_price";
	private static final String QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE = "quickflight.departure_time_in_advance";


	private static final String APPLICATION_INITIALIZED = "application_initialized";

	@PostConstruct
	private void init() {
		// @formatter:on
		if (isApplicationParamInitialized()) {
			LOG.warn("Application params are already initialized, skipped initialization.");
			return;
		}
		setQuickflightSpeed(ApplicationParams.DEFAULT_QUICKFLIGHT_SPEED);
		setQuickflightUnitPrice(ApplicationParams.DEFAULT_QUICKFLIGHT_UNIT_TIME_PRICE);
		setQuickflightDepartureTime(ApplicationParams.DEFAULT_QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE);

		// mark as initialized
		applicationParamInitialized();
	}

	private boolean isApplicationParamInitialized() {
		Setting settings = settingRepository.findByName(APPLICATION_INITIALIZED);
		return settings != null && Boolean.valueOf(settings.getValue());
	}

	private void applicationParamInitialized() {
		Setting setting = settingRepository.findByName(APPLICATION_INITIALIZED);
		if (setting == null) {
			setting = Setting.newSetting(APPLICATION_PARAM_CATEGORY);
			setting.setName(APPLICATION_INITIALIZED);
		}
		setting.setValue(Boolean.TRUE.toString());
		settingRepository.save(setting);
	}


	@Override
	public void clearCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.clear();
		}
	}

	@Override
	public void clearAllCaches() {
		cacheManager.getCacheNames().parallelStream().forEach(name -> cacheManager.getCache(name).clear());
	}


	@Transactional
	@Override
	public void setQuickflightSpeed(int speed) {
		Setting setting = settingRepository.findByName(QUICKFLIGHT_SPEED);
		if (setting == null) {
			setting = Setting.newSetting(APPLICATION_PARAM_CATEGORY);
			setting.setName(QUICKFLIGHT_SPEED);
		}
		LOG.debug("Set name: {}, value: {}", setting.getName(), speed);
		setting.setValue(String.valueOf(speed));
		settingRepository.save(setting);
	}

	@Override
	public int getQuickflightSpeed() {
		Setting settings = settingRepository.findByName(QUICKFLIGHT_SPEED);
		if (settings == null) {
			return ApplicationParams.DEFAULT_QUICKFLIGHT_SPEED;
		}
		try {
			return Integer.valueOf(settings.getValue().trim());
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get setting %s, cause:", settings.getName(), e.getMessage()), e);
		}
		return ApplicationParams.DEFAULT_QUICKFLIGHT_SPEED;
	}


	@Transactional
	@Override
	public void setQuickflightUnitPrice(int price) {
		Setting setting = settingRepository.findByName(QUICKFLIGHT_UNIT_TIME_PRICE);
		if (setting == null) {
			setting = Setting.newSetting(APPLICATION_PARAM_CATEGORY);
			setting.setName(QUICKFLIGHT_UNIT_TIME_PRICE);
		}
		LOG.debug("Set name: {}, value: {}", setting.getName(), price);
		setting.setValue(String.valueOf(price));
		settingRepository.save(setting);
	}

	@Override
	public int getQuickflightUnitPrice() {
		Setting settings = settingRepository.findByName(QUICKFLIGHT_UNIT_TIME_PRICE);
		if (settings == null) {
			return ApplicationParams.DEFAULT_QUICKFLIGHT_UNIT_TIME_PRICE;
		}
		try {
			return Integer.valueOf(settings.getValue().trim());
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get setting %s, cause:", settings.getName(), e.getMessage()), e);
		}
		return ApplicationParams.DEFAULT_QUICKFLIGHT_UNIT_TIME_PRICE;
	}


	@Transactional
	@Override
	public void setQuickflightDepartureTime(int time) {
		Setting setting = settingRepository.findByName(QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE);
		if (setting == null) {
			setting = Setting.newSetting(APPLICATION_PARAM_CATEGORY);
			setting.setName(QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE);
		}
		LOG.debug("Set name: {}, value: {}", setting.getName(), time);
		setting.setValue(String.valueOf(time));
		settingRepository.save(setting);
	}

	@Override
	public int getQuickflightDepartureTime() {
		Setting settings = settingRepository.findByName(QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE);
		if (settings == null) {
			return ApplicationParams.DEFAULT_QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE;
		}
		try {
			return Integer.valueOf(settings.getValue().trim());
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get setting %s, cause:", settings.getName(), e.getMessage()), e);
		}
		return ApplicationParams.DEFAULT_QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE;
	}


}
