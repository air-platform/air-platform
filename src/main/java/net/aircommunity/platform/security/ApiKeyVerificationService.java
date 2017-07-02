package net.aircommunity.platform.security;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.micro.core.security.Claims;
import io.micro.core.security.TokenVerificationService;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.service.security.AccountService;

/**
 * Verify API calls.
 * 
 * @author Bin.Zhang
 */
@Service
public class ApiKeyVerificationService implements TokenVerificationService {

	@Resource
	private AccountService accountService;

	@Override
	public boolean verifyToken(String token, Consumer<Claims> claimsResult) {
		// load apiKey from DB and check
		Optional<Account> accountRef = accountService.findAccountByApiKey(token);
		if (!accountRef.isPresent()) {
			return false;
		}
		Account account = accountRef.get();
		Role role = account.getRole();
		// NOTE: only allow ADMIN & TENANT call API using API KEY
		if (role == Role.ADMIN || role == Role.TENANT) {
			Map<String, Object> claimsMap = ImmutableMap.of(//
					Constants.CLAIM_ID, account.getId(), // ID
					Constants.CLAIM_API_KEY, account.getApiKey(), // API KEY
					Claims.CLAIM_ROLES, ImmutableSet.of(account.getRole().name())); // ROLES
			claimsResult.accept(new Claims(account.getId(), claimsMap));
			return true;
		}
		return false;
	}

}
