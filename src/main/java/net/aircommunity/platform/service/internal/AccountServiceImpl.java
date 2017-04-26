package net.aircommunity.platform.service.internal;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import io.micro.common.Randoms;
import io.micro.common.Strings;
import io.micro.common.UUIDs;
import io.micro.common.crypto.password.PasswordEncoder;
import io.micro.core.security.AccessTokenService;
import io.micro.core.security.Claims;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
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
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.MailService;
import net.aircommunity.platform.service.TemplateService;
import net.aircommunity.platform.service.VerificationService;

/**
 * Account service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

	private static final String CACHE_NAME_ACCOUNT = "cache.account";
	// private static final String CACHE_NAME_APIKEY = "cache.account_apikey";

	// FORMAT: http://host:port/context/api-version/account/email/confirm?token=xxx&code=xxx
	private static final String EMAIL_CONFIRMATION_LINK_BASE_FORMAT = "http://%s:%d%s/%s/account/email/confirm?%s";
	private static final String EMAIL_CONFIRMATION_LINK_PARAMS = "token=%s&code=%s";
	private static final String EMAIL_BINDING_USERNAME = "username";
	private static final String EMAIL_BINDING_COMPANY = "company";
	private static final String EMAIL_BINDING_WEBSITE = "website";
	private static final String EMAIL_BINDING_VERIFICATIONLINK = "verificationLink";
	private static final String EMAIL_BINDING_RNDPASSWORD = "rndPassword";
	private static final String VERIFICATION_CODE_SEPARATOR = ":";
	private static final String VERIFICATION_CODE_FORMAT = "%s" + VERIFICATION_CODE_SEPARATOR + "%s"; // code:timestamp

	// private static final String EMAIL_BINDING_RESETPASSWORDLINK = "resetPasswordLink";
	private static final int PASSWORD_LENGTH = 20;

	@Resource
	private Configuration configuration;

	@Resource
	private AccountRepository accountRepository;

	@Resource
	private PassengerRepository passengerRepository;

	@Resource
	private AccountAuthRepository accountAuthRepository;

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private MailService mailService;

	@Resource
	private TemplateService templateService;

	@Resource
	private VerificationService verificationService;

	@Resource
	private AccessTokenService accessTokenService;

	@Resource
	private AirBBAccountService airBBAccountService;

	private String emailConfirmationLink;

	@PostConstruct
	private void init() {
		AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(AuthType.USERNAME,
				Constants.DEFAULT_ADMIN_USERNAME);
		if (auth == null) {
			createAdminAccount(Constants.DEFAULT_ADMIN_USERNAME, Constants.DEFAULT_ADMIN_PASSWORD);
			LOG.debug("Created default admin account");
		}
		emailConfirmationLink = String.format(EMAIL_CONFIRMATION_LINK_BASE_FORMAT, configuration.getPublicHost(),
				configuration.getPublicPort(), configuration.getContextPath(), configuration.getApiVersion(),
				EMAIL_CONFIRMATION_LINK_PARAMS);
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
			if (configuration.isMobileVerificationEnabled()) {
				valid = verificationService.verifyCode(credential, credential);
			}
			else {
				LOG.warn("Mobiel verification is not enabled, verification skipped.");
				valid = true;
			}
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

		default: // noop
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
		newAccount.setNickName(principal);
		newAccount.setApiKey(UUIDs.shortRandom());
		newAccount.setRole(role);
		newAccount.setCreationDate(new Date());
		newAccount.setStatus(Status.ENABLED);
		newAccount.setAvatar(configuration.getDefaultAvatar());

		// save for nodebb user
		String password = credential;

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
			if (configuration.isMobileVerificationEnabled()) {
				verified = verificationService.verifyCode(principal, verificationCode);
				if (!verified) {
					throw new AirException(Codes.ACCOUNT_CREATION_FAILURE,
							String.format("Failed to create account with %s: %s, invalid verification code: %s", type,
									principal, verificationCode));
				}
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
		// create account in NodeBB
		airBBAccountService.createAccount(principal, password);
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
		List<Address> addresses = User.class.cast(account).getAddresses();
		return addresses.stream().collect(Collectors.toList());
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account addUserAddress(String accountId, Address address) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Address is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.addAddress(address);
		return accountRepository.save(user);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account removeUserAddress(String accountId, String addressId) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Address is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.removeAddressById(addressId);
		return accountRepository.save(user);
	}

	@Override
	public List<Passenger> listUserPassengers(String accountId) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			return Collections.emptyList();
		}
		List<Passenger> passengers = User.class.cast(account).getPassengers();
		return passengers.stream().collect(Collectors.toList());
	}

	// TODO REMOVE
	// @CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Passenger addUserPassenger(String accountId, Passenger passenger) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Passenger is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.addPassenger(passenger);
		User userSaved = accountRepository.save(user);
		Optional<Passenger> passengerAdded = userSaved.getPassengers().stream()
				.filter(p -> p.getIdentity().equals(passenger.getIdentity())).findFirst();
		if (!passengerAdded.isPresent()) {
			throw new AirException(Codes.ACCOUNT_ADD_PASSENGER_FAILURE,
					String.format("Add passenger failure: %s", passenger.getIdentity()));
		}
		return passengerAdded.get();
	}

	// TODO REMOVE
	// @CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public void removeUserPassenger(String accountId, String passengerId) {
		Account account = findAccount(accountId);
		if (account.getRole() != Role.USER) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					String.format("Passenger is not allowed for account %s", account.getNickName()));
		}
		User user = User.class.cast(account);
		user.removePassengerById(passengerId);
		passengerRepository.delete(passengerId);
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
		auth.setCredential(String.format(VERIFICATION_CODE_FORMAT, verificationCode, System.currentTimeMillis()));
		accountAuthRepository.save(auth);
		Account account = findAccount(accountId);
		sendConfirmationEmail(email, verificationCode, account, AccountAuth.EXPIRES_IN_ONE_DAY);

		// update NodeBB user email
		airBBAccountService.updateAccountProfile(account.getNickName(), email);
	}

	/**
	 * Send credential via email to allow user to confirm
	 */
	private void sendConfirmationEmail(String email, String verificationCode, Account account, long expires) {
		String token = accessTokenService.generateToken(account.getId(), ImmutableMap.of(Claims.CLAIM_ROLES,
				ImmutableSet.of(account.getRole().name()), Claims.CLAIM_EXPIRY, expires));
		String verificationLink = String.format(emailConfirmationLink, token, verificationCode);
		Map<String, Object> bindings = new HashMap<>(4);
		bindings.put(EMAIL_BINDING_USERNAME, account.getNickName());
		bindings.put(EMAIL_BINDING_COMPANY, configuration.getCompany());
		bindings.put(EMAIL_BINDING_WEBSITE, configuration.getWebsite());
		bindings.put(EMAIL_BINDING_VERIFICATIONLINK, verificationLink);
		String mailVerificationBody = templateService.renderFile(Constants.TEMPLATE_MAIL_VERIFICATION, bindings);
		mailService.sendMail(email, configuration.getMailVerificationSubject(), mailVerificationBody);
	}

	@Override
	public void confirmEmail(String accountId, String verificationCode) {
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, String.format("Account %s is not found", accountId));
		}
		if (auth.isVerified()) {
			LOG.warn("Account {} {} is already verified", auth.getPrincipal(), auth.getType());
			return;
		}
		String credential = auth.getCredential();
		if (Strings.isBlank(credential) || Strings.isBlank(verificationCode)) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					String.format("Invalid verification code: %s", verificationCode));
		}
		LOG.debug("Auth expires: {}", auth.getExpires());
		String[] parts = credential.split(VERIFICATION_CODE_SEPARATOR);
		String code = parts[0];
		long codeTimestamp = 0;
		try {
			// test to backward compatible
			if (parts.length >= 2) {
				codeTimestamp = Long.valueOf(parts[1]);
			}
		}
		catch (Exception e) {
			LOG.warn("Failed to get timestamp: " + e.getMessage(), e);
		}
		long expiry = auth.getExpires() * 1000 + codeTimestamp;
		long now = System.currentTimeMillis();
		boolean exipired = expiry < now;
		LOG.debug("Auth code: {}, expiry: {}, exipired: {}", code, expiry, exipired);
		if (!verificationCode.equals(code) || exipired) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					String.format("Invalid verification code: %s or expired", verificationCode));
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

		// update NodeBB account password
		airBBAccountService.updateAccountPassword(account.getNickName(), newPassword);
		return accountRepository.save(account);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account resetPasswordTo(String accountId, String newPassword) {
		Account account = findAccount(accountId);
		account.setPassword(passwordEncoder.encode(newPassword));
		return accountRepository.save(account);
	}

	@Override
	public Account resetPasswordViaMobile(String mobile, String newPassword) {
		AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(AuthType.MOBILE, mobile);
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_MOBILE_NOT_FOUND, String.format("Mobile %s not found", mobile));
		}
		Account account = auth.getAccount();
		account.setPassword(passwordEncoder.encode(newPassword));
		return accountRepository.save(account);
	}

	@CachePut(cacheNames = CACHE_NAME_ACCOUNT, key = "#accountId")
	@Override
	public Account resetPasswordViaEmail(String accountId) {
		Account account = findAccount(accountId);
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_EMAIL_NOT_BIND,
					"Email is not bound, cannot reset password via email.");
		}
		String rndPassword = Randoms.randomAlphanumeric(PASSWORD_LENGTH);
		account.setPassword(passwordEncoder.encode(rndPassword));
		Account accountUpdated = accountRepository.save(account);
		Map<String, Object> bindings = new HashMap<>(4);
		bindings.put(EMAIL_BINDING_USERNAME, account.getNickName());
		bindings.put(EMAIL_BINDING_COMPANY, configuration.getCompany());
		bindings.put(EMAIL_BINDING_WEBSITE, configuration.getWebsite());
		bindings.put(EMAIL_BINDING_RNDPASSWORD, rndPassword);
		// bindings.put(EMAIL_BINDING_RESETPASSWORDLINK, "#TODO"); // XXX NOT USED FOR NOW
		String mailBody = templateService.renderFile(Constants.TEMPLATE_MAIL_RESET_PASSOWRD, bindings);
		mailService.sendMail(auth.getPrincipal(), configuration.getMailResetPasswordSubject(), mailBody);
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

			// delete NodeBB account
			airBBAccountService.deleteAccount(account.getNickName());
			LOG.info("Delete account: {}", account);
		}
	}
}