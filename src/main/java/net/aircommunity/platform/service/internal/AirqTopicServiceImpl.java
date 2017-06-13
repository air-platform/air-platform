package net.aircommunity.platform.service.internal;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.AirqTopic;
import net.aircommunity.platform.service.AirqTopicService;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AirQ Topic Service implementation
 * 
 * @author luocheng
 */
@Service
public class AirqTopicServiceImpl implements AirqTopicService {
	private static final Logger LOG = LoggerFactory.getLogger(AirqTopicServiceImpl.class);

	private static final String APPLICATION_JSON = "application/json";
	private static final String API_URL_FORMAT = "%s/api/recent";
	private static final String TOPIC_URL_FORMAT = "%s/topic/%d";
	private String apiUrl;

	@Resource
	private Configuration configuration;

	@Resource
	private OkHttpClient httpClient;

	@PostConstruct
	private void init() {
		apiUrl = String.format(API_URL_FORMAT, configuration.getAirqUrl());
	}

	@Override
	public List<AirqTopic> listRecentTopics() {
		LOG.debug("List recet topics url: {}", apiUrl);
		try {
			Request request = new Request.Builder().url(apiUrl).headers(buildHeaders()).get().build();
			Response response = httpClient.newCall(request).execute();
			String body = response.body().string();
			if (response.isSuccessful()) {
				try (JsonReader jsonReader = Json.createReader(new StringReader(body))) {
					JsonObject jsonObj = jsonReader.readObject();
					JsonArray topicsArray = jsonObj.getJsonArray("topics");
					ImmutableList.Builder<AirqTopic> builder = ImmutableList.builder();
					int size = topicsArray.size();
					for (int i = 0; i < size; i++) {
						JsonObject t = topicsArray.getJsonObject(i);
						LOG.debug("Topics: {}", t);
						String category = t.getJsonObject("category").getString("name");
						String title = t.getString("title");
						int tid = t.getInt("tid");
						String url = String.format(TOPIC_URL_FORMAT, configuration.getAirqUrl(), tid);
						builder.add(new AirqTopic(category, title, url));
					}
					return builder.build();
				}
			}
		}
		catch (Exception e) {
			LOG.error("Got error when List recent AirQ topics:" + e.getMessage(), e);
		}
		return Collections.emptyList();
	}

	/**
	 * Build HTTP headers
	 */
	private Headers buildHeaders() {
		Headers.Builder headers = new Headers.Builder();
		headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON).add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
		return headers.build();
	}
}
