package net.aircommunity.security;

import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.aircommunity.Constants;
import net.aircommunity.model.Account;
import net.aircommunity.rest.core.security.Claims;
import net.aircommunity.rest.core.security.TokenVerificationService;
import net.aircommunity.service.AccountService;

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
		Claims claims = new Claims(account.getId(), ImmutableMap.of(Claims.CLAIM_ROLES,
				ImmutableSet.of(account.getRole().getValue()), Constants.CLAIM_API_KEY, account.getApiKey()));
		claimsResult.accept(claims);
		return true;
	}

}
