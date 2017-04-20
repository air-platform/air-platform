package net.aircommunity.platform.service.internal;

import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AirBB Account CRUD.
 * 
 * @author luocheng
 */
@Service
public class AirBBAccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AirBBAccountService.class);

	private static final String APPLICATION_JSON = "application/json";
	private static final String USER_API_URL_FORMAT = "%s/api/v1/users";

	private String userApiUrl;
	private String userApiUrlBase;

	@Resource
	private Configuration configuration;

	@Resource
	private OkHttpClient httpClient;

	@PostConstruct
	private void init() {
		userApiUrl = String.format(USER_API_URL_FORMAT, configuration.getNodebbUrl());
		userApiUrlBase = userApiUrl + "/";
	}

	// TODO use OkHttpClient instead of HttpURLConnection

	public void createAccount(String username, String password) {
		try {
			URL url = new URL(userApiUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", APPLICATION_JSON);
			conn.setRequestProperty("Authorization", configuration.getNodebbToken());
			String json = Json.createObjectBuilder().add("username", username).add("password", password)
					.add("email", "").add("_uid", "1").build().toString();

			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();
			int respCode = conn.getResponseCode();
			if (respCode != HttpURLConnection.HTTP_OK) {
				LOG.debug("Creating NodeBB user Failed : HTTP error code : " + respCode);
			}
			conn.disconnect();
		}
		catch (Exception e) {
			LOG.error("Failed to create NodeBB user:" + e.getMessage(), e);
			throw new AirException(Codes.INTERNAL_ERROR, "Failed to create NodeBB user:" + e.getMessage(), e);
		}
	}

	public void updateAccountPassword(String username, String newPassword) {
		try {
			String userID = getUsername(username);
			URL url = new URL(userApiUrlBase + userID + "/password");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", APPLICATION_JSON);
			conn.setRequestProperty("Authorization", configuration.getNodebbToken());

			String json = Json.createObjectBuilder().add("uid", userID).add("new", newPassword).add("_uid", "1").build()
					.toString();
			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();

			int respCode = conn.getResponseCode();
			if (respCode != HttpURLConnection.HTTP_OK) {
				LOG.debug("update NodeBB user password Failed : HTTP error code : " + respCode);
			}
			conn.disconnect();
		}
		catch (Exception e) {
			LOG.error("Failed to update NodeBB user password:" + e.getMessage(), e);
			throw new AirException(Codes.INTERNAL_ERROR, "Failed to update NodeBB user password:" + e.getMessage(), e);
		}
	}

	public void updateAccountProfile(String username, String email) {
		try {
			String userID = getUsername(username);
			URL url = new URL(userApiUrlBase + userID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", APPLICATION_JSON);
			conn.setRequestProperty("Authorization", configuration.getNodebbToken());

			String json = Json.createObjectBuilder().add("email", email).add("_uid", "1").build().toString();
			OutputStream os = conn.getOutputStream();
			os.write(json.getBytes());
			os.flush();

			int respCode = conn.getResponseCode();
			if (respCode != HttpURLConnection.HTTP_OK) {
				LOG.debug("update NodeBB user profile Failed : HTTP error code : " + respCode);
			}

			conn.disconnect();
		}
		catch (Exception e) {
			LOG.error("Failed to update NodeBB user email:" + e.getMessage(), e);
			throw new AirException(Codes.INTERNAL_ERROR, "Failed to update NodeBB user email:" + e.getMessage(), e);
		}
	}

	public void deleteAccount(String username) {
		try {
			String userID = getUsername(username);
			URL url = new URL(userApiUrlBase + userID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", APPLICATION_JSON);
			conn.setRequestProperty("Authorization", configuration.getNodebbToken());

			String json = Json.createObjectBuilder().add("_uid", "1").build().toString();
			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes());
			os.flush();

			int respCode = conn.getResponseCode();
			if (respCode != HttpURLConnection.HTTP_OK) {
				LOG.debug("Deleting NodeBB user Failed : HTTP error code : " + respCode);
			}
			conn.disconnect();
		}
		catch (Exception e) {
			LOG.error("Failed to delete NodeBB user:" + e.getMessage(), e);
			throw new AirException(Codes.INTERNAL_ERROR, "Failed to delete NodeBB user:" + e.getMessage(), e);
		}
	}

	public String getUsername(String username) {
		String userID = null;
		try {
			Request request = new Request.Builder().url(userApiUrl).headers(buildHeaders()).get().build();
			Response response = httpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				try (JsonReader jsonReader = Json.createReader(new StringReader(response.body().string()))) {
					JsonObject jsonObj = jsonReader.readObject();
					JsonArray userArray = jsonObj.getJsonArray("users");
					for (int i = 0; i < userArray.size(); i++) {
						JsonObject user = userArray.getJsonObject(i);
						String un = user.getString("username");
						if (un.equals(username)) {
							userID = user.get("uid").toString();
							break;
						}
					}
				}
			}
		}
		catch (Exception e) {
			LOG.error("Failed to get NodeBB user ID:" + e.getMessage(), e);
			throw new AirException(Codes.INTERNAL_ERROR, "Failed to get NodeBB user ID:" + e.getMessage(), e);
		}
		return userID;
	}

	/**
	 * Build HTTP headers
	 */
	private Headers buildHeaders() {
		Headers.Builder headers = new Headers.Builder();
		headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON).add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
				.add(HttpHeaders.AUTHORIZATION, configuration.getNodebbToken());
		return headers.build();
	}
}
