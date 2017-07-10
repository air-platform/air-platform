package net.aircommunity.platform.rest;

import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.micro.annotation.TokenSecured;
import io.micro.annotation.multipart.MultipartForm;
import io.micro.common.Strings;
import io.micro.common.UUIDs;
import io.micro.common.io.MoreFiles;
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
import net.aircommunity.platform.model.AuthContext;
import net.aircommunity.platform.model.AuthcRequest;
import net.aircommunity.platform.model.AvatarImage;
import net.aircommunity.platform.model.DailySigninResponse;
import net.aircommunity.platform.model.EmailRequest;
import net.aircommunity.platform.model.ImageCropResult;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.PasswordRequest;
import net.aircommunity.platform.model.PasswordResetRequest;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.TenantAccountRequest;
import net.aircommunity.platform.model.UserAccountRequest;
import net.aircommunity.platform.model.UsernameRequest;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.AccountAuth;
import net.aircommunity.platform.model.domain.AccountAuth.AuthType;
import net.aircommunity.platform.model.domain.DailySignin;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.common.FileService;
import net.aircommunity.platform.service.common.SmsService;
import net.aircommunity.platform.service.common.TemplateService;
import net.aircommunity.platform.service.common.VerificationService;
import net.aircommunity.platform.service.security.AccountService;

