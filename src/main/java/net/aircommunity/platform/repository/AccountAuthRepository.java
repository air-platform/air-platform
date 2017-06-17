package net.aircommunity.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.AccountAuth;
import net.aircommunity.platform.model.AccountAuth.AuthType;

/**
 * Repository interface for {@link AccountAuth} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface AccountAuthRepository extends JpaRepository<AccountAuth, String> {

	// TODO enable cache for CRUD

	/**
	 * Find an account auths for accountId.
	 * 
	 * @param accountId the accountId
	 * @return auths or empty if none
	 */
	List<AccountAuth> findByAccountId(String accountId);

	/**
	 * Find an account auth by type and accountId.
	 * 
	 * @param accountId the accountId
	 * @param type the auth type
	 * @return an auth or null if none
	 */
	AccountAuth findByAccountIdAndType(String accountId, AuthType type);

	/**
	 * Find an account auth by type and principal.
	 * 
	 * @param type the auth type
	 * @param principal the auth principal
	 * @return an auth or null if none
	 */
	AccountAuth findByTypeAndPrincipal(AuthType type, String principal);

	/**
	 * Delete auths of an account
	 *
	 * @param accountId the accountId
	 * @return the number of records deleted
	 */
	long deleteByAccountId(String accountId);

}
