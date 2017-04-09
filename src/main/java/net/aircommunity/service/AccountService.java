package net.aircommunity.service;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.AirException;
import net.aircommunity.model.Account;
import net.aircommunity.model.Page;
import net.aircommunity.model.Account.Status;
import net.aircommunity.model.AccountAuth.AuthType;
import net.aircommunity.model.Role;

/**
 * Account service manages {@code Account}s. The account principal can be username, mobile or email.
 * 
 * @author Bin.Zhang
 */
public interface AccountService {

	/**
	 * Authenticate an account.
	 * 
	 * @param principal the username, mobile or email (internal)
	 * @param credential the credential
	 * @param isOtp whether is one time password or not (dynamic code via mobile)
	 * @return authenticated account, null otherwise
	 */
	Account authenticateAccount(@Nonnull String principal, @Nonnull String credential, boolean isOtp);

	/**
	 * Authenticate an account with external auth methods (external, a.k.a. 3rd party).
	 * 
	 * @param type auth type
	 * @param principal the auth principal
	 * @param credential the auth credential
	 * @param expires the account authentication expiry
	 * @return the authenticated account
	 */
	Account authenticateAccount(@Nonnull AuthType type, @Nonnull String principal, @Nonnull String credential,
			long expires);

	/**
	 * Create an account.
	 * 
	 * @param type the authentication type
	 * @param principal the account principal
	 * @param credential the account credential
	 * @param verificationCode the verification code
	 * @param expires the account authentication expiry
	 * @param role the account role
	 * @return the created account
	 */
	// @Nonnull
	// Account createAccount(@Nonnull AuthType type, @Nonnull String principal, @Nonnull String credential,
	// @Nullable String verificationCode, long expires, @Nonnull Role role);

	/**
	 * Create an admin account.
	 * 
	 * @param username the account username
	 * @param password the account password
	 * @return the created account
	 */
	@Nonnull
	Account createAdminAccount(@Nonnull String username, @Nonnull String password);

	/**
	 * Create an account via username (for website).
	 * 
	 * @param mobile the account mobile
	 * @param password the account password
	 * @param role the user role
	 * @return the created account
	 */
	@Nonnull
	Account createAccount(@Nonnull String username, @Nonnull String password, Role role);

	/**
	 * Create an account via mobile (for app).
	 * 
	 * @param mobile the account mobile
	 * @param password the account password
	 * @param verificationCode the verification code received by user mobile
	 * @return the created account
	 */
	@Nonnull
	Account createAccount(@Nonnull String mobile, @Nonnull String password, @Nullable String verificationCode,
			Role role);

	/**
	 * Update account profile.
	 * 
	 * @param accountId the accountId
	 * @param newAccount the new account
	 * @return updated account
	 */
	@Nonnull
	Account updateAccount(@Nonnull String accountId, @Nonnull Account newAccount);

	/**
	 * Update account status.
	 * 
	 * @param accountId the accountId
	 * @param newStatus the new status
	 * @return updated account
	 */
	@Nonnull
	Account updateAccountStatus(@Nonnull String accountId, @Nonnull Status newStatus);

	/**
	 * Find an account by apiKey.
	 * 
	 * @param apiKey the account apiKey
	 * @return Account found
	 */
	@Nonnull
	Optional<Account> findAccountByApiKey(@Nonnull String apiKey);

	/**
	 * Find an account by ID.
	 * 
	 * @param accountId the accountId
	 * @return Account found
	 * @throws AirException if Account with the given is not found
	 */
	@Nonnull
	Account findAccount(@Nonnull String accountId);

	/**
	 * List All Accounts by pagination
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of accounts or empty
	 */
	@Nonnull
	Page<Account> listAccounts(int page, int pageSize);

	/**
	 * Change the username for a account.
	 * 
	 * @param accountId the accountId
	 * @param username the account username
	 */
	void updateUsername(@Nonnull String accountId, @Nonnull String username);

	/**
	 * Update email.
	 * 
	 * @param accountId theaccountId
	 * @param email the new email
	 * @throws AirException if email is not legitimate
	 */
	void updateEmail(@Nonnull String accountId, @Nonnull String email);

	void confirmEmail(@Nonnull String accountId, @Nonnull String verificationCode);

	/**
	 * Change the password for a account.
	 * 
	 * @param accountId the accountId
	 * @param oldPassword the account old password
	 * @param newPassword the new password
	 * @return update account
	 */
	@Nonnull
	Account updatePassword(@Nonnull String accountId, @Nonnull String oldPassword, @Nonnull String newPassword);

	/**
	 * Reset password with a random value and send via email
	 * 
	 * @param accountId the accountId
	 * @return update account
	 */
	@Nonnull
	Account resetPassword(@Nonnull String accountId);

	/**
	 * Refresh apiKey
	 * 
	 * @param accountId the accountId
	 * @return update account
	 */
	@Nonnull
	Account refreshApiKey(@Nonnull String accountId);

	/**
	 * Delete an account by principal.
	 * 
	 * @param accountId the accountId
	 * @throws AirException if Account with the given is not found
	 */
	void deleteAccount(@Nonnull String accountId);

}