/**
 * Account RESTful API.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("account")
public class AccountResource {
	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private AccountService accountService;

	@Resource
	private FileService fileService;

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

	/**
	 * User registration
	 */
	@POST
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "register")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "") })
	public Response createUserAccount(@ApiParam(required = true) @NotNull @Valid UserAccountRequest request,
			@Context UriInfo uriInfo) {
		Account account = accountService.createAccount(request.getMobile(), request.getPassword(),
				request.getVerificationCode(), Role.USER /* force user */);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(account.getId()).build();
		LOG.debug("Created account: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Tenant registration
	 */
	@POST
	@PermitAll
	@Path("tenant")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "tenant-register", hidden = true)
	public Response createTenantAccount(@NotNull @Valid TenantAccountRequest request, @Context UriInfo uriInfo) {
		Account account = accountService.createAccount(request.getUsername(), request.getPassword(),
				Role.TENANT /* force tenant */);
		// NOTE: need to convert URI:
		// FROM URI: http://localhost:8080/api/v2/account/tenant/7f000001-5d1d-1abc-815d-1dcd75560000
		// TO URI: http://localhost:8080/api/v2/account/7f000001-5d1d-1abc-815d-1dcd75560000
		URI uriRoot = uriInfo.getAbsolutePathBuilder().build();
		String baseUri = uriRoot.toString();
		URI uri = UriBuilder.fromUri(URI.create(baseUri.substring(0, baseUri.lastIndexOf("/"))))
				.segment(account.getId()).build();
		LOG.debug("Created tenant account: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Authenticate ADMIN/TENANT/CUSTOMER_SERVICE
	 */
	@POST
	@PermitAll
	@Path("auth/mgt")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authManagement(@HeaderParam(value = "x-forwarded-for") String ip,
			@HeaderParam("user-agent") String userAgent, @NotNull @Valid AuthcRequest request) {
		return doAuthenticate(ip, userAgent, AuthType.USERNAME, request,
				EnumSet.of(Role.ADMIN, Role.TENANT, Role.CUSTOMER_SERVICE));
	}

	/**
	 * Authenticate via internal(username,email,mobile) or external(3rd party) auth
	 */
	@POST
	@PermitAll
	@Path("auth")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "auth", response = AccessToken.class)
	@ApiResponses(value = { @ApiResponse(code = 401, message = ""), @ApiResponse(code = 404, message = "") })
	public Response authenticate(@HeaderParam(value = "x-forwarded-for") String ip,
			@HeaderParam("user-agent") String userAgent, @QueryParam("type") AuthType type,
			@NotNull @Valid AuthcRequest request) {
		return doAuthenticate(ip, userAgent, type, request, Collections.emptySet());// allow all roles
	}

	private Response doAuthenticate(String ip, String userAgent, AuthType type, AuthcRequest request,
			Set<Role> allowedRoles) {
		LOG.debug("Auth attempt from [{}][{}][{}] account: {}", ip, type, Joiner.on(",").join(allowedRoles),
				request.getPrincipal());
		// Authenticate the user using the credentials provided against a database credentials can be account not found
		// or via external auth
		Account account = null;
		AuthContext ctx = new AuthContext().ipAddress(ip).otp(request.isOtp()).source(userAgent)
				.expiry(request.getExpires());
		if (type != null && !type.isInternal()) {
			account = accountService.authenticateAccount(type, request.getPrincipal(), request.getCredential(), ctx);
		}
		else {
			account = accountService.authenticateAccount(request.getPrincipal(), request.getCredential(), ctx);
		}
		// 1) account = null: should never happen here, just check it for sure
		// 2) ensure allowed roles (allowedRoles is empty means role is not checked)
		if (account == null || (!allowedRoles.isEmpty() && !allowedRoles.contains(account.getRole()))) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		try {
			// Issue a token for this account
			String username = getUsername(account);
			Map<String, Object> claims = new HashMap<>();
			claims.put(Claims.CLAIM_ROLES, ImmutableSet.of(account.getRole().name()));
			claims.put(Claims.CLAIM_EXPIRY, configuration.getTokenExpirationTimeSeconds());
			claims.put(Constants.CLAIM_ID, account.getId());
			claims.put(Constants.CLAIM_USERNAME, username);
			claims.put(Constants.CLAIM_NICKNAME, account.getNickName());
			String token = accessTokenService.generateToken(account.getId(), claims);
			// Return the token on the response
			return Response.ok(new AccessToken(token)).build();
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to auth %s, cause: %", request, e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	private String getUsername(Account account) {
		AccountAuth accountAuthUsername = accountService.findAccountUsername(account.getId());
		if (accountAuthUsername != null) {
			return accountAuthUsername.getPrincipal();
		}
		AccountAuth accountAuthMobile = accountService.findAccountMobile(account.getId());
		return accountAuthMobile.getPrincipal();
	}

	/**
	 * Daily signin
	 */
	@POST
	@Authenticated
	@Path("daily-signin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response dailySignin(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.findAccount(accountId);
		if (!User.class.isAssignableFrom(account.getClass())) {
			LOG.warn("Signin is forbidden for none user account: {}", account);
			return Response.status(Status.FORBIDDEN).build();
		}
		account = accountService.dailySignin(accountId);
		DailySignin dailySignin = User.class.cast(account).getDailySignin();
		LOG.debug("{} dailySignin: {}", account, dailySignin);
		return Response.ok(new DailySigninResponse(dailySignin)).build();
	}

	/**
	 * Avatar upload to cloud, e.g. ?cropOptions=300x300a50a50 ( {cropsize}a<dx>a<dy> )
	 */
	@POST
	@Path("avatar")
	@Authenticated
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ImageCropResult uploadAvatarToCloud(@QueryParam("cropOptions") String cropOptions,
			@MultipartForm AvatarImage avatarImage) {
		String fileName = avatarImage.getFileName();
		LOG.debug("Uploading avatar {} to cloud", fileName);
		if (Strings.isBlank(fileName)) {
			throw new AirException(Codes.INVALID_FORM_INPUT, M.msg(M.FORM_INVALID_FILE_INPUT));
		}
		try {
			String extension = MoreFiles.getExtension(fileName);
			return fileService.cropImage(
					String.format(Constants.FILE_UPLOAD_NAME_FORMAT, UUIDs.shortRandom(), extension),
					avatarImage.getFileData(), cropOptions);
		}
		finally {
			if (avatarImage != null) {
				avatarImage.close();
			}
		}
	}

	/**
	 * Send SMS verification code
	 */
	@POST
	@PermitAll
	@Path("verification")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "request-verification")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	public void requestVerification(@HeaderParam(value = "x-forwarded-for") String ip,
			@NotNull @QueryParam("mobile") String mobile, @Context HttpServletRequest request) {
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
	@POST
	@Authenticated
	@Path("auth/refresh")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "auth-refresh", response = AccessToken.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""), @ApiResponse(code = 401, message = "") })
	public AccessToken refreshToken(@Context SecurityContext context) {
		SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
		String refreshedToken = accessTokenService.generateToken(principal.getClaims().getSubject(),
				principal.getClaims().getClaimsMap());
		return new AccessToken(refreshedToken);
	}

	/**
	 * Get API Key (ADMIN, CS, TENANT)
	 */
	@GET
	@Path("apikey")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "apikey-refresh", response = AccessToken.class, hidden = true)
	@ApiResponses(value = { @ApiResponse(code = 401, message = ""), @ApiResponse(code = 404, message = "") })
	public AccessToken findApiKey(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.findAccount(accountId);
		return new AccessToken(account.getApiKey());
	}

	/**
	 * Refresh API Key (ADMIN, CS, TENANT)
	 */
	@POST
	@Path("apikey/refresh")
	@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_CUSTOMER_SERVICE, Roles.ROLE_TENANT })
	@ApiOperation(value = "apikey-refresh", response = AccessToken.class, hidden = true)
	@Produces(MediaType.APPLICATION_JSON)
	public AccessToken refreshApiKey(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.refreshApiKey(accountId);
		return new AccessToken(account.getApiKey());
	}

	/**
	 * Find account by ID (as a result of account creation), may not really useful, just to complete the RESTful API.
	 */
	@GET
	@Authenticated
	@Path("{accountId}")
	@JsonView(JsonViews.User.class)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "account-id", hidden = true)
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
	@GET
	@Authenticated
	@Path("profile")
	@JsonView(JsonViews.User.class)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "profile", response = Account.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""), @ApiResponse(code = 401, message = ""),
			@ApiResponse(code = 404, message = "") })
	public Response getSelfAccount(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return buildAccountResponse(accountId);
	}

	private Response buildAccountResponse(String accountId) {
		Account account = accountService.findAccount(accountId);
		List<AccountAuth> auths = accountService.findAccountAuths(accountId);
		try {
			String json = objectMapper.writerWithView(JsonViews.User.class).writeValueAsString(account);
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
	@GET
	@Authenticated
	@Path("profile/auths")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	@ApiOperation(value = "profile-auths", response = AccountAuth.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "") })
	public List<AccountAuth> getSelfAccountAuths(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		return accountService.findAccountAuths(accountId);
	}

	/**
	 * Update account (User/Tenant/Admin)
	 */

	@PUT
	@Authenticated
	@Path("profile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "profile-update", response = Account.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "") })
	@JsonView(JsonViews.User.class)
	public Account updateAccount(@NotNull @Valid JsonObject accountData, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.findAccount(accountId);
		// convert to json string
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.writeObject(accountData);
		}
		String json = stringWriter.toString();
		try {
			Account newAccount = objectMapper.readValue(json, account.getClass());
			return accountService.updateAccount(accountId, newAccount);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to update account: %, cause: %s", json, e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	/**
	 * Change password if old password is correct.
	 */
	@POST
	@Authenticated
	@Path("password")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "password-update")
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 401, message = ""),
			@ApiResponse(code = 404, message = "") })
	public void updatePassword(@NotNull @Valid PasswordRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updatePassword(accountId, request.getOldPassword(), request.getNewPassword());
	}

	/**
	 * Reset/forget password via mobile
	 */
	@POST
	@PermitAll
	@Path("password/reset")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "password-reset-mobile")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	public Response resetPassword(@NotNull @Valid PasswordResetRequest request) {
		if (!configuration.isMobileVerificationEnabled()) {
			LOG.warn(
					"Mobiel verification is not enabled, cannot reset password via mobile, please use email to reset.");
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}
		if (!verificationService.verifyCode(request.getMobile(), request.getVerificationCode())) {
			throw new AirException(Codes.ACCOUNT_INVALID_VERIFICATION_CODE,
					M.msg(M.ACCOUNT_RESET_PASSWORD_INVALID_VERIFICATION_CODE, request.getVerificationCode()));
		}
		accountService.resetPasswordViaMobile(request.getMobile(), request.getNewPassword());
		return Response.noContent().build();
	}

	/**
	 * Reset/forget password via email
	 */
	@POST
	@Authenticated
	@Path("password/reset/email")
	@ApiOperation(value = "password-reset-email")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	public void resetPassword(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.resetPasswordViaEmail(accountId);
	}

	/**
	 * Change username
	 */

	@POST
	@Authenticated
	@Path("username")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "username-update")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "") })
	public void updateUsername(@NotNull @Valid UsernameRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updateUsername(accountId, request.getUsername());
	}

	/**
	 * Change email and need to be verified
	 */
	@POST
	@Authenticated
	@Path("email")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "email-update")
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 400, message = ""),
			@ApiResponse(code = 401, message = ""), @ApiResponse(code = 404, message = "") })
	public void updateEmail(@NotNull @Valid EmailRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updateEmail(accountId, request.getEmail());
	}

	/**
	 * Confirm email, account/email/confirm?token=xxx&code=xxx
	 */
	@GET
	@TokenSecured
	@Path("email/confirm")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value = "email-confirm")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "") })
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
	@DELETE
	@Authenticated
	@ApiOperation(value = "delete", hidden = true)
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 401, message = "") })
	public void deleteSelfAccount(@HeaderParam(value = "x-forwarded-for") String ip,
			@HeaderParam("user-agent") String userAgent, @NotNull @Valid AuthcRequest request) {
		Account account = accountService.authenticateAccount(request.getPrincipal(), request.getCredential(),
				new AuthContext().ipAddress(ip).otp(request.isOtp()).source(userAgent).expiry(request.getExpires()));
		accountService.deleteAccount(account.getId());
	}

	/**
	 * @deprecated
	 */
	// @ApiOperation(value = "login", hidden = true)
	// @GET
	// @Path("login")
	// @PermitAll
	// @Produces(MediaType.TEXT_HTML)
	// public Response login(@Context SecurityContext context) {
	// SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
	// LOG.debug("principal: {}", principal);
	// String accountId = null;
	// if (principal == null) {
	// return Response.ok("access-denied").build();
	// }
	// else {
	// accountId = principal.getName();
	// }
	// Account account = accountService.findAccount(accountId);
	// LOG.debug("account: {}", account);
	// String username = getUsername(account);
	// LOG.debug("username: {}", username);
	// String token = accessTokenService.generateToken(account.getId(),
	// ImmutableMap.of(Claims.CLAIM_ROLES, ImmutableSet.of(account.getRole().name()), Claims.CLAIM_EXPIRY,
	// configuration.getTokenExpirationTimeSeconds(), Constants.CLAIM_USERNAME, username));
	// String domain = configuration.getPublicHost();
	//
	// LOG.debug("domain: {}", domain);
	// domain = "aircommunity.net";
	// int maxAge = (int) configuration.getTokenExpirationTimeSeconds();
	// NewCookie cookie = new NewCookie("token", token, "/"/* path */, domain, NewCookie.DEFAULT_VERSION,
	// null/* comment */, maxAge, null/* expiry */, false/* secure */, true/* httpOnly */);
	// return Response.ok("ok").cookie(cookie).build();
	// }

	/**
	 * AirQ
	 * @deprecated
	 */
	// @ApiOperation(value = "airq", response = AccessToken.class)
	// @ApiResponses(value = { @ApiResponse(code = 200, message = ""), @ApiResponse(code = 403, message = "") })
	// @GET
	// @Path("airq")
	// @PermitAll
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response airq(@Context SecurityContext context) {
	// SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
	// LOG.debug("principal: {}", principal);
	// String accountId = null;
	// if (principal == null) {
	// return Response.status(Status.FORBIDDEN).build();
	// }
	// else {
	// accountId = principal.getName();
	// }
	// Account account = accountService.findAccount(accountId);
	// LOG.debug("account: {}", account);
	// String username = getUsername(account);
	// Map<String, Object> claims = ImmutableMap.<String, Object> builder()
	// .put(Claims.CLAIM_ROLES, ImmutableSet.of(account.getRole().name()))
	// .put(Claims.CLAIM_EXPIRY, configuration.getTokenExpirationTimeSeconds())
	// .put(Constants.CLAIM_ID, account.getId()).put(Constants.CLAIM_USERNAME, username)
	// .put(Constants.CLAIM_NICKNAME, account.getNickName()).build();
	// String token = accessTokenService.generateToken(account.getId(), claims);
	// return Response.ok(new AccessToken(token)).build();
	// }

}
