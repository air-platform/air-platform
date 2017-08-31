package net.aircommunity.platform.service.internal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Resource;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.DomainEvent;
import net.aircommunity.platform.model.DomainEvent.DomainType;
import net.aircommunity.platform.model.DomainEvent.Operation;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.model.domain.Product.Type;
import net.aircommunity.platform.model.domain.PushNotification;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AccountAuthRepository;
import net.aircommunity.platform.repository.SettingRepository;
import net.aircommunity.platform.service.common.PushNotificationService;
import net.aircommunity.platform.service.security.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.retry.support.RetryTemplate;

/**
 * Abstract service support.
 *
 * @author Bin.Zhang
 */
public abstract class AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceSupport.class);

	// *******************************************************************
	// METRICS
	// *******************************************************************
	// metrics base name
	private static final String PRODUCT_METRIC_NAME_FORMAT = "api.product.%s.%s";
	private static final String ORDER_METRIC_NAME_FORMAT = "api.order.%s.%s";

	// *******************************************************************
	// PRODUCT ACTIONS
	// *******************************************************************
	protected static final String PRODUCT_ACTION_CREATE = "create";
	protected static final String PRODUCT_ACTION_READ = "read";
	protected static final String PRODUCT_ACTION_UPDATE = "update";
	protected static final String PRODUCT_ACTION_DELETE = "delete";
	// list products for ANYBODY (read only)
	protected static final String PRODUCT_ACTION_LIST = "list";

	// list all orders| products for a TENANT
	protected static final String PRODUCT_ACTION_TENANT_LIST = "tenant.list";

	// list all orders (included DELETED status) | products (all products of tenants) for a ADMIN
	protected static final String PRODUCT_ACTION_AMDIN_LIST = "admin.list";

	// *******************************************************************
	// ORDER ACTIONS
	// *******************************************************************
	protected static final String ORDER_ACTION_CREATE = "create";
	protected static final String ORDER_ACTION_READ = "read";
	protected static final String ORDER_ACTION_UPDATE = "update";
	protected static final String ORDER_ACTION_DELETE = "delete";
	protected static final String ORDER_ACTION_PAYMENT = "payment";
	protected static final String ORDER_ACTION_REFUND = "refund";

	// USER
	// list all orders for a USER (all)
	protected static final String ORDER_ACTION_USER_LIST = "user.list";
	// list all orders for a USER (pending, finished, cancelled, refund)
	protected static final String ORDER_ACTION_USER_LIST_GROUPED = "user.list.grouped";
	// recent N days
	protected static final String ORDER_ACTION_USER_LIST_NDAYS = "user.list.ndays";

	// TENANT
	// list all order for TENANT (all)
	protected static final String ORDER_ACTION_TENANT_LIST = "tenant.list";

	// ADMIN
	// orders only
	// admin list all orders
	protected static final String ORDER_ACTION_ADMIN_LIST = "admin.list";
	// admin list all orders of a user
	protected static final String ORDER_ACTION_ADMIN_LIST_USER = "admin.list.user";
	// admin list all orders of a tenant
	protected static final String ORDER_ACTION_ADMIN_LIST_TENANT = "admin.list.tenant";

	@Resource
	protected Configuration configuration;

	@Resource
	protected ObjectMapper objectMapper;

	@Resource
	protected RetryTemplate retryTemplate;

	@Resource
	protected GaugeService gaugeService;

	@Resource
	protected MetricRegistry metricRegistry;

	@Resource
	protected EventBus eventBus;

	@Resource
	protected AccountService accountService;

	@Resource
	protected SettingRepository settingRepository;

	@Resource
	protected CacheManager cacheManager;

	@Resource
	protected PushNotificationService pushNotificationService;

	@Resource
	private AccountAuthRepository accountAuthRepository;

	protected Timer orderOperationTimer(Class<?> type, String action) {
		Type t = typeMapping.get(type);
		return metricRegistry.timer(orderMetricName(t, action));
	}

	protected Timer productOperationTimer(Class<?> type, String action) {
		Type t = typeMapping.get(type);
		return metricRegistry.timer(productMetricName(t, action));
	}

	private static String orderMetricName(Type type, String action) {
		String name = type == null ? TYPE_COMMON_ORDER : type.name().toLowerCase(Locale.ENGLISH);
		return String.format(ORDER_METRIC_NAME_FORMAT, name, action);
	}

	private static String productMetricName(Type type, String action) {
		String name = type == null ? TYPE_COMMON_PRODUCT : type.name().toLowerCase(Locale.ENGLISH);
		return String.format(PRODUCT_METRIC_NAME_FORMAT, name, action);
	}

	protected boolean isMetricsEnabled() {
		return configuration.isApiMetricsEnabled();
	}

	private static final String TYPE_COMMON_PRODUCT = "common";
	private static final String TYPE_COMMON_ORDER = "common";
	// @formatter:off
	private static final Map<Class<?>, Type> typeMapping = ImmutableMap.<Class<?>, Type>builder()
			.put(AirTaxi.class, Type.AIRTAXI)
			.put(AirTour.class, Type.AIRTOUR)
			.put(AirTransport.class, Type.AIRTRANSPORT)
			.put(Fleet.class, Type.FLEET)
			.put(FerryFlight.class, Type.FERRYFLIGHT)
			.put(JetTravel.class, Type.JETTRAVEL)
			.put(Course.class, Type.COURSE)
			.build();
	// @formatter:on

	protected final <T extends Account> T findAccount(String accountId, Class<T> type) {
		Account account = accountService.findAccount(accountId);
		try {
			return type.cast(account);
		}
		catch (Exception e) {
			LOG.error(String.format("Account type mismatch, expected %s, but was %s, cause:", type, account.getClass(),
					e.getMessage()), e);
			throw new AirException(Codes.ACCOUNT_TYPE_MISMATCH, M.msg(M.ACCOUNT_TYPE_MISMATCH));
		}
	}

	private static final Predicate<DomainEvent> PREDICATE_ALWAYS = e -> true;

	// TODO: improve cache on different product or order type, or per tenant basis if necessary ?
	protected void registerCacheEvictOnDomainEvent(String cacheName, EnumSet<DomainType> interestedDomains) {
		registerCacheEvictOnDomainEvent(cacheName, interestedDomains, PREDICATE_ALWAYS);
	}

	protected void registerPushNotificationEvent(EnumSet<DomainType> interestedDomains) {
		registerPushNotificationOnDomainEvent(interestedDomains, PREDICATE_ALWAYS);
	}

	/**
	 * Register domain event to evict cache on UPDATE and DELETION.
	 *
	 * @param interestedDomains the interested domains
	 */
	protected void registerPushNotificationOnDomainEvent(EnumSet<DomainType> interestedDomains,
														 Predicate<DomainEvent> when) {
		Objects.requireNonNull(interestedDomains, "interestedDomains cannot be null");
		Objects.requireNonNull(when, "when cannot be null");
		registerDomainEventHandler(event -> {
			LOG.debug("Received domain event: {}", event);
			boolean notificationRequired = event.getOperation() == Operation.PUSH_NOTIFICATION;
			if (interestedDomains.contains(event.getType()) && notificationRequired && when.test(event)) {
				PushNotification pf = new PushNotification();
				String accountId = event.getParam(Constants.TEMPLATE_PUSHNOTIFICATION_ACCOUNTID).toString();


				User user = findAccount(accountId, User.class);
				pf.setType(PushNotification.Type.PLAIN_TEXT);

				//TODO use meaningful alias?
				// jiguang sdk don't support string containing dash as alias, use underline instead
				pf.setAlias(accountId.replace("-", "_"));

				pf.setOwner(user);

				if (event.getType() == DomainType.ORDER) {
					String msg = event.getParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE).toString();
					String extras = event.getParam(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS).toString();
					pf.setMessage(msg);
					pf.setExtra(extras);
				}
				else if (event.getType() == DomainType.POINT) {
					String extras = event.getParam(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS).toString();

					pf.setMessage(Constants.TEMPLATE_PUSHNOTIFICATION_POINT_MESSAGE);
					pf.setExtra(extras);
				}
				pushNotificationService.sendInstantPushNotification(pf);
			}
		});

	}

	/**
	 * Register domain event to evict cache on UPDATE and DELETION.
	 *
	 * @param cacheName         the name of the cache to be cleared
	 * @param interestedDomains the interested domains
	 */
	protected void registerCacheEvictOnDomainEvent(String cacheName, EnumSet<DomainType> interestedDomains,
												   Predicate<DomainEvent> when) {
		Objects.requireNonNull(cacheName, "cacheName cannot be null");
		Objects.requireNonNull(interestedDomains, "interestedDomains cannot be null");
		Objects.requireNonNull(when, "when cannot be null");
		registerDomainEventHandler(event -> {
			LOG.debug("Received domain event: {}", event);
			boolean evictRequired = event.getOperation() == Operation.UPDATE
					|| event.getOperation() == Operation.DELETION;
			if (interestedDomains.contains(event.getType()) && evictRequired && when.test(event)) {
				clearCache(cacheName);
			}
		});

	}

	/**
	 * Register domain event handler
	 *
	 * @param consumer the domain event consumer
	 */
	protected void registerDomainEventHandler(Consumer<DomainEvent> consumer) {
		eventBus.register(new DomainEvent.Handler(consumer));
	}

	/**
	 * Post domain event that other service may interested in
	 *
	 * @param event the domain event
	 */
	protected void postDomainEvent(DomainEvent event) {
		eventBus.post(event);
	}

	protected void clearCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.clear();
		}
	}

	protected final <T> T safeExecute(Callable<T> action, String errorMessageTemplate, Object... errorMessageArgs) {
		try {
			return action.call();
		}
		catch (Exception e) {
			String errorMessage = String.format(errorMessageTemplate, errorMessageArgs);
			LOG.error(String.format("%s, casue: %s", errorMessage, e.getMessage()), e);
			throw newInternalException();
		}
	}

	protected final void safeExecute(Runnable action, String errorMessageTemplate, Object... errorMessageArgs) {
		try {
			action.run();
		}
		catch (Exception e) {
			String errorMessage = String.format(errorMessageTemplate, errorMessageArgs);
			LOG.error(String.format("%s, casue: %s", errorMessage, e.getMessage()), e);
			throw newInternalException();
		}
	}

	/**
	 * Delete
	 */
	protected final <T> void safeDeletion(Runnable flusher, Runnable deleteAction, Code errorCode,
										  String errorMessage) {
		try {
			deleteAction.run();
			// NOTE: important flush() here, otherwise we cannot catch DataIntegrityViolationException
			flusher.run();
		}
		catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Deletion failure, casue: %s", e.getMessage()), e);
			throw new AirException(errorCode, errorMessage);
		}
		catch (Exception e) {
			LOG.error(String.format("Internal server error casue: %s", e.getMessage()), e);
			newInternalException();
		}
	}

	/**
	 * Delete
	 */
	protected final <T> void safeDeletion(JpaRepository<T, String> repository, String id, Code errorCode,
										  String errorMessage) {
		try {
			repository.delete(id);
			// NOTE: important flush() here, otherwise we cannot catch DataIntegrityViolationException
			repository.flush();
		}
		catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Deletion failure, casue: %s", e.getMessage()), e);
			throw new AirException(errorCode, errorMessage);
		}
		catch (Exception e) {
			LOG.error(String.format("Internal server error casue: %s", e.getMessage()), e);
			newInternalException();
		}
	}

	/**
	 * Delete
	 */
	protected final <T> void safeDeletion(JpaRepository<T, String> repository, Runnable deleteAction, Code errorCode,
										  String errorMessage) {
		try {
			deleteAction.run();
			// NOTE: important flush() here, otherwise we cannot catch DataIntegrityViolationException
			repository.flush();
		}
		catch (DataIntegrityViolationException e) {
			LOG.error(String.format("Deletion failure, casue: %s", e.getMessage()), e);
			throw new AirException(errorCode, errorMessage);
		}
		catch (Exception e) {
			LOG.error(String.format("Internal server error casue: %s", e.getMessage()), e);
			newInternalException();
		}
	}

	protected AirException newInternalException() {
		return new AirException(Codes.INTERNAL_ERROR, M.msg(M.INTERNAL_SERVER_ERROR));
	}

	protected AirException newServiceUnavailableException() {
		return new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
	}

	protected <T, ID extends Serializable> Page<T> findAll(JpaRepository<T, ID> repository, int page, int pageSize) {
		return Pages.adapt(repository.findAll(createPageRequest(page, pageSize)));
	}

	protected static Pageable createPageRequest(int page, int pageSize) {
		return Pages.createPageRequest(page, pageSize);
	}
}
