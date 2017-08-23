package net.aircommunity.platform.service.internal.security;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.micro.common.Randoms;
import io.micro.common.Strings;
import io.micro.common.UUIDs;
import io.micro.common.crypto.password.PasswordEncoder;
import io.micro.common.tuple.Pair;
import io.micro.core.security.AccessTokenService;
import io.micro.core.security.Claims;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.AuthContext;
import net.aircommunity.platform.model.DomainEvent;
import net.aircommunity.platform.model.DomainEvent.DomainType;
import net.aircommunity.platform.model.DomainEvent.Operation;
import net.aircommunity.platform.model.IdCardInfo;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PointRules;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.Account.Status;
import net.aircommunity.platform.model.domain.AccountAuth;
import net.aircommunity.platform.model.domain.AccountAuth.AuthType;
import net.aircommunity.platform.model.domain.Address;
import net.aircommunity.platform.model.domain.Admin;
import net.aircommunity.platform.model.domain.DailySignin;
import net.aircommunity.platform.model.domain.Passenger;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.model.domain.Tenant.VerificationStatus;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.model.metrics.AccountMetrics;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AccountAuthRepository;
import net.aircommunity.platform.repository.AccountRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.repository.TenantRepository;
import net.aircommunity.platform.repository.UserRepository;
import net.aircommunity.platform.service.MemberPointsService;
import net.aircommunity.platform.service.common.IdentityCardService;
import net.aircommunity.platform.service.common.MailService;
import net.aircommunity.platform.service.common.TemplateService;
import net.aircommunity.platform.service.common.VerificationService;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.product.CommonProductService;
import net.aircommunity.platform.service.security.AccountService;

