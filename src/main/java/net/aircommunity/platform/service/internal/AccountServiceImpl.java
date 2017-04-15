package net.aircommunity.platform.service.internal;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.common.base.Randoms;
import net.aircommunity.platform.common.base.Strings;
import net.aircommunity.platform.common.base.UUIDs;
import net.aircommunity.platform.common.crypto.password.PasswordEncoder;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.Account.Status;
import net.aircommunity.platform.model.AccountAuth;
import net.aircommunity.platform.model.AccountAuth.AuthType;
import net.aircommunity.platform.model.Address;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Tenant;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.AccountAuthRepository;
import net.aircommunity.platform.repository.AccountRepository;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.MailService;
import net.aircommunity.platform.service.VerificationService;
import net.aircommunity.rest.core.security.AccessTokenService;
import net.aircommunity.rest.core.security.Claims;

/**
 * Account service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

	private static final int PASSWORD_LENGTH = 20;
	// TODO configure full link
	private static final String EMAIL_CONFIRMATION_LINK_FORMAT = "account/email/confirm?token=%s&code=%s";
	private static final String CACHE_NAME_ACCOUNT = "cache.account";
	// private static final String CACHE_NAME_APIKEY = "cache.account_apikey";

	@Resource
	private Configuration configuration;

	@Resource
	private AccountRepository accountRepository;

	@Resource
	private AccountAuthRepository accountAuthRepository;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private MailService mailService;

	@Resource
	private VerificationService verificationService;

	@Resource
	private AccessTokenService accessTokenService;

	@PostConstruct
	private void init() {
		AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(AuthType.USERNAME,
				Constants.DEFAULT_ADMIN_USERNAME);
		if (auth == null) {
			createAdminAccount(Constants.DEFAULT_ADMIN_USERNAME, Constants.DEFAULT_ADMIN_PASSWORD);
			// for testing XXX
			createAccount("user1", "p0o9i8u7", Role.USER);
			LOG.debug("Created default admin account");
		}
	}

	@Override
	public Account authenticateAccount(String principal, String credential, boolean isOtp) {
		AccountAuth auth = null;
		for (AuthType authType : AuthType.internalAuths()) {
			auth = accountAuthRepository.findByTypeAndPrincipal(authType, principal);
			if (auth != null) {
				break;
			}
		}
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, String.format("Account %s is not found", principal));
		}
		Account account = auth.getAccount();
		if (account.getStatus() != Status.ENABLED) {
			LOG.warn("Account {} is not enabled, cannot be authenticated", principal);
			return null;
		}
		boolean valid = false;
		// OTP is available for mobile
		if (isOtp) {
			valid = verificationService.verifyCode(credential, credential);
		}
		else {
			valid = passwordEncoder.matches(credential, account.getPassword());
		}
		return valid ? account : null;
	}

	@Override
	public Account authenticateAccount(AuthType type, String principal, String credential, long expires) {
		Account account = null;
		switch (type) {
		case WECHAT:
		case QQ:
		case WEIBO:
			AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(type, principal);
			if (auth == null) {
				// create account
				account = createAccount(type, principal, credential, null, expires, Role.USER);
			}
			else {
				// update with new credential, e.g. access-token returned from wechat
				auth.setCredential(credential);
				auth.setLastAccessedDate(new Date());
				accountAuthRepository.save(auth);
				account = auth.getAccount();
			}
			break;

		default:
		}
		return account;
	}

	@Override
	public Account createAccount(String username, String password, Role role) {
		return createAccount(AuthType.USERNAME, username, password, null, AccountAuth.EXPIRES_NERVER, role);
	}

	@Override
	public Account createAccount(String mobile, String password, String verificationCode, Role role) {
		return createAccount(AuthType.MOBILE, mobile, password, verificationCode, AccountAuth.EXPIRES_NERVER, role);
	}

	private Account createAccount(AuthType type, String principal, String credential, String verificationCode,
			long expires, Role role) {
		// user will only provide mobile to register an account
		if (type == AuthType.MOBILE && Strings.isBlank(verificationCode)) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					String.format("Invalid verification code: %s", verificationCode));
		}
		if (role == Role.ADMIN) {
			throw new AirException(Codes.ACCOUNT_CREATION_FAILURE, "Cannot create an admin account");
		}
		return doCreateAccount(type, principal, credential, verificationCode, expires, role);
	}

	private Account doCreateAccount(AuthType type, String principal, String credential, String verificationCode,
			long expires, Role role) {
		AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(type, principal);
		if (auth != null) {
			throw new AirException(Codes.ACCOUNT_ALREADY_EXISTS,
					String.format("Account with %s: %s is already exists", type, principal));
		}
		// create a new account
		Account newAccount = null;
		switch (role) {
		case ADMIN:
			newAccount = new Account();
			break;

		case TENANT:
			newAccount = new Tenant();
			break;

		case USER:
			newAccount = new User();
			break;

		default: // noop
		}
		// generate an api key
		newAccount.setNickName(principal); // TODO
		newAccount.setApiKey(UUIDs.shortRandom());
		newAccount.setRole(role);
		newAccount.setCreationDate(new Date());
		newAccount.setStatus(Status.ENABLED);

		// auth type
		boolean verified = false;
		switch (type) {
		// no need to be verified
		case USERNAME:
			newAccount.setPassword(passwordEncoder.encode(credential));
			verified = true;
			expires = AccountAuth.EXPIRES_NERVER;
			// credential not used
			credential = null;
			break;

		// need to be verified later via email
		case EMAIL:
			newAccount.setPassword(passwordEncoder.encode(credential));
			// expires: defines the verification code lifespan
			expires = (expires == 0 ? AccountAuth.EXPIRES_IN_ONE_DAY : expires);
			verified = false;
			// used as verification code and nulled-out after verified
			credential = UUIDs.shortRandom();
			break;

		// need to be verified before creation
		case MOBILE:
			verified = verificationService.verifyCode(principal, verificationCode);
			if (!verified) {
				throw new AirException(Codes.ACCOUNT_CREATION_FAILURE,
						String.format("Failed to create account with %s: %s, invalid verification code: %s", type,
								principal, verificationCode));
			}
			newAccount.setPassword(passwordEncoder.encode(credential));
			expires = AccountAuth.EXPIRES_NERVER;
			// credential not used
			credential = null;
			break;

		case WECHAT:
		case QQ:
		case WEIBO:
			verified = true;
			// TODO later: leave it AS-IS for now
			break;

		default:
			throw new AirException(Codes.ACCOUNT_CREATION_FAILURE, String
					.format("Failed to create account with %s: %s, unsupported auth type: %s", type, principal, type));
		}

		Account accountCreated = accountRepository.save(newAccount);
		if (type == AuthType.EMAIL) {
			sendConfirmationEmail(principal, credential, accountCreated, expires);
		}
		// create account auth info
		auth = new AccountAuth();
		auth.setAccount(accountCreated);
		auth.setCredential(credential);
		auth.setPrincipal(principal);
		auth.setType(type);
		auth.setExpires(expires);
		auth.setVerified(verified);
		auth.setCreationDate(new Date());
		auth.setLastAccessedDate(auth.getCreationDate());
		accountAuthRepository.save(auth);
		return accountCreated;
	}

	@Override
	public Account createAdminAccount(String username, String password) {
		return doCreateAccount(AuthType.USERNAME, username, password, null, AccountAuth.EXPIRES_NERVER, Role.ADMIN);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account updateAccount(String accountId, Account newAccount) {
		Account account = findAccount(accountId);
		// profile
		account.setNickName(newAccount.getNickName());
		account.setAvatar(newAccount.getAvatar());
		switch (account.getRole()) {
		case ADMIN:
			// noop
			break;

		case TENANT:
			Tenant newTenant = (Tenant) newAccount;
			Tenant tenant = (Tenant) account;
			tenant.setEmail(newTenant.getEmail());
			tenant.setWebsite(newTenant.getWebsite());
			tenant.setAddress(newTenant.getAddress());
			tenant.setHotline(newTenant.getHotline());
			tenant.setDescription(newTenant.getDescription());
			break;

		case USER:
			User newUser = (User) newAccount;
			User user = (User) account;
			user.setBirthday(newUser.getBirthday());
			user.setCity(newUser.getCity());
			user.setGender(newUser.getGender());
			user.setHobbies(newUser.getHobbies());
			user.setRealName(newUser.getRealName());
			break;

		default: // noop
		}
		return accountRepository.save(account);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account updateAccountStatus(String accountId, Status newStatus) {
		Account account = findAccount(accountId);
		if (account.getRole() == Role.ADMIN) {
			throw new AirException(Codes.ACCOUNT_UNAUTHORIZED_PERMISSION, "Cannot update admin account status");
		}
		account.setStatus(newStatus);
		return accountRepository.save(account);
	}

	// TODO enable cache
	@Override
	public Optional<Account> findAccountByApiKey(String apiKey) {
		Account account = accountRepository.findByApiKey(apiKey);
		return Optional.ofNullable(account);
	}

	@Cacheable(cacheNames = CACHE_NAME_ACCOUNT)
	@Override
	public Account findAccount(String accountId) {
		Account account = accountRepository.findOne(accountId);
		if (account == null) {
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, String.format("Account %s is not found", accountId));
		}
		return account;
	}

	@Override
	public List<AccountAuth> findAccountAuths(String accountId) {
		return accountAuthRepository.findByAccountId(accountId);
	}

	@Override
	public List<Address> listUserAddresses(String accountId) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			return Collections.emptyList();
		}
		return User.class.cast(account).getAddresses();
	}

	@Override
	public void addUserAddress(String accountId, Address address) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Address is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.addAddress(address);
		accountRepository.save(user);
	}

	@Override
	public void removeUserAddress(String accountId, String addressId) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Address is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.removeAddressById(addressId);
		accountRepository.save(user);
	}

	@Override
	public List<Passenger> listUserPassengers(String accountId) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			return Collections.emptyList();
		}
		return User.class.cast(account).getPassengers();
	}

	@Override
	public void addUserPassenger(String accountId, Passenger passenger) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Passenger is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.addPassenger(passenger);
		accountRepository.save(user);
	}

	@Override
	public void removeUserPassenger(String accountId, String passengerId) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Passenger is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.removePassengerById(passengerId);
		accountRepository.save(user);
	}

	@Override
	public Page<Account> listAccounts(int page, int pageSize) {
		return Pages.adapt(accountRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Account> listAccounts(Role role, int page, int pageSize) {
		if (role == null) {
			return listAccounts(page, pageSize);
		}
		return Pages.adapt(accountRepository.findByRole(role, Pages.createPageRequest(page, pageSize)));
	}

	// TODO list all/cleanup unconfirmed/expired accounts, need to resend email if confirm link expired

	@Override
	public void updateUsername(String accountId, String username) {
		AccountAuth existingAuth = accountAuthRepository.findByTypeAndPrincipal(AuthType.USERNAME, username);
		// username not equals with others's email
		if (existingAuth != null && !existingAuth.getAccount().getId().equals(accountId)) {
			throw new AirException(Codes.ACCOUNT_USERNAME_ALREADY_EXISTS,
					String.format("Username %s already exists", username));
		}
		if (existingAuth.getPrincipal().equals(username)) {
			return;
		}
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.USERNAME);
		auth.setPrincipal(username);
		accountAuthRepository.save(auth);
	}

	@Override
	public void updateEmail(String accountId, String email) {
		AccountAuth existingAuth = accountAuthRepository.findByTypeAndPrincipal(AuthType.EMAIL, email);
		// email not equals with others's email
		if (existingAuth != null && !existingAuth.getAccount().getId().equals(accountId)) {
			throw new AirException(Codes.ACCOUNT_EMAIL_ALREADY_EXISTS, String.format("Email %s already exists", email));
		}
		// send email to verify
		String verificationCode = UUIDs.shortRandom();
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
			Account account = findAccount(accountId);
			auth = new AccountAuth();
			auth.setAccount(account);
			auth.setType(AuthType.EMAIL);
			auth.setExpires(AccountAuth.EXPIRES_IN_ONE_DAY);
			auth.setVerified(false);
			auth.setCreationDate(new Date());
			auth.setLastAccessedDate(auth.getCreationDate());
		}
		auth.setPrincipal(email);
		auth.setCredential(verificationCode);
		accountAuthRepository.save(auth);
		Account account = findAccount(accountId);
		sendConfirmationEmail(email, verificationCode, account, AccountAuth.EXPIRES_IN_ONE_DAY);
	}

	private void sendConfirmationEmail(String email, String verificationCode, Account account, long expires) {
		// send credential via email to allow user to confirm
		String token = accessTokenService.generateToken(account.getId(), ImmutableMap.of(Claims.CLAIM_ROLES,
				ImmutableSet.of(account.getRole().name()), Claims.CLAIM_EXPIRY, expires));
		String link = String.format(EMAIL_CONFIRMATION_LINK_FORMAT, token, verificationCode);
		// TODO build email body and full confirmation link
		mailService.sendMail(email, "Email Confirm", "TODO: build and email body with full link: " + link);
	}

	@Override
	public void confirmEmail(String accountId, String verificationCode) {
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, String.format("Account %s is not found", accountId));
		}
		Instant expiryDate = auth.getCreationDate().toInstant().plusSeconds(auth.getExpires());
		boolean exipired = expiryDate.isAfter(Instant.now());
		if (!auth.getCredential().equals(verificationCode) || exipired) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					String.format("Invalid verification code: %s", verificationCode));
		}
		auth.setVerified(true);
		auth.setCredential(null);
		accountAuthRepository.save(auth);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account updatePassword(String accountId, String oldPassword, String newPassword) {
		Account account = findAccount(accountId);
		boolean matches = passwordEncoder.matches(oldPassword, account.getPassword());
		if (!matches) {
			throw new AirException(Codes.ACCOUNT_PASSWORD_MISMATCH, "Password is mismatch");
		}
		account.setPassword(passwordEncoder.encode(newPassword));
		return accountRepository.save(account);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account resetPasswordTo(String accountId, String newPassword) {
		Account account = findAccount(accountId);
		account.setPassword(passwordEncoder.encode(newPassword));
		return accountRepository.save(account);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account resetPassword(String accountId) {
		Account account = findAccount(accountId);
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_EMAIL_NOT_BIND, "Email is not set");
		}
		String rndPassword = Randoms.randomAlphanumeric(PASSWORD_LENGTH);
		account.setPassword(passwordEncoder.encode(rndPassword));
		Account accountUpdated = accountRepository.save(account);
		// TODO
		mailService.sendMail(auth.getPrincipal(), "Reset Password",
				"TODO: build and email body with password inside: " + rndPassword);
		return accountUpdated;
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account refreshApiKey(String accountId) {
		Account account = findAccount(accountId);
		account.setApiKey(UUIDs.shortRandom());
		return accountRepository.save(account);
	}

	@CacheEvict(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public void deleteAccount(String accountId) {
		Account account = accountRepository.findOne(accountId);
		if (account != null && account.getRole() != Role.ADMIN) {
			// delete account
			// TODO delete all the data related to this account
			accountAuthRepository.deleteByAccountId(account.getId());
			accountRepository.delete(account);
			LOG.info("Delete account: {}", account);
		}
	}
}
