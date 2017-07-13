package net.aircommunity.platform.service.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.domain.Contact;
import net.aircommunity.platform.model.domain.Setting;
import net.aircommunity.platform.service.PlatformService;

/**
 * Generic platform service implementation for platform administrators.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
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

	@Transactional
	@Override
	public void setPlatformClientManagers(Set<Contact> clientManagers) {
		if (clientManagers == null || clientManagers.isEmpty()) {
			return;
		}
		String contacts = clientManagers.stream().map(e -> new StringBuilder().append(e.getPerson())
				.append(Constants.CONTACT_INFO_SEPARATOR).append(e.getEmail()).toString())
				.collect(Collectors.joining(Constants.CONTACT_SEPARATOR));
		Setting setting = settingRepository.findByName(PLATFORM_CLIENT_MANAGERS);
		if (setting == null) {
			setting = Setting.newSystemSetting();
			setting.setName(PLATFORM_CLIENT_MANAGERS);
		}
		setting.setValue(contacts);
		LOG.debug("Set setting with name: {}, value: {}", PLATFORM_CLIENT_MANAGERS, contacts);
		settingRepository.save(setting);
	}

	@Override
	public Set<Contact> getPlatformClientManagers() {
		Setting setting = settingRepository.findByName(PLATFORM_CLIENT_MANAGERS);
		if (setting == null) {
			return Collections.emptySet();
		}
		return Contact.parseContacts(setting.getValue());
	}

}