/**
 * Account service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class AccountServiceImpl extends AbstractServiceSupport implements AccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

	private static final String CACHE_NAME = "cache.account";
	private static final String CACHE_NAME_APIKEY = "cache.account-apikey";
	// FORMAT: http://host:port/context/api-version/account/email/confirm?token=xxx&code=xxx
	private static final String EMAIL_CONFIRMATION_LINK_FORMAT = "/account/email/confirm?token=%s&code=%s";
	private static final String VERIFICATION_CODE_SEPARATOR = ":";
	private static final String VERIFICATION_CODE_FORMAT = "%s" + VERIFICATION_CODE_SEPARATOR + "%s"; // code:timestamp
	// private static final String EMAIL_BINDING_RESETPASSWORDLINK = "resetPasswordLink";
	private static final int PASSWORD_LENGTH = 20;

	private static final DomainEvent EVENT_DELETION = new DomainEvent(DomainType.ACCOUNT, Operation.DELETION);

	@Resource
	private AccountRepository accountRepository;

	@Resource
	private TenantRepository tenantRepository;

	@Resource
	private UserRepository userRepository;

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
	private IdentityCardService identityCardService;

	@Resource
	private MemberPointsService memberPointsService;

	@Resource
	private CommonProductService commonProductService;

	@Resource(name = "commonOrderService")
	private CommonOrderService commonOrderService;

	private String emailConfirmationLink;

	// TODO list all/cleanup unconfirmed/expired accounts, need to re-send email if confirm link expired

	@PostConstruct
	private void init() {
		try {
			AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(AuthType.USERNAME,
					Constants.DEFAULT_ADMIN_USERNAME);
			if (auth == null) {
				createAdminAccount(Constants.DEFAULT_ADMIN_USERNAME, Constants.DEFAULT_ADMIN_PASSWORD);
				LOG.debug("Created default admin account");
			}
		}
		catch (Exception e) {
			throw new AirException(Codes.INTERNAL_ERROR, M.msg(M.INTERNAL_SERVER_ERROR), e);
		}
		registerPushNotificationEvent(EnumSet.of(DomainType.POINT));
		emailConfirmationLink = configuration.getApiBaseUrl() + EMAIL_CONFIRMATION_LINK_FORMAT;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#result.id")
	@Override
	public Account authenticateAccount(String principal, String credential, AuthContext context) {
		AccountAuth auth = null;
		for (AuthType authType : AuthType.INTERNAL_AUTHS) {
			auth = accountAuthRepository.findByTypeAndPrincipal(authType, principal);
			if (auth != null) {
				auth = accountAccessed(auth, context);
				break;
			}
		}
		return doAuthenticate(principal, credential, context, auth).getAccount();
	}

	/**
	 * Perform authentication and return AccountAuth
	 */
	private @Nonnull AccountAuth doAuthenticate(String principal, String credential, AuthContext context,
			AccountAuth auth) {
		if (auth == null) {
			LOG.error("Failed to authenticate, account {} is not found", principal);
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, M.msg(M.ACCOUNT_NOT_FOUND));
		}
		Account account = auth.getAccount();
		if (account == null) {
			LOG.error("Failed to authenticate, account {} data corrupted, auth info exists but account is not found",
					principal);
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, M.msg(M.ACCOUNT_NOT_FOUND));
		}
		if (account.getStatus() != Status.ENABLED) {
			LOG.warn("Account {} is not enabled, cannot be authenticated", principal);
			throw new AirException(Codes.ACCOUNT_UNAUTHORIZED, M.msg(M.ACCOUNT_UNAUTHORIZED_DISABLED));
		}

		// OTP is available for mobile
		boolean valid = false;
		if (context.isOtp()) {
			if (configuration.isMobileVerificationEnabled()) {
				valid = verificationService.verifyCode(credential, credential);
			}
			else {
				LOG.warn("Mobile verification is not enabled, verification failed.");
			}
		}
		else {
			valid = passwordEncoder.matches(credential, account.getPassword());
		}
		if (!valid) {
			LOG.debug("Invalid credential for account: {}, when using auth: {}", principal, auth);
			throw new AirException(Codes.ACCOUNT_UNAUTHORIZED, M.msg(M.ACCOUNT_UNAUTHORIZED));
		}
		return auth;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#result.id")
	@Override
	public Account authenticateAccount(AuthType type, String principal, String credential, AuthContext context) {
		AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(type, principal);
		switch (type) {
		// internal auth
		case USERNAME:
		case EMAIL:
		case MOBILE:
			auth = doAuthenticate(principal, credential, context, auth);
			break;

		// external auth
		case WECHAT:
		case QQ:
		case WEIBO:
			if (auth == null) {
				// create account
				Pair<Account, AccountAuth> created = createAccount(type, principal, credential, null, context.expiry(),
						Role.USER);
				auth = created.getRight();
			}
			else {
				// update with new credential, e.g. access-token returned from wechat
				auth.setCredential(credential);
			}
			break;

		default: // noop
		}

		// record accessed
		if (auth != null) {
			auth = accountAccessed(auth, context);
		}
		return auth == null ? null : auth.getAccount();
	}

	private AccountAuth accountAccessed(AccountAuth auth, AuthContext context) {
		auth.setLastAccessedDate(new Date());
		auth.setLastAccessedIp(context.ipAddress());
		auth.setLastAccessedSource(context.source());
		// also update LastAccessedIp of account for quick lookup
		Account account = auth.getAccount();
		account.setLastAccessedIp(context.ipAddress());
		account.setLastAccessedDate(new Date());
		account = accountRepository.save(account);
		auth.setAccount(account);
		return accountAuthRepository.save(auth);
	}

	@Transactional
	@Override
	public Account createAccount(String username, String password, Role role) {
		Pair<Account, AccountAuth> pair = createAccount(AuthType.USERNAME, username, password, null,
				AccountAuth.EXPIRES_NERVER, role);
		return pair.getLeft();
	}

	@Transactional
	@Override
	public Account createAccount(String mobile, String password, String verificationCode, Role role) {
		Pair<Account, AccountAuth> pair = createAccount(AuthType.MOBILE, mobile, password, verificationCode,
				AccountAuth.EXPIRES_NERVER, role);
		return pair.getLeft();
	}

	private Pair<Account, AccountAuth> createAccount(AuthType type, String principal, String credential,
			String verificationCode, long expires, Role role) {
		// user will only provide mobile to register an account
		if (type == AuthType.MOBILE && Strings.isBlank(verificationCode)) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					M.msg(M.ACCOUNT_INVALID_VERIFICATION_CODE, verificationCode));
		}
		if (role == Role.ADMIN) {
			throw new AirException(Codes.ACCOUNT_CREATION_FAILURE, M.msg(M.ACCOUNT_CREATION_ADMIN_NOT_ALLOWED));
		}
		return doCreateAccount(type, principal, credential, verificationCode, expires, role);
	}

	private Pair<Account, AccountAuth> doCreateAccount(AuthType type, String principal, String credential,
			String verificationCode, long expires, Role role) {
		AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(type, principal);
		if (auth != null) {
			LOG.error("Account with {}: {} is already exists", type, principal);
			throw new AirException(Codes.ACCOUNT_ALREADY_EXISTS, M.msg(M.ACCOUNT_ALREADY_EXISTS, principal));
		}
		// create a new account
		Account newAccount = null;
		switch (role) {
		case ADMIN:
			newAccount = new Admin();
			newAccount.setRole(Role.ADMIN);
			break;

		case TENANT:
			newAccount = new Tenant();
			// TODO: make it UNVERIFIED by default later once tenant registration is enabled
			((Tenant) newAccount).setVerification(VerificationStatus.VERIFIED);
			newAccount.setRole(Role.TENANT);
			break;

		case CUSTOMER_SERVICE:
		case USER:
			newAccount = new User();
			newAccount.setRole(role);
			User u = (User) newAccount;
			u.setPoints(memberPointsService.getPointsEarnedFromRule(PointRules.ACCOUNT_REGISTRATION));
			break;

		default: // noop
		}
		// generate an api key
		newAccount.setNickName(principal);
		newAccount.setApiKey(UUIDs.shortRandom());
		newAccount.setCreationDate(new Date());
		newAccount.setStatus(Status.ENABLED);
		newAccount.setAvatar(configuration.getDefaultAvatar());

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
					LOG.error("Failed to create account with {}: {}, invalid verification code: {}", type, principal,
							verificationCode);
					throw new AirException(Codes.ACCOUNT_CREATION_FAILURE,
							M.msg(M.ACCOUNT_CREATION_INVALID_VERIFICATION_CODE, verificationCode));
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
			// XXX NOTE: NOT implemented, LEAVE IT AS-IS FOR NOW
			break;

		default:
			LOG.error("Failed to create account with {}: {}, unsupported auth type: {}", type, principal, type);
			throw new AirException(Codes.ACCOUNT_CREATION_FAILURE, M.msg(M.ACCOUNT_CREATION_FAILURE));
		}

		try {
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
			auth = accountAuthRepository.save(auth);
			return Pair.of(accountCreated, auth);
		}
		catch (Exception e) {
			LOG.error(String.format("Create account with auth: %s, principal: %s failed, casue: %s", type, principal,
					e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	@Transactional
	@Override
	public Account createAdminAccount(String username, String password) {
		Pair<Account, AccountAuth> pair = doCreateAccount(AuthType.USERNAME, username, password, null,
				AccountAuth.EXPIRES_NERVER, Role.ADMIN);
		return pair.getLeft();
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account updateAccount(String accountId, Account newAccount) {
		Account account = findAccount(accountId);
		// profile
		account.setNickName(newAccount.getNickName());
		account.setAvatar(newAccount.getAvatar());
		switch (account.getRole()) {
		case ADMIN:
			Admin newAdmin = (Admin) newAccount;
			Admin admin = (Admin) account;
			admin.setAvatar(newAdmin.getAvatar());
			admin.setNickName(newAdmin.getNickName());
			break;

		case TENANT:
			Tenant newTenant = (Tenant) newAccount;
			Tenant tenant = (Tenant) account;
			tenant.setNickName(newTenant.getNickName());
			tenant.setAvatar(newTenant.getAvatar());
			tenant.setEmail(newTenant.getEmail());
			tenant.setWebsite(newTenant.getWebsite());
			tenant.setAddress(newTenant.getAddress());
			tenant.setHotline(newTenant.getHotline());
			tenant.setDescription(newTenant.getDescription());
			break;

		case USER:
			User newUser = (User) newAccount;
			User user = (User) account;
			user.setNickName(newUser.getNickName());
			user.setAvatar(newUser.getAvatar());
			user.setRealName(newUser.getRealName());
			user.setBirthday(newUser.getBirthday());
			user.setGender(newUser.getGender());
			user.setCountry(newUser.getCountry());
			user.setProvince(newUser.getProvince());
			user.setCity(newUser.getCity());
			user.setHobbies(newUser.getHobbies());
			String identity = newUser.getIdentity();
			if (Strings.isNotBlank(identity)) {
				IdCardInfo cardInfo = identityCardService.getIdCardInfo(user.getIdentity(), user.getRealName());
				if (cardInfo == null) {
					throw new AirException(Codes.ACCOUNT_INVALID_IDCARD, M.msg(M.ACCOUNT_INVALID_IDCARD));
				}
				// update data with the real info retrieved
				user.setIdentity(cardInfo.getIdentity());
				user.setBirthday(cardInfo.getBirthday());
				user.setRealName(cardInfo.getName());
				user.setGender(cardInfo.getGender());
			}
			break;

		default: // noop
		}
		Account updated = safeExecute(() -> accountRepository.save(account), "Update account %s to %s failed",
				accountId, newAccount);
		postDomainEvent(new DomainEvent(DomainType.ACCOUNT, Operation.UPDATE));
		// some may only listen for tenant update event
		if (account.getRole() == Role.TENANT) {
			postDomainEvent(new DomainEvent(DomainType.TENANT, Operation.UPDATE));
		}
		return updated;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account updateUserRole(String accountId, Role newRole) {
		if (newRole == null) {
			throw new AirException(Codes.ACCOUNT_INVALID_ROLE, M.msg(M.ACCOUNT_INVALID_ROLE));
		}
		Account account = findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			LOG.warn("Only can update user account role, but account {} role is {}", accountId, account.getRole());
			throw new AirException(Codes.ACCOUNT_PERMISSION_DENIED, M.msg(M.ACCOUNT_UPDATE_ROLE_NOT_ALLOWED));
		}
		account.setRole(newRole);
		return safeExecute(() -> accountRepository.save(account), "Update account %s role to %s failed", accountId,
				newRole);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account updateAccountStatus(String accountId, Status newStatus) {
		Account account = findAccount(accountId);
		if (account.getRole() == Role.ADMIN) {
			LOG.warn("Cannot update admin account {} status", account);
			throw new AirException(Codes.ACCOUNT_PERMISSION_DENIED, M.msg(M.ACCOUNT_UPDATE_ADMIN_STATUS_NOT_ALLOWED));
		}
		account.setStatus(newStatus);
		return safeExecute(() -> accountRepository.save(account), "Update account %s status to %s failed", accountId,
				newStatus);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account updateTenantVerificationStatus(String accountId, VerificationStatus newStatus,
			String verificationResult) {
		Account account = findAccount(accountId);
		if (!Tenant.class.isAssignableFrom(account.getClass())) {
			LOG.warn("Tenant account required, but was: {}", account);
			throw new AirException(Codes.ACCOUNT_NOT_TENANT, M.msg(M.ACCOUNT_NOT_TENANT));
		}
		Tenant tenant = (Tenant) account;
		tenant.setVerificationResult(verificationResult);
		tenant.setVerification(newStatus);
		return safeExecute(() -> accountRepository.save(account),
				"Update tenant account %s VerificationStatus to %s failed", accountId, newStatus);
	}

	@Cacheable(cacheNames = CACHE_NAME_APIKEY)
	@Override
	public Optional<Account> findAccountByApiKey(String apiKey) {
		Account account = accountRepository.findByApiKey(apiKey);
		return Optional.ofNullable(account);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Account findAccount(String accountId) {
		Account account = accountRepository.findOne(accountId);
		if (account == null) {
			LOG.error("Failed to find account, account {} is not found", accountId);
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, M.msg(M.ACCOUNT_NOT_FOUND));
		}
		return account;
	}

	@Override
	public List<AccountAuth> findAccountAuths(String accountId) {
		return accountAuthRepository.findByAccountId(accountId);
	}

	@Override
	public AccountAuth findAccountMobile(String accountId) {
		return accountAuthRepository.findByAccountIdAndType(accountId, AuthType.MOBILE);
	}

	@Override
	public AccountAuth findAccountUsername(String accountId) {
		return accountAuthRepository.findByAccountIdAndType(accountId, AuthType.USERNAME);
	}

	@Override
	public AccountAuth findAccountEmail(String accountId) {
		return accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
	}

	@Override
	public List<Address> listUserAddresses(String accountId) {
		Account account = findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			return Collections.emptyList();
		}
		List<Address> addresses = User.class.cast(account).getAddresses();
		return addresses.stream().collect(Collectors.toList());
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account addUserAddress(String accountId, Address address) {
		Account account = findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					M.msg(M.ACCOUNT_ADDRESS_NOT_ALLOWED, account.getNickName()));
		}
		User user = User.class.cast(account);
		user.addAddress(address);
		return safeExecute(() -> accountRepository.save(user), "Add user %s address %s failed", accountId, address);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account removeUserAddress(String accountId, String addressId) {
		Account account = findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			throw new AirException(Codes.ACCOUNT_ADDRESS_NOT_ALLOWED,
					M.msg(M.ACCOUNT_ADDRESS_NOT_ALLOWED, account.getNickName()));
		}
		User user = User.class.cast(account);
		user.removeAddressById(addressId);
		return safeExecute(() -> accountRepository.save(user), "Remove user %s address %s failed", accountId,
				addressId);
	}

	@Override
	public List<Passenger> listUserPassengers(String accountId) {
		Account account = findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			return Collections.emptyList();
		}
		List<Passenger> passengers = User.class.cast(account).getPassengers();
		// lazily load make copy
		return passengers.stream().map(Passenger::clone).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public Passenger addUserPassenger(String accountId, Passenger passenger) {
		Account account = findAccount(accountId);
		// pre-check if account is user
		if (!User.class.isAssignableFrom(account.getClass())) {
			throw new AirException(Codes.ACCOUNT_PASSENGER_NOT_ALLOWED,
					M.msg(M.ACCOUNT_PASSENGER_NOT_ALLOWED, account.getNickName()));
		}
		// check ID Card existence
		Passenger passengerFound = passengerRepository.findByOwnerIdAndIdentity(accountId, passenger.getIdentity());
		if (passengerFound != null) {
			throw new AirException(Codes.ACCOUNT_PASSENGER_ALREADY_EXISTS,
					M.msg(M.PASSENGER_ALREADY_EXISTS, passenger.getIdentity()));
		}
		// verify ID Card
		boolean valid = identityCardService.verifyIdentityCard(passenger.getIdentity(), passenger.getName());
		if (!valid) {
			throw new AirException(Codes.ACCOUNT_ADD_PASSENGER_FAILURE, M.msg(M.ACCOUNT_INVALID_IDCARD));
		}

		// perform passenger add
		User user = User.class.cast(account);
		user.addPassenger(passenger);
		return safeExecute(() -> {
			User userSaved = accountRepository.save(user);
			Optional<Passenger> passengerAdded = userSaved.getPassengers().stream()
					.filter(p -> p.getIdentity().equals(passenger.getIdentity())).findFirst();
			if (!passengerAdded.isPresent()) {
				throw new AirException(Codes.ACCOUNT_ADD_PASSENGER_FAILURE,
						M.msg(M.ACCOUNT_ADD_PASSENGER_FAILURE, passenger.getIdentity()));
			}
			return passengerAdded.get();
		}, "Add user %s passenger %s failed", accountId, passenger);
	}

	@Transactional
	@Override
	public void removeUserPassenger(String accountId, String passengerId) {
		Account account = findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			throw new AirException(Codes.ACCOUNT_PASSENGER_NOT_ALLOWED,
					M.msg(M.ACCOUNT_PASSENGER_NOT_ALLOWED, account.getNickName()));
		}
		User user = User.class.cast(account);
		user.removePassengerById(passengerId);
		safeExecute(() -> {
			passengerRepository.delete(passengerId);
			accountRepository.save(user);
		}, "Remove user %s passenger %s failed", accountId, passengerId);
	}

	@Override
	public Page<Account> listAccounts(int page, int pageSize) {
		return Pages.adapt(accountRepository.findAll(createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Account> listAccounts(Role role, int page, int pageSize) {
		if (role == null) {
			return listAccounts(page, pageSize);
		}
		return Pages.adapt(accountRepository.findByRole(role, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<User> listUserAccounts(Role role, int page, int pageSize) {
		if (role == null) {
			return Pages.adapt(userRepository.findAll(createPageRequest(page, pageSize)));
		}
		return Pages.adapt(userRepository.findByRole(role, createPageRequest(page, pageSize)));
	}

	@Override
	public Page<Tenant> listTenantAccounts(Role role, int page, int pageSize) {
		if (role == null) {
			return Pages.adapt(tenantRepository.findAll(createPageRequest(page, pageSize)));
		}
		return Pages.adapt(tenantRepository.findByRole(role, createPageRequest(page, pageSize)));
	}

	// private <T extends Account> Page<T> transformAccounts(Page<T> data) {
	// data.getContent().stream().forEach(this::decorateAccount);
	// return data;
	// }
	//
	// private Account decorateAccount(Account account) {
	// List<AccountAuth> auths = findAccountAuths(account.getId());
	// account.setAuths(auths);
	// return account;
	// }

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account updateUserPoints(String accountId, long deltaPoints) {
		User user = findAccount(accountId, User.class);
		user.setPoints(user.getPoints() + deltaPoints);
		DomainEvent de = new DomainEvent(DomainType.POINT, Operation.PUSH_NOTIFICATION);
		de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_ACCOUNTID, accountId);
		postDomainEvent(de);
		return safeExecute(() -> accountRepository.save(user), "Update user %s points with delta %s failed", accountId,
				deltaPoints);
	}

	@Transactional
	@Override
	public void updateUsername(String accountId, String username) {
		AccountAuth existingAuth = accountAuthRepository.findByTypeAndPrincipal(AuthType.USERNAME, username);
		// username not equals with others's email
		if (existingAuth != null && !existingAuth.getAccount().getId().equals(accountId)) {
			throw new AirException(Codes.ACCOUNT_USERNAME_ALREADY_EXISTS,
					M.msg(M.ACCOUNT_USERNAME_ALREADY_EXISTS, username));
		}
		if (existingAuth.getPrincipal().equals(username)) {
			return;
		}
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.USERNAME);
		auth.setPrincipal(username);
		safeExecute(() -> accountAuthRepository.save(auth), "Update account %s username to %s failed", accountId,
				username);
	}

	@Transactional
	@Override
	public void updateEmail(String accountId, String email) {
		Account account = findAccount(accountId);
		AccountAuth existingAuth = accountAuthRepository.findByTypeAndPrincipal(AuthType.EMAIL, email);
		// email not equals with others's email
		if (existingAuth != null && !existingAuth.getAccount().getId().equals(accountId)) {
			throw new AirException(Codes.ACCOUNT_EMAIL_ALREADY_EXISTS, M.msg(M.ACCOUNT_EMAIL_ALREADY_EXISTS, email));
		}
		// send email to verify
		String verificationCode = UUIDs.shortRandom();
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
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
		final AccountAuth authToSave = auth;
		safeExecute(() -> accountAuthRepository.save(authToSave), "Update account %s email to %s failed", accountId,
				email);
		sendConfirmationEmail(email, verificationCode, account, AccountAuth.EXPIRES_IN_ONE_DAY);
	}

	/**
	 * Send credential via email to allow user to confirm
	 */
	private void sendConfirmationEmail(String email, String verificationCode, Account account, long expires) {
		String token = accessTokenService.generateToken(account.getId(), ImmutableMap.of(Claims.CLAIM_ROLES,
				ImmutableSet.of(account.getRole().name()), Claims.CLAIM_EXPIRY, expires));
		String verificationLink = String.format(emailConfirmationLink, token, verificationCode);
		Map<String, Object> bindings = new HashMap<>(4);
		bindings.put(Constants.TEMPLATE_BINDING_USERNAME, account.getNickName());
		bindings.put(Constants.TEMPLATE_BINDING_COMPANY, configuration.getCompany());
		bindings.put(Constants.TEMPLATE_BINDING_WEBSITE, configuration.getWebsite());
		bindings.put(Constants.TEMPLATE_BINDING_VERIFICATIONLINK, verificationLink);
		String mailVerificationBody = templateService.renderFile(Constants.TEMPLATE_MAIL_VERIFICATION, bindings);
		mailService.sendMail(email, configuration.getMailVerificationSubject(), mailVerificationBody);
	}

	@Transactional
	@Override
	public void confirmEmail(String accountId, String verificationCode) {
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
			LOG.warn("Failed to confirm email, account {} is not found", accountId);
			throw new AirException(Codes.ACCOUNT_NOT_FOUND, M.msg(M.ACCOUNT_NOT_FOUND));
		}
		if (auth.isVerified()) {
			throw new AirException(Codes.ACCOUNT_EMAIL_ALREADY_VERIFIED,
					M.msg(M.ACCOUNT_EMAIL_ALREADY_VERIFIED, auth.getPrincipal()));
		}
		String credential = auth.getCredential();
		if (Strings.isBlank(credential) || Strings.isBlank(verificationCode)) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					M.msg(M.ACCOUNT_INVALID_VERIFICATION_CODE, verificationCode));
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
					M.msg(M.ACCOUNT_INVALID_VERIFICATION_CODE, verificationCode));
		}
		auth.setVerified(true);
		auth.setCredential(null);
		safeExecute(() -> accountAuthRepository.save(auth), "Confirm account %s email failed", accountId);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account updatePassword(String accountId, String oldPassword, String newPassword) {
		Account account = findAccount(accountId);
		boolean matches = passwordEncoder.matches(oldPassword, account.getPassword());
		if (!matches) {
			throw new AirException(Codes.ACCOUNT_PASSWORD_MISMATCH, M.msg(M.ACCOUNT_PASSWORD_MISMATCH));
		}
		account.setPassword(passwordEncoder.encode(newPassword));
		return safeExecute(() -> accountRepository.save(account), "Update account %s password failed", accountId);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account resetPasswordTo(String accountId, String newPassword) {
		Account account = findAccount(accountId);
		account.setPassword(passwordEncoder.encode(newPassword));
		return safeExecute(() -> accountRepository.save(account), "Reset account %s password failed", accountId);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#result.id")
	@Override
	public Account resetPasswordViaMobile(String mobile, String newPassword) {
		AccountAuth auth = accountAuthRepository.findByTypeAndPrincipal(AuthType.MOBILE, mobile);
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_MOBILE_NOT_FOUND, M.msg(M.ACCOUNT_MOBILE_NOT_FOUND, mobile));
		}
		Account account = auth.getAccount();
		account.setPassword(passwordEncoder.encode(newPassword));
		return safeExecute(() -> accountRepository.save(account), "Reset account password via mobile %s failed",
				mobile);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account resetPasswordViaEmail(String accountId) {
		Account account = findAccount(accountId);
		AccountAuth auth = accountAuthRepository.findByAccountIdAndType(accountId, AuthType.EMAIL);
		if (auth == null) {
			throw new AirException(Codes.ACCOUNT_EMAIL_NOT_BIND, M.msg(M.ACCOUNT_EMAIL_NOT_BIND));
		}
		String rndPassword = Randoms.randomAlphanumeric(PASSWORD_LENGTH);
		account.setPassword(passwordEncoder.encode(rndPassword));
		Account updated = accountRepository.save(account);
		Map<String, Object> bindings = new HashMap<>(4);
		bindings.put(Constants.TEMPLATE_BINDING_USERNAME, account.getNickName());
		bindings.put(Constants.TEMPLATE_BINDING_COMPANY, configuration.getCompany());
		bindings.put(Constants.TEMPLATE_BINDING_WEBSITE, configuration.getWebsite());
		bindings.put(Constants.TEMPLATE_BINDING_RNDPASSWORD, rndPassword);
		// bindings.put(EMAIL_BINDING_RESETPASSWORDLINK, "xxx"); // XXX NOT USED FOR NOW
		String mailBody = templateService.renderFile(Constants.TEMPLATE_MAIL_RESET_PASSOWRD, bindings);
		mailService.sendMail(auth.getPrincipal(), configuration.getMailResetPasswordSubject(), mailBody);
		return updated;
	}

	@Transactional
	@Caching(put = @CachePut(cacheNames = CACHE_NAME, key = "#accountId"), evict = @CacheEvict(cacheNames = CACHE_NAME_APIKEY))
	@Override
	public Account refreshApiKey(String accountId) {
		Account account = findAccount(accountId);
		account.setApiKey(UUIDs.shortRandom());
		return safeExecute(() -> accountRepository.save(account), "Refresh account %s apikey failed", accountId);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#accountId")
	@Override
	public Account dailySignin(String accountId) {
		Account account = findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			LOG.warn("Signin ignored for none user account: {}", account);
			return account;
		}
		User user = User.class.cast(account);
		DailySignin dailySignin = user.getDailySignin();
		if (dailySignin == null) {
			dailySignin = new DailySignin();
		}
		// not signin yet
		if (!dailySignin.isSignin()) {
			Map<Integer, Long> rules = memberPointsService.getDailySigninPointRules();
			LOG.debug("Daily signin rules: {}", rules);
			int maxDailySignins = rules.isEmpty() ? 0 : rules.keySet().stream().mapToInt(x -> x).max().getAsInt();
			Long signin1Point = rules.get(1);
			long pointsEarned = signin1Point == null ? 0 : signin1Point;
			if (dailySignin.isConsecutive()) {
				dailySignin.increaseConsecutiveSignins();
				int consecutiveSignins = Math.min(dailySignin.getConsecutiveSignins(), maxDailySignins);
				Long points = rules.get(consecutiveSignins);
				if (points != null) {
					pointsEarned = points;
				}
			}
			else {
				// reset consecutive
				dailySignin.resetConsecutiveSignins();
			}
			LOG.debug("Got points: {}", pointsEarned);
			dailySignin.setLastSigninDate(new Date());
			dailySignin.setPointsEarned(pointsEarned);
			dailySignin.setSuccess(true);
			LOG.debug("Success: {}", dailySignin.isSuccess());
		}
		else {
			dailySignin.setSuccess(false);
		}
		user.setDailySignin(dailySignin);
		user.setPoints(user.getPoints() + dailySignin.getPointsEarned());
		User updated = safeExecute(() -> accountRepository.save(user), "Update user %s for signin", accountId);
		// we need set success state, because it's NOT persisted
		DailySignin updatedSignin = updated.getDailySignin();
		updatedSignin.setSuccess(dailySignin.isSuccess());
		updatedSignin.setPointsEarned(dailySignin.getPointsEarned());
		return updated;
	}

	@Transactional
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_APIKEY }, key = "#accountId")
	@Override
	public void deleteAccount(String accountId) {
		Account account = findAccount(accountId);
		switch (account.getRole()) {
		case ADMIN:
			LOG.error("Cannot delete an admin account: {}", account);
			throw new AirException(Codes.ACCOUNT_PERMISSION_DENIED, M.msg(M.ACCOUNT_ADMIN_CANNOT_BE_DELETED));

		case TENANT:
			// delete all products and everything of a tenant
			commonProductService.purgeProducts(account.getId());
			break;

		// both common user (can login via app)
		case CUSTOMER_SERVICE:
		case USER:
			// delete all orders and everything of a user
			commonOrderService.purgeOrders(account.getId());
			break;

		default:// noops
		}

		// delete all auth data related to this account
		accountAuthRepository.deleteByAccountId(account.getId());
		accountRepository.delete(account);
		LOG.info("Delete account: {}", account);
		postDomainEvent(EVENT_DELETION);
	}

	@Override
	public AccountMetrics getAccountMetrics() {
		long totalCount = accountRepository.count();
		long adminCount = accountRepository.countByRole(Role.ADMIN);
		long tenantCount = accountRepository.countByRole(Role.TENANT);
		long userCount = accountRepository.countByRole(Role.USER);
		long userCountThisMonth = userRepository.countThisMonth();
		AccountMetrics metrics = new AccountMetrics();
		metrics.setTotalCount(totalCount);
		metrics.setAdminCount(adminCount);
		metrics.setTenantCount(tenantCount);
		metrics.setUserCount(userCount);
		metrics.setUserCountThisMonth(userCountThisMonth);
		return metrics;
	}
}