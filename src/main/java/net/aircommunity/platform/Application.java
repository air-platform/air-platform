package net.aircommunity.platform;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.aircommunity.platform.common.crypto.bcrypt.BCryptPasswordEncoder;
import net.aircommunity.platform.common.crypto.password.PasswordEncoder;
import net.aircommunity.platform.repository.SettingsRepository;
import net.aircommunity.platform.security.DatabaseTokenConfigStorage;
import net.aircommunity.rest.boot.annotation.EnableJaxrs;
import net.aircommunity.rest.core.security.AccessTokenConfig;
import net.aircommunity.rest.core.security.AccessTokenConfigStorage;
import net.aircommunity.rest.core.security.AccessTokenService;
import net.aircommunity.rest.core.security.AccessTokenVerifier;
import net.aircommunity.rest.core.security.AuthenticationStrategy;
import net.aircommunity.rest.core.security.SimpleAccessTokenService;
import net.aircommunity.rest.core.security.TokenVerificationService;
import net.aircommunity.rest.core.security.filter.AuthenticationFilter;
import net.aircommunity.rest.support.ObjectMappers;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * Entry point of AIR Platform.
 * 
 * @author Bin.Zhang
 */
@EnableJaxrs
@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableConfigurationProperties
@SuppressWarnings("javadoc")
public class Application {

	private static final String KEY_ID = "air.access_token_key";

	@Value("${air.security.access-token-expiration-time}")
	private long expirationTimeSeconds = 60 * 60 * 24 * 7; // 7 Days

	@Value("${air.security.access-token-refresh-time}")
	private long refreshTimeSeconds = 60 * 60 * 24 * 7; // 7 Days

	@Resource
	private TokenVerificationService appkeyVerificationService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return ObjectMappers.getObjectMapper();
	}

	@Bean
	public AuthenticationFilter authenticationFilter(ApplicationContext ctx) {
		AuthenticationFilter filter = new AuthenticationFilter();
		filter.setTokenVerifiers(new AccessTokenVerifier(appkeyVerificationService),
				new AccessTokenVerifier(accessTokenService(ctx)));
		filter.setAuthenticationStrategy(AuthenticationStrategy.atLeastOneSuccess());
		return filter;
	}

	@Bean
	public AccessTokenService accessTokenService(ApplicationContext ctx) {
		SimpleAccessTokenService accessTokenService = new SimpleAccessTokenService();
		accessTokenService.setAccessTokenConfigStorage(accessTokenKeyStorage(ctx));
		AccessTokenConfig config = new AccessTokenConfig();
		config.setKeyId(KEY_ID);
		config.setExpirationTimeSeconds(expirationTimeSeconds);
		config.setRefreshTimeSeconds(refreshTimeSeconds);
		accessTokenService.initialize(config);
		return accessTokenService;
	}

	@Bean
	public AccessTokenConfigStorage accessTokenKeyStorage(ApplicationContext ctx) {
		return new DatabaseTokenConfigStorage(ctx.getBean(SettingsRepository.class), objectMapper());
	}

	@Bean
	public OkHttpClient httpClient() {
		// ConnectionSpec customSpec = new
		// ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_2)
		// .cipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
		// CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
		// CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
		// .build();
		ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.SECONDS);
		return new OkHttpClient.Builder().readTimeout(6000, TimeUnit.MILLISECONDS)
				.connectTimeout(6000, TimeUnit.MILLISECONDS).connectionPool(connectionPool)
				.retryOnConnectionFailure(false).build();
	}

	@Bean
	public EventBus eventBus() {
		return new AsyncEventBus("async-eventbus", Executors.newFixedThreadPool(10,
				new ThreadFactoryBuilder().setDaemon(true).setNameFormat("async-eventbus-pool-%d").build()));
	}

	/**
	 * Start application
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		try {
			app.run(args);
		}
		catch (Exception e) {
			System.exit(1);
		}
	}

}
