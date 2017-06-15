package net.aircommunity.platform.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.Account.Status;
import net.aircommunity.platform.model.AccountAuth;
import net.aircommunity.platform.model.AccountAuth.AuthType;
import net.aircommunity.platform.model.Address;
import net.aircommunity.platform.model.AuthContext;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Tenant.VerificationStatus;

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
	 * @param context the auth context
	 * @return authenticated account, null otherwise
	 */
	@Nonnull
	Account authenticateAccount(@Nonnull String principal, @Nonnull String credential, @Nonnull AuthContext context);

	/**
	 * Authenticate an account with external auth methods (external, a.k.a. 3rd party).
	 * 
	 * @param type auth type
	 * @param principal the auth principal
	 * @param credential the auth credential
	 * @param context the auth context
	 * @return the authenticated account
	 */
	@Nonnull
	Account authenticateAccount(@Nonnull AuthType type, @Nonnull String principal, @Nonnull String credential,
			@Nonnull AuthContext context);

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
	 * Update tenant account verification status.
	 * 
	 * @param accountId the accountId
	 * @param newStatus the new verification status
	 * @return updated account
	 */
	@Nonnull
	Account updateTenantVerificationStatus(@Nonnull String accountId, @Nonnull VerificationStatus newStatus);

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
	 * Find an account auths by ID.
	 * 
	 * @param accountId the accountId
	 * @return Account found
	 * @throws AirException if Account with the given is not found
	 */
	@Nonnull
	List<AccountAuth> findAccountAuths(String accountId);

	AccountAuth findAccountMobile(@Nonnull String accountId);

	AccountAuth findAccountUsername(@Nonnull String accountId);

	AccountAuth findAccountEmail(@Nonnull String accountId);

	/**
	 * List all Addresses of an user.
	 * 
	 * @param accountId the accountId
	 * @return a list of addresses or empty if none
	 * @throws AirException if Account with the given is not found
	 */
	@Nonnull
	List<Address> listUserAddresses(@Nonnull String accountId);

	@Nonnull
	Account addUserAddress(@Nonnull String accountId, @Nonnull Address address);

	@Nonnull
	Account removeUserAddress(@Nonnull String accountId, @Nonnull String addressId);

	/**
	 * List all Passengers of an user.
	 * 
	 * @param accountId the accountId
	 * @return a list of passengers or empty if none
	 * @throws AirException if Account with the given is not found
	 */
	@Nonnull
	List<Passenger> listUserPassengers(@Nonnull String accountId);

	@Nonnull
	Passenger addUserPassenger(@Nonnull String accountId, @Nonnull Passenger passenger);

	void removeUserPassenger(@Nonnull String accountId, @Nonnull String passengerId);

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
	 * List All Accounts by pagination filter by role.
	 * 
	 * @param role the account role
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of accounts or empty
	 */
	@Nonnull
	Page<Account> listAccounts(@Nullable Role role, int page, int pageSize);

	/**
	 * Change the username for a account.
	 * 
	 * @param accountId the accountId
	 * @param deltaPoints add delta point (+-)
	 * @return account updated
	 */
	@Nonnull
	Account updateUserPoints(@Nonnull String accountId, long deltaPoints);

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
	 * Reset password to a new password
	 * 
	 * @param accountId the accountId
	 * @param newPassword the newPassword
	 * @return update account
	 */
	@Nonnull
	Account resetPasswordTo(@Nonnull String accountId, @Nonnull String newPassword);

	/**
	 * Reset password to a new password
	 * 
	 * @param mobile the mobile
	 * @param newPassword the newPassword
	 * @return update account
	 */
	@Nonnull
	Account resetPasswordViaMobile(@Nonnull String mobile, @Nonnull String newPassword);

	/**
	 * Reset password with a random value and send via email
	 * 
	 * @param accountId the accountId
	 * @return update account
	 */
	@Nonnull
	Account resetPasswordViaEmail(@Nonnull String accountId);

	/**
	 * Refresh apiKey
	 * 
	 * @param accountId the accountId
	 * @return update account
	 */
	@Nonnull
	Account refreshApiKey(@Nonnull String accountId);

	/**
	 * Daily signin
	 * 
	 * @param accountId the accountId
	 * @return update account (user only)
	 */
	@Nonnull
	Account dailySignin(@Nonnull String accountId);

	/**
	 * Delete an account by principal.
	 * 
	 * @param accountId the accountId
	 * @throws AirException if Account with the given is not found
	 */
	void deleteAccount(@Nonnull String accountId);

}
