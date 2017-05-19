package net.aircommunity.platform.rest;

import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.micro.annotation.TokenSecured;
import io.micro.common.Strings;
import io.micro.core.security.AccessTokenService;
import io.micro.core.security.Claims;
import io.micro.core.security.SimplePrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.AccessToken;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.AccountAuth;
import net.aircommunity.platform.model.AccountAuth.AuthType;
import net.aircommunity.platform.model.AccountRequest;
import net.aircommunity.platform.model.AuthcRequest;
import net.aircommunity.platform.model.EmailRequest;
import net.aircommunity.platform.model.PasswordRequest;
import net.aircommunity.platform.model.PasswordResetRequest;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.UserAccountRequest;
import net.aircommunity.platform.model.UsernameRequest;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.SmsService;
import net.aircommunity.platform.service.TemplateService;
import net.aircommunity.platform.service.VerificationService;

/**
 * Account RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Api
@Path("account")
public class AccountResource {
	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private AccountService accountService;

	@Resource
	private AccessTokenService accessTokenService;

	@Resource
	private VerificationService verificationService;

	@Resource
	private SmsService smsService;

	@Resource
	private TemplateService templateService;

	@Resource
	private Configuration configuration;

	// TODO AirQ
	@ApiOperation(value = "login", hidden = true)
	@GET
	@Path("login")
	@PermitAll
	@Produces(MediaType.TEXT_HTML)
	public Response login(@Context SecurityContext context) {
		SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
		LOG.debug("principal: {}", principal);
		String accountId = null;
		if (principal == null) {
			return Response.ok("access-denied").build();
		}
		else {
			accountId = principal.getName();
		}
		Account account = accountService.findAccount(accountId);
		LOG.debug("account: {}", account);
		String username = getUsername(account);
		LOG.debug("username: {}", username);
		String token = accessTokenService.generateToken(account.getId(),
				ImmutableMap.of(Claims.CLAIM_ROLES, ImmutableSet.of(account.getRole().name()), Claims.CLAIM_EXPIRY,
						configuration.getTokenExpirationTimeSeconds(), Constants.CLAIM_USERNAME, username));
		String domain = configuration.getPublicHost();

		LOG.debug("domain: {}", domain);
		domain = "aircommunity.net";
		int maxAge = (int) configuration.getTokenExpirationTimeSeconds();
		NewCookie cookie = new NewCookie("token", token, "/"/* path */, domain, NewCookie.DEFAULT_VERSION,
				null/* comment */, maxAge, null/* expiry */, false/* secure */, true/* httpOnly */);
		return Response.ok("ok").cookie(cookie).build();
	}

	/**
	 * AirQ
	 */
	// TODO AirQ
	@ApiOperation(value = "airq", response = AccessToken.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""), @ApiResponse(code = 403, message = "") })
	@GET
	@Path("airq")
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response airq(@Context SecurityContext context) {
		SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
		LOG.debug("principal: {}", principal);
		String accountId = null;
		if (principal == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		else {
			accountId = principal.getName();
		}
		Account account = accountService.findAccount(accountId);
		LOG.debug("account: {}", account);
		String username = getUsername(account);
		String token = accessTokenService.generateToken(username,
				ImmutableMap.of(Claims.CLAIM_ROLES, ImmutableSet.of(account.getRole().name()), Claims.CLAIM_EXPIRY,
						configuration.getTokenExpirationTimeSeconds(), Constants.CLAIM_USERNAME, username));
		return Response.ok(new AccessToken(token)).build();
	}

	private String getUsername(Account account) {
		AccountAuth accountAuthUsername = accountService.findAccountUsername(account.getId());
		if (accountAuthUsername != null) {
			return accountAuthUsername.getPrincipal();
		}
		AccountAuth accountAuthMobile = accountService.findAccountMobile(account.getId());
		String nickName = account.getNickName();
		if (Strings.isBlank(nickName)) {
			nickName = accountAuthMobile != null ? accountAuthMobile.getPrincipal() : M.msg(M.USERNAME_ANONYMOUS);
		}
		return nickName;
	}

	/**
	 * Create user (registration)
	 */
	@ApiOperation(value = "register")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "") })
	@POST
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUserAccount(@ApiParam(required = true) @NotNull @Valid UserAccountRequest request,
			@Context UriInfo uriInfo) {
		Account account = accountService.createAccount(request.getMobile(), request.getPassword(),
				request.getVerificationCode(), Role.USER /* force user */);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(account.getId()).build();
		LOG.debug("Created account: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Create tenant (registration)
	 */
	@ApiOperation(value = "tenant-register", hidden = true)
	@Path("tenant")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response createTenantAccount(@NotNull @Valid AccountRequest request, @Context UriInfo uriInfo) {
		Account account = accountService.createAccount(request.getUsername(), request.getPassword(),
				Role.TENANT /* force tenant */);
		// FIXME URI
		// URI uri = UriBuilder.fromMethod(getClass(), "findAccount").segment(account.getId()).build();
		URI uri = URI.create("account/" + account.getId());
		LOG.debug("Created tenant account: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Authenticate via internal(username,email,mobile) or external(3rd party) auth
	 */
	@ApiOperation(value = "auth", response = AccessToken.class)
	@ApiResponses(value = { @ApiResponse(code = 401, message = ""), @ApiResponse(code = 404, message = "") })
	@POST
	@Path("auth")
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authenticate(@QueryParam("type") AuthType type, @NotNull @Valid AuthcRequest request) {
		// Authenticate the user using the credentials provided against a database credentials can be account not found
		// or via external auth
		Account account = null;
		if (type != null && !type.isInternal()) {
			account = accountService.authenticateAccount(type, request.getPrincipal(), request.getCredential(),
					request.getExpires());
		}
		else {
			account = accountService.authenticateAccount(request.getPrincipal(), request.getCredential(),
					request.isOtp());
		}
		// should never happen here, just check it for sure
		if (account == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		try {
			// Issue a token for this account
			String username = getUsername(account);
			String token = accessTokenService.generateToken(account.getId(), ImmutableMap.of(Claims.CLAIM_ROLES,
					ImmutableSet.of(account.getRole().name()), Constants.CLAIM_USERNAME, username));
			// Return the token on the response
			return Response.ok(new AccessToken(token)).build();
		}
		catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	/**
	 * Send SMS verification code
	 */
	@ApiOperation(value = "request-verification")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	@POST
	@Path("verification")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public void requestVerification(@NotNull @QueryParam("mobile") String mobile, @Context HttpServletRequest request) {
		String forwardIp = "";
		try {
			// when using Heroku, the remote host is the AWS application tier, therefore the EC2 ip address.
			forwardIp = request.getHeader(Constants.HEADER_X_FORWARDED_FOR).split(",")[0];
		}
		catch (Exception ignored) {
		}
		String ip = request.getRemoteAddr();
		if (Constants.LOOPBACK_ADDRESSES.contains(ip)) {
			ip = forwardIp;
		}
		if (Strings.isBlank(ip)) {
			ip = Constants.LOOPBACK_LOCALHOST;
		}
		LOG.debug("Request verification from IP: {}", ip);
		if (configuration.isMobileVerificationEnabled()) {
			String code = verificationService.generateCode(mobile, ip);
			smsService.sendSms(mobile, code);
		}
	}

	/**
	 * Refresh authc token
	 */
	@ApiOperation(value = "auth-refresh", response = AccessToken.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""), @ApiResponse(code = 401, message = "") })
	@POST
	@Authenticated
	@Path("auth/refresh")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessToken refreshToken(@Context SecurityContext context) {
		SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
		String refreshedToken = accessTokenService.generateToken(principal.getClaims().getSubject(),
				principal.getClaims().getClaimsMap());
		return new AccessToken(refreshedToken);
	}

	/**
	 * Get API Key
	 */
	@ApiOperation(value = "apikey-refresh", response = AccessToken.class, hidden = true)
	@ApiResponses(value = { @ApiResponse(code = 401, message = ""), @ApiResponse(code = 404, message = "") })
	@GET
	@Path("apikey")
	@Authenticated
	@Produces(MediaType.APPLICATION_JSON)
	public AccessToken findApiKey(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.findAccount(accountId);
		return new AccessToken(account.getApiKey());
	}

	/**
	 * Refresh API Key
	 */
	@ApiOperation(value = "apikey-refresh", response = AccessToken.class, hidden = true)
	@POST
	@Authenticated
	@Path("apikey/refresh")
	@Produces(MediaType.APPLICATION_JSON)
	public AccessToken refreshApiKey(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.refreshApiKey(accountId);
		return new AccessToken(account.getApiKey());
	}

	/**
	 * Find account by ID (as a result of account creation), may not really useful, just to complete the RESTful API.
	 * 
	 * TODO REMOVE?
	 */
	@ApiOperation(value = "account-id", hidden = true)
	@GET
	@Path("{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response findAccount(@PathParam("accountId") String accountId, @Context SecurityContext context) {
		String selfAccountId = context.getUserPrincipal().getName();
		// denied for: 1) not self, 2) not admin
		if (!(selfAccountId.equals(accountId) || context.isUserInRole(Role.ADMIN.name()))) {
			return Response.status(Status.FORBIDDEN).build();
		}
		return buildAccountResponse(accountId);
	}

	/**
	 * Get account profile for all (user/tenant/admin)
	 */
	@ApiOperation(value = "profile", response = Account.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""), @ApiResponse(code = 401, message = ""),
			@ApiResponse(code = 404, message = "") })
	@GET
	@Path("profile")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response getSelfAccount(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return buildAccountResponse(accountId);
	}

	private Response buildAccountResponse(String accountId) {
		Account account = accountService.findAccount(accountId);
		List<AccountAuth> auths = accountService.findAccountAuths(accountId);
		try {
			String json = objectMapper.writeValueAsString(account);
			Map<String, Object> data = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
			});
			for (AccountAuth auth : auths) {
				data.putAll(Json.createObjectBuilder()
						.add(auth.getType().toString().toLowerCase(Locale.ENGLISH), auth.getPrincipal()).build());
			}
			Stream.of(AuthType.values())
					.forEach(type -> data.putIfAbsent(type.name().toLowerCase(Locale.ENGLISH), null));
			return Response.ok(data).build();
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to get account: %s, cause: %s", accountId, e.getMessage()), e);
			throw new AirException(Codes.INTERNAL_ERROR, M.msg(M.INTERNAL_SERVER_ERROR));
		}
	}

	/**
	 * Get account auths of a profile
	 */
	@ApiOperation(value = "profile-auths", response = AccountAuth.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "") })
	@GET
	@Path("profile/auths")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public List<AccountAuth> getSelfAccountAuths(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return accountService.findAccountAuths(accountId);
	}

	/**
	 * Update account (User/Tenant/Admin)
	 */
	@ApiOperation(value = "profile-update", response = Account.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "") })
	@PUT
	@Path("profile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Account updateAccount(@NotNull @Valid JsonObject newData, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.findAccount(accountId);
		// convert to json string
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.writeObject(newData);
		}
		String json = stringWriter.toString();
		try {
			Account newAccount = objectMapper.readValue(json, account.getClass());
			Account accountUpdated = accountService.updateAccount(accountId, newAccount);
			return accountUpdated;
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to update account: %, cause: %s", json, e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	/**
	 * Change password if old password is correct.
	 */
	@ApiOperation(value = "password-update")
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 401, message = ""),
			@ApiResponse(code = 404, message = "") })
	@POST
	@Path("password")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	public void updatePassword(@NotNull @Valid PasswordRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updatePassword(accountId, request.getOldPassword(), request.getNewPassword());
	}

	/**
	 * Reset/forget password via mobile
	 */
	@ApiOperation(value = "password-reset-mobile")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	@POST
	@Path("password/reset")
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPassword(@NotNull @Valid PasswordResetRequest request) {
		if (!configuration.isMobileVerificationEnabled()) {
			LOG.warn(
					"Mobiel verification is not enabled, cannot reset password via mobile, please use email to reset.");
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}
		if (!verificationService.verifyCode(request.getMobile(), request.getVerificationCode())) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					M.msg(M.ACCOUNT_CREATION_INVALID_VERIFICATION_CODE, request.getVerificationCode()));
		}
		accountService.resetPasswordViaMobile(request.getMobile(), request.getNewPassword());
		return Response.noContent().build();
	}

	/**
	 * Reset/forget password via email
	 */
	@ApiOperation(value = "password-reset-email")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	@POST
	@Authenticated
	@Path("password/reset/email")
	public void resetPassword(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.resetPasswordViaEmail(accountId);
	}

	/**
	 * Change username
	 */
	@ApiOperation(value = "username-update")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	@POST
	@Path("username")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateUsername(@NotNull @Valid UsernameRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updateUsername(accountId, request.getUsername());
	}

	/**
	 * Change email and need to be verified
	 */
	@ApiOperation(value = "email-update")
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 400, message = ""),
			@ApiResponse(code = 401, message = ""), @ApiResponse(code = 404, message = "") })
	@POST
	@Path("email")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEmail(@NotNull @Valid EmailRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updateEmail(accountId, request.getEmail());
	}

	/**
	 * Confirm email, account/email/confirm?token=xxx&code=xxx
	 */
	@ApiOperation(value = "email-confirm")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "") })
	@GET
	@TokenSecured
	@Path("email/confirm")
	@Produces(MediaType.TEXT_HTML)
	public Response confirmEmail(@QueryParam("code") String verificationCode, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Map<String, Object> bindings = new HashMap<>(3);
		bindings.put(Constants.TEMPLATE_BINDING_COMPANY, configuration.getCompany());
		String result = "";
		try {
			accountService.confirmEmail(accountId, verificationCode);
			AccountAuth auth = accountService.findAccountEmail(accountId);
			bindings.put(Constants.TEMPLATE_BINDING_USERNAME, auth.getAccount().getNickName());
			bindings.put(Constants.TEMPLATE_BINDING_EMAIL, auth.getPrincipal());
			result = templateService.renderFile(Constants.TEMPLATE_MAIL_VERIFICATION_SUCCESS, bindings);
		}
		catch (AirException e) {
			bindings.put(Constants.TEMPLATE_BINDING_FAILURE_CAUSE, e.getLocalizedMessage());
			result = templateService.renderFile(Constants.TEMPLATE_MAIL_VERIFICATION_FAILURE, bindings);
		}
		return Response.ok(result).build();
	}

	/**
	 * Delete self account
	 */
	@ApiOperation(value = "delete", hidden = true)
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 401, message = "") })
	@DELETE
	@Authenticated
	public void deleteSelfAccount(@NotNull @Valid AuthcRequest request) {
		Account account = accountService.authenticateAccount(request.getPrincipal(), request.getCredential(),
				request.isOtp());
		accountService.deleteAccount(account.getId());
	}

}
