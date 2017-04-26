package net.aircommunity.platform.service.internal;

import java.io.Serializable;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.AccountService;

/**
 * Abstract service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceSupport.class);

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
			LOG.error("Internal error:" + e.getMessage(), e);
			throw new AirException(Codes.INTERNAL_ERROR, M.bind(M.INTERNAL_SERVER_ERROR));
		}
	}

	protected <T, ID extends Serializable> Page<T> findAll(JpaRepository<T, ID> repository, int page, int pageSize) {
		return Pages.adapt(repository.findAll(Pages.createPageRequest(page, pageSize)));
	}

}
