package net.aircommunity.platform.service.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.domain.Contact;
import net.aircommunity.platform.model.domain.Settings;
import net.aircommunity.platform.service.PlatformService;

/**
 * Generic platform service implementation for platform administrators.
 * 
 * @author Bin.Zhang
 */
@Service
public class PlatformServiceImpl extends AbstractServiceSupport implements PlatformService {
	private static final Logger LOG = LoggerFactory.getLogger(PlatformServiceImpl.class);

	private static final String PLATFORM_CLIENT_MANAGERS = "platform.client_managers";

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

	@Override
	public void setPlatformClientManagers(Set<Contact> clientManagers) {
		if (clientManagers == null || clientManagers.isEmpty()) {
			return;
		}
		String contacts = clientManagers.stream().map(e -> new StringBuilder().append(e.getPerson())
				.append(Constants.CONTACT_INFO_SEPARATOR).append(e.getEmail()).toString())
				.collect(Collectors.joining(Constants.CONTACT_SEPARATOR));
		Settings settings = settingsRepository.findByName(PLATFORM_CLIENT_MANAGERS);
		if (settings == null) {
			settings = Settings.newSystemSettings();
			settings.setName(PLATFORM_CLIENT_MANAGERS);
		}
		settings.setValue(contacts);
		LOG.debug("Set setting with name: {}, value: {}", PLATFORM_CLIENT_MANAGERS, contacts);
		settingsRepository.save(settings);
	}

	@Override
	public Set<Contact> getPlatformClientManagers() {
		Settings settings = settingsRepository.findByName(PLATFORM_CLIENT_MANAGERS);
		if (settings == null) {
			return Collections.emptySet();
		}
		return Contact.parseContacts(settings.getValue());
	}

}
