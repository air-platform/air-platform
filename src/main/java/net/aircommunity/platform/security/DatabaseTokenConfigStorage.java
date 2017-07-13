package net.aircommunity.platform.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micro.core.security.AccessTokenConfig;
import io.micro.core.security.AccessTokenConfigStorage;
import net.aircommunity.platform.model.domain.Setting;
import net.aircommunity.platform.repository.SettingRepository;

/**
 * Database AccessTokenConfigStorage
 * 
 * @author Bin.Zhang
 */
public class DatabaseTokenConfigStorage implements AccessTokenConfigStorage {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseTokenConfigStorage.class);

	private final SettingRepository settingRepository;
	private final ObjectMapper objectMapper;

	public DatabaseTokenConfigStorage(SettingRepository settingRepository, ObjectMapper objectMapper) {
		this.settingRepository = settingRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	public AccessTokenConfig loadAccessTokenConfig(String keyId) {
		Setting settings = settingRepository.findByName(keyId);
		if (settings == null) {
			return null;
		}
		try {
			return objectMapper.readValue(settings.getValue(), AccessTokenConfig.class);
		}
		catch (Exception e) {
			LOG.error("Failed to load AccessTokenConfig:" + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveAccessTokenConfig(String keyId, AccessTokenConfig config) {
		try {
			Setting setting = settingRepository.findByName(keyId);
			if (setting == null) {
				setting = Setting.newSystemSetting();
			}
			String body = objectMapper.writeValueAsString(config);
			setting.setName(keyId);
			setting.setValue(body);
			settingRepository.save(setting);
		}
		catch (Exception e) {
			LOG.error("Failed to load saveAccessTokenConfig", e);
		}
	}

}
