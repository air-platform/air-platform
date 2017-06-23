package net.aircommunity.platform.common.ua;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

//import org.apache.commons.collections.map.LRUMap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * When doing webanalytics (with for example PIG) the main pattern is to process weblogs in clickstreams. A basic fact
 * about common clickstreams is that in general the same browser will do multiple requests in sequence. This has the
 * effect that the same useragent will appear in the logfiles and we will see the need to parse the same useragent over
 * and over again.
 *
 * This class introduces a very simple LRU cache to reduce the number of times the parsing is actually done.
 *
 * @author Niels Basjes
 *
 */
public class CachingParser extends Parser {

	// TODO: Make configurable
	private static final int CACHE_SIZE = 1000;

	private Cache<String, Client> cacheClient = CacheBuilder.newBuilder().initialCapacity(CACHE_SIZE).build();
	private Map<String, UserAgent> cacheUserAgent = null;
	private Map<String, Device> cacheDevice = null;
	private Map<String, OS> cacheOS = null;

	// ------------------------------------------

	public CachingParser() throws IOException {
		super();
	}

	public CachingParser(InputStream regexYaml) {
		super(regexYaml);
	}

	// ------------------------------------------

	@Override
	public Client parse(String agentString) {
		if (agentString == null) {
			return null;
		}
		Client client = cacheClient.getIfPresent(agentString);
		if (client != null) {
			return client;
		}
		client = super.parse(agentString);
		cacheClient.put(agentString, client);
		return client;
	}

	// ------------------------------------------

	@Override
	public UserAgent parseUserAgent(String agentString) {
		if (agentString == null) {
			return null;
		}
		UserAgent userAgent = cacheUserAgent.get(agentString);
		if (userAgent != null) {
			return userAgent;
		}
		userAgent = super.parseUserAgent(agentString);
		cacheUserAgent.put(agentString, userAgent);
		return userAgent;
	}

	// ------------------------------------------

	@Override
	public Device parseDevice(String agentString) {
		if (agentString == null) {
			return null;
		}
		Device device = cacheDevice.get(agentString);
		if (device != null) {
			return device;
		}
		device = super.parseDevice(agentString);
		cacheDevice.put(agentString, device);
		return device;
	}

	// ------------------------------------------

	@Override
	public OS parseOS(String agentString) {
		if (agentString == null) {
			return null;
		}
		OS os = cacheOS.get(agentString);
		if (os != null) {
			return os;
		}
		os = super.parseOS(agentString);
		cacheOS.put(agentString, os);
		return os;
	}

	// ------------------------------------------

}
