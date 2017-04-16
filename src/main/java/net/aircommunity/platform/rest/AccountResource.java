package net.aircommunity.platform.rest;

import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
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
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.SmsService;
import net.aircommunity.platform.service.VerificationService;
import net.aircommunity.rest.annotation.Authenticated;
import net.aircommunity.rest.annotation.RESTful;
import net.aircommunity.rest.annotation.TokenSecured;
import net.aircommunity.rest.core.security.AccessTokenService;
import net.aircommunity.rest.core.security.Claims;
import net.aircommunity.rest.core.security.SimplePrincipal;

/**
 * Account RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
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

	/**
	 * Create user (registration)
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response createUserAccount(@NotNull @Valid UserAccountRequest request, @Context UriInfo uriInfo) {
		Account account = accountService.createAccount(request.getMobile(), request.getPassword(),
				request.getVerificationCode(), Role.USER /* force user */);
		URI uri = uriInfo.getAbsolutePathBuilder().segment(account.getId()).build();
		LOG.debug("Created account: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Create tenant (registration)
	 */
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
	@Path("auth")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@PermitAll
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
		if (account == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		try {
			// Issue a token for this account
			String token = accessTokenService.generateToken(account.getId(),
					ImmutableMap.of(Claims.CLAIM_ROLES, ImmutableSet.of(account.getRole().name())));
			// Return the token on the response
			return Response.ok(new AccessToken(token)).build();
		}
		catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return Response.serverError().build();
	}

	/**
	 * Send SMS verification code
	 */
	@POST
	@Path("verification")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response requestVerification(@NotNull @QueryParam("mobile") String mobile) {
		String code = verificationService.generateCode(mobile);
		smsService.sendSms(mobile, code);
		return Response.noContent().build();
	}

	/**
	 * Refresh authc token
	 */
	@POST
	@Path("auth/refresh")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response refreshToken(@Context SecurityContext context) {
		SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
		String refreshedToken = accessTokenService.generateToken(principal.getClaims().getSubject(),
				principal.getClaims().getClaimsMap());
		return Response.ok(new AccessToken(refreshedToken)).build();
	}

	/**
	 * Get API Key
	 */
	@GET
	@Path("apikey")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response findApiKey(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.findAccount(accountId);
		return Response.ok(new AccessToken(account.getApiKey())).build();
	}

	/**
	 * Refresh API Key
	 */
	@POST
	@Path("apikey/refresh")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response refreshApiKey(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.refreshApiKey(accountId);
		return Response.ok(new AccessToken(account.getApiKey())).build();
	}

	/**
	 * Find account by ID (as a result of account creation), may not really useful, just to complete the RESTful API.
	 * 
	 * TODO REMOVE?
	 */
	@GET
	@Path("{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response findAccount(@PathParam("accountId") String accountId, @Context SecurityContext context) {
		String selfAccountId = context.getUserPrincipal().getName();
		// denied for: 1) not self, 2) not admin
		if (!(selfAccountId.equals(accountId) || context.isUserInRole(Role.ADMIN.name()))) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		Account account = accountService.findAccount(accountId);
		return Response.ok(account).build();
	}

	/**
	 * Get account profile for all (user/tenant/admin)
	 */
	@GET
	@Path("profile")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response getSelfAccount(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		Account account = accountService.findAccount(accountId);
		return Response.ok(account).build();
	}

	/**
	 * Get account auths of a profile
	 */
	@GET
	@Path("profile/auths")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response getSelfAccountAuths(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		List<AccountAuth> accountAuths = accountService.findAccountAuths(accountId);
		return Response.ok(accountAuths).build();
	}

	/**
	 * Update account (User/Tenant/Admin)
	 */
	@PUT
	@Path("profile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response updateAccount(@NotNull @Valid JsonObject newData, @Context SecurityContext context) {
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
			return Response.ok(accountUpdated).build();
		}
		catch (Exception e) {
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Failed to update account: %, cause: %s", json, e.getMessage()), e);
		}
	}

	/**
	 * Change password if old password is correct.
	 */
	@POST
	@Path("password")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response updatePassword(@NotNull @Valid PasswordRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updatePassword(accountId, request.getOldPassword(), request.getNewPassword());
		return Response.noContent().build();
	}

	/**
	 * Reset/forget password via mobile
	 */
	@POST
	@Path("password/reset")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response resetPassword(@NotNull @Valid PasswordResetRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		if (verificationService.verifyCode(request.getMobile(), request.getVerificationCode())) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		accountService.resetPasswordTo(accountId, request.getNewPassword());
		return Response.noContent().build();
	}

	/**
	 * Reset/forget password via email
	 */
	@POST
	@Path("password/reset/email")
	@Authenticated
	public Response resetPassword(@Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.resetPassword(accountId);
		return Response.noContent().build();
	}

	/**
	 * Change username
	 */
	@POST
	@Path("username")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response updateUsername(@NotNull @Valid UsernameRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updateUsername(accountId, request.getUsername());
		return Response.noContent().build();
	}

	/**
	 * Change email and need to be verified
	 */
	@POST
	@Path("email")
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response updateEmail(@NotNull @Valid EmailRequest request, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.updateEmail(accountId, request.getEmail());
		return Response.noContent().build();
	}

	/**
	 * Confirm email, account/email/confirm?token=xxx&code=xxx
	 */
	@GET
	@Path("email/confirm")
	@TokenSecured
	public Response confirmEmail(@QueryParam("code") String verificationCode, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		accountService.confirmEmail(accountId, verificationCode);
		return Response.noContent().build();
	}

	/**
	 * Delete self account
	 */
	@DELETE
	@Authenticated
	public Response deleteSelfAccount(@NotNull @Valid AuthcRequest request) {
		Account account = accountService.authenticateAccount(request.getPrincipal(), request.getCredential(),
				request.isOtp());
		if (account == null) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		accountService.deleteAccount(account.getId());
		return Response.noContent().build();
	}

}
