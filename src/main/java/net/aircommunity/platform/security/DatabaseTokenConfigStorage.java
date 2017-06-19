package net.aircommunity.platform.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micro.core.security.AccessTokenConfig;
import io.micro.core.security.AccessTokenConfigStorage;
import net.aircommunity.platform.model.domain.Settings;
import net.aircommunity.platform.repository.SettingsRepository;

/**
 * Database AccessTokenConfigStorage
 * 
 * @author Bin.Zhang
 */
public class DatabaseTokenConfigStorage implements AccessTokenConfigStorage {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseTokenConfigStorage.class);

	private final SettingsRepository settingsRepository;
	private final ObjectMapper objectMapper;

	public DatabaseTokenConfigStorage(SettingsRepository settingsRepository, ObjectMapper objectMapper) {
		this.settingsRepository = settingsRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	public AccessTokenConfig loadAccessTokenConfig(String keyId) {
		Settings settings = settingsRepository.findByName(keyId);
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
			Settings settings = settingsRepository.findByName(keyId);
			if (settings == null) {
				settings = Settings.newSystemSettings();
			}
			String body = objectMapper.writeValueAsString(config);
			settings.setName(keyId);
			settings.setValue(body);
			settingsRepository.save(settings);
		}
		catch (Exception e) {
			LOG.error("Failed to load saveAccessTokenConfig", e);
		}
	}

}
