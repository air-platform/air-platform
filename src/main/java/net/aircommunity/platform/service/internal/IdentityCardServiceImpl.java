package net.aircommunity.platform.service.internal;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import io.micro.common.DateFormats;
import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.IdCardInfo;
import net.aircommunity.platform.model.domain.User.Gender;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.IdentityCardService;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Resident identification card service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class IdentityCardServiceImpl implements IdentityCardService {
	private static final Logger LOG = LoggerFactory.getLogger(IdentityCardServiceImpl.class);

	private static final String AUTH_HEADER_FORMAT = "APPCODE %s";
	private static final String REQUEST_PROP_CARDNO = "cardno";
	private static final String REQUEST_PROP_NAME = "name";
	private static final String RESPONSE_PROP_RESP = "resp";
	private static final String RESPONSE_PROP_CODE = "code";
	private static final String RESPONSE_PROP_DATA = "data";
	private static final String RESPONSE_PROP_DATA_SEX = "sex";
	private static final String DATA_SEX_MALE = "M";
	private static final String RESPONSE_PROP_DATA_ADDRESS = "address";
	private static final String RESPONSE_PROP_DATA_BIRTHDAY = "birthday";
	private static final SimpleDateFormat BIRTHDAY_FORMATTER = DateFormats.simple("yyyy-MM-dd");
	private static final int RESPONSE_CODE_SUCCESS = 0;
	// private static final int RESPONSE_CODE_UNMATCH = 5;
	// private static final int RESPONSE_CODE_UNKNOWN_IDNO = 14;
	// private static final int RESPONSE_CODE_FAILURE = 96;
	private static final String API_PATH = "/lianzhuo/idcard";

	@Resource
	private Configuration configuration;

	@Resource
	private OkHttpClient httpClient;

	/**
	 * Example:
	 * 
	 * <pre>
	 * {
	 *	"resp": {
	 *		"code": 0,
	 *		"desc": "匹配"
	 *	},
	 *	"data": {
	 *		"sex": "M",
	 *		"address": "广东省清远市清新县",
	 *		"birthday": "1989-05-25"
	 *	 }
	 *	}
	 * </pre>
	 */
	@Override
	public boolean verifyIdentityCard(String cardNo, String name) {
		IdCardInfo info = getIdCardInfo(cardNo, name);
		return info != null;
	}

	@Override
	public IdCardInfo getIdCardInfo(String cardNo, String name) {
		try {
			Request request = new Request.Builder()
					.url(buildUrl(configuration.getIdcardUrl(), API_PATH,
							ImmutableMap.of(REQUEST_PROP_CARDNO, cardNo, REQUEST_PROP_NAME, name)))
					.headers(buildHeaders(configuration.getIdcardToken())).get().build();
			Response response = httpClient.newCall(request).execute();
			LOG.debug("Got response: {}", response);
			if (response.isSuccessful()) {
				String body = response.body().string();
				LOG.debug("Got result body: {}", body);
				try (JsonReader jsonReader = Json.createReader(new StringReader(body))) {
					JsonObject jsonData = jsonReader.readObject();
					int resultCode = jsonData.getJsonObject(RESPONSE_PROP_RESP).getInt(RESPONSE_PROP_CODE);
					if (resultCode == RESPONSE_CODE_SUCCESS) {
						JsonObject data = jsonData.getJsonObject(RESPONSE_PROP_DATA);
						Gender gender = DATA_SEX_MALE.equalsIgnoreCase(data.getString(RESPONSE_PROP_DATA_SEX))
								? Gender.MALE : Gender.FEMALE;
						String address = data.getString(RESPONSE_PROP_DATA_ADDRESS);
						String birthday = data.getString(RESPONSE_PROP_DATA_BIRTHDAY);
						return new IdCardInfo(cardNo, name, address, BIRTHDAY_FORMATTER.parse(birthday), gender);
					}
				}
			}
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to verify Identity cardNo: %s, name: %s, cause: %s", cardNo, name,
					e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.IDCARD_VERIFICATION_FAILURE));
		}
		return null;
	}

	/**
	 * Build HTTP headers
	 */
	private Headers buildHeaders(String appCode) {
		Headers.Builder headers = new Headers.Builder();
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
				.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.add(HttpHeaders.AUTHORIZATION, String.format(AUTH_HEADER_FORMAT, appCode));
		return headers.build();
	}

	private static String buildUrl(String host, String path, Map<String, String> queryParams)
			throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder();
		url.append(host);
		if (!Strings.isBlank(path)) {
			url.append(path);
		}
		if (null != queryParams) {
			StringBuilder query = new StringBuilder();
			for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
				if (query.length() > 0) {
					query.append("&");
				}
				if (Strings.isBlank(queryParam.getKey()) && !Strings.isBlank(queryParam.getValue())) {
					query.append(queryParam.getValue());
				}
				if (!Strings.isBlank(queryParam.getKey())) {
					query.append(queryParam.getKey());
					if (!Strings.isBlank(queryParam.getValue())) {
						query.append("=").append(URLEncoder.encode(queryParam.getValue(), "utf-8"));
					}
				}
			}
			if (query.length() > 0) {
				url.append("?").append(query);
			}
		}
		return url.toString();
	}
}
