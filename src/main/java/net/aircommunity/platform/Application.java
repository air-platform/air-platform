package net.aircommunity.platform;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.micro.boot.annotation.EnableJaxrs;
import io.micro.common.crypto.bcrypt.BCryptPasswordEncoder;
import io.micro.common.crypto.password.PasswordEncoder;
import io.micro.core.security.AccessTokenConfig;
import io.micro.core.security.AccessTokenConfigStorage;
import io.micro.core.security.AccessTokenService;
import io.micro.core.security.AccessTokenVerifier;
import io.micro.core.security.AuthenticationStrategy;
import io.micro.core.security.SimpleAccessTokenService;
import io.micro.core.security.TokenVerificationService;
import io.micro.core.security.filter.AuthenticationFilter;
import io.micro.support.ObjectMappers;
import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.repository.SettingRepository;
import net.aircommunity.platform.security.DatabaseTokenConfigStorage;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * Entry point of AIR Platform.
 * 
 * @author Bin.Zhang
 */
@EnableJaxrs
@EnableScheduling
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableRetry(proxyTargetClass = true)
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication
@SuppressWarnings("javadoc")
public class Application {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	private static final String KEY_ID = "access_token_key";
	private static final String ISSUER = "AIR_PLATFORM_ISSUER";
	private static final String AUDIENCE = "AIR_PLATFORM_AUDIENCE";
	private static final String KEY_ID_PASSWORD = "AIR_PLATFORM_SECUREKEY_PASSWORD_9D8DD4ECBB9341D6BB364A053B058A34";

	@Value("${air.dc-id}")
	private long datacenterId;

	@Value("${air.node-id}")
	private long nodeId;

	@Value("${air.security.access-token-expiration-time}")
	private long expirationTimeSeconds = 60 * 60 * 24 * 7; // 7 Days

	@Value("${air.security.access-token-refresh-time}")
	private long refreshTimeSeconds = 60 * 60 * 24 * 7; // 7 Days

	@Resource
	private TokenVerificationService appkeyVerificationService;

	@Bean
	public OrderNoGenerator orderNoGenerator() {
		return new OrderNoGenerator(datacenterId, nodeId);
	}

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
		config.setKeyIdPassword(KEY_ID_PASSWORD);
		config.setIssuer(ISSUER);
		config.setAudience(AUDIENCE);
		config.setExpirationTimeSeconds(expirationTimeSeconds);
		config.setRefreshTimeSeconds(refreshTimeSeconds);
		accessTokenService.initialize(config);
		return accessTokenService;
	}

	@Bean
	public AccessTokenConfigStorage accessTokenKeyStorage(ApplicationContext ctx) {
		return new DatabaseTokenConfigStorage(ctx.getBean(SettingRepository.class), objectMapper());
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

	@Bean
	public RetryTemplate retryTemplate() {
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(5);
		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(1000); // 1 seconds
		RetryTemplate template = new RetryTemplate();
		template.setRetryPolicy(retryPolicy);
		template.setBackOffPolicy(backOffPolicy);
		return template;
	}

	/**
	 * Start application
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		app.addListeners(new ApplicationPidFileWriter());
		try {
			app.run(args);
		}
		catch (Throwable e) {
			LOG.error("Application exiting... run into error:" + e.getMessage(), e);
			System.exit(1);
		}
	}

}
