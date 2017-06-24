package net.aircommunity.platform.common.ua;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.micro.common.io.MoreFiles;

/**
 * Java implementation of <a href="https://github.com/ua-parser/uap-java">UA Parser</a>
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class Parser {
	private static final int CACHE_SIZE = 100;
	private static final String REGEX_YAML_PATH = "ua-regexes.yaml";
	private static final String CONF_USER_AGENT_PARSERS = "user_agent_parsers";
	private static final String CONF_OS_PARSERS = "os_parsers";
	private static final String CONF_DEVICE_PARSERS = "device_parsers";
	private final Cache<String, Client> cacheClient = CacheBuilder.newBuilder().initialCapacity(CACHE_SIZE).build();
	private final Cache<String, UserAgent> cacheUA = CacheBuilder.newBuilder().initialCapacity(CACHE_SIZE).build();
	private final Cache<String, Device> cacheDevice = CacheBuilder.newBuilder().initialCapacity(CACHE_SIZE).build();
	private final Cache<String, OS> cacheOS = CacheBuilder.newBuilder().initialCapacity(CACHE_SIZE).build();
	private final UserAgentParser uaParser;
	private final OSParser osParser;
	private final DeviceParser deviceParser;

	public static Parser newParser() {
		try {
			return newParser(MoreFiles.toReader(REGEX_YAML_PATH));
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Failed to initialize UA Parser:" + e.getMessage(), e);
		}
	}

	public static Parser newParser(Reader config) {
		return new Parser(config);
	}

	@SuppressWarnings("unchecked")
	private Parser(Reader reader) {
		Yaml yaml = new Yaml(new SafeConstructor());
		Map<String, List<Map<String, String>>> config = (Map<String, List<Map<String, String>>>) yaml.load(reader);
		// UA
		List<Map<String, String>> uaParserConfigs = config.get(CONF_USER_AGENT_PARSERS);
		uaParser = UserAgentParser.fromList(Objects.requireNonNull(uaParserConfigs, CONF_USER_AGENT_PARSERS));

		// OS
		List<Map<String, String>> osParserConfigs = config.get(CONF_OS_PARSERS);
		osParser = OSParser.fromList(Objects.requireNonNull(osParserConfigs, CONF_OS_PARSERS));

		// Device
		List<Map<String, String>> deviceParserConfigs = config.get(CONF_DEVICE_PARSERS);
		deviceParser = DeviceParser.fromList(Objects.requireNonNull(deviceParserConfigs, CONF_DEVICE_PARSERS));
	}

	public Client parse(String agentString) {
		if (agentString == null) {
			return null;
		}
		Client client = cacheClient.getIfPresent(agentString);
		if (client != null) {
			return client;
		}
		UserAgent ua = parseUserAgent(agentString);
		OS os = parseOS(agentString);
		Device device = parseDevice(agentString);
		client = new Client(ua, os, device);
		cacheClient.put(agentString, client);
		return client;
	}

	public UserAgent parseUserAgent(String agentString) {
		if (agentString == null) {
			return null;
		}
		UserAgent userAgent = cacheUA.getIfPresent(agentString);
		if (userAgent != null) {
			return userAgent;
		}
		userAgent = uaParser.parse(agentString);
		cacheUA.put(agentString, userAgent);
		return userAgent;
	}

	public Device parseDevice(String agentString) {
		if (agentString == null) {
			return null;
		}
		Device device = cacheDevice.getIfPresent(agentString);
		if (device != null) {
			return device;
		}
		device = deviceParser.parse(agentString);
		cacheDevice.put(agentString, device);
		return device;
	}

	public OS parseOS(String agentString) {
		if (agentString == null) {
			return null;
		}
		OS os = cacheOS.getIfPresent(agentString);
		if (os != null) {
			return os;
		}
		os = osParser.parse(agentString);
		cacheOS.put(agentString, os);
		return os;
	}

}
