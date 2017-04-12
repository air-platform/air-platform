package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.CacheManager;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.service.AccountService;

/**
 * Abstract service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractServiceSupport {

	@Resource
	protected AccountService accountService;

	@Resource
	protected CacheManager cacheManager;

	protected <T extends Account> T findAccount(String accountId, Class<T> type) {
		Account account = accountService.findAccount(accountId);
		try {
			return type.cast(account);
		}
		catch (Exception e) {
			throw new AirException(Codes.INTERNAL_ERROR, "Internal error:" + e.getMessage(), e);
		}
	}

}
