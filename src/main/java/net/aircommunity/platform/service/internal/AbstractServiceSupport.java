package net.aircommunity.platform.service.internal;

import java.io.Serializable;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import com.google.common.eventbus.EventBus;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.SettingsRepository;
import net.aircommunity.platform.service.AccountService;

/**
 * Abstract service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceSupport.class);

	@Resource
	protected Configuration configuration;

	@Resource
	protected EventBus eventBus;

	@Resource
	protected AccountService accountService;

	@Resource
	protected SettingsRepository settingsRepository;

	@Resource
	protected CacheManager cacheManager;

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
		return Pages.adapt(repository.findAll(Pages.createPageRequest(page, pageSize)));
	}

}
