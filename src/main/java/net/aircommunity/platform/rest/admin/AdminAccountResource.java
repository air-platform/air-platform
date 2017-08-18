package net.aircommunity.platform.rest.admin;

import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.AccountRequest;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.AccountAuth;
import net.aircommunity.platform.model.domain.Tenant.VerificationStatus;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.service.security.AccountService;

/**
 * Account RESTful API for ADMIN.
 * 
 * @author Bin.Zhang
 */
// NOTE: @RolesAllowed is needed and not inherited from parent resource
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminAccountResource extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AdminAccountResource.class);

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private AccountService accountService;

	// **************************************
	// Account administration
	// **************************************

	/**
	 * Create a tenant account
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAccount(@NotNull @Valid AccountRequest request, @Context UriInfo uriInfo) {
		Account created = accountService.createAccount(request.getUsername(), request.getPassword(), request.getRole());
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created account: {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * List All accounts
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllAccounts(@NotNull @QueryParam("type") Account.Type type, @QueryParam("role") Role role,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		switch (type) {
		case USER:
			return Response.ok(accountService.listUserAccounts(role, page, pageSize)).build();

		case TENANT:
			return Response.ok(accountService.listTenantAccounts(role, page, pageSize)).build();

		default:
		}
		// all types
		return Response.ok(accountService.listAccounts(role, page, pageSize)).build();
	}

	/**
	 * Gets a account
	 */
	@GET
	@Path("{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account findAccount(@PathParam("accountId") String accountId) {
		Account account = accountService.findAccount(accountId);
		List<AccountAuth> auths = accountService.findAccountAuths(accountId);
		account.setAuths(auths);
		return account;
	}

	/**
	 * Update account
	 */
	@PUT
	@Path("{accountId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Account updateAccount(@PathParam("accountId") String accountId, @NotNull @Valid JsonObject accountData) {
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
	 * Update role for a account
	 */
	@POST
	@Path("{accountId}/role")
	public void updateUserAccountRole(@PathParam("accountId") String accountId, @NotNull JsonObject role) {
		String newRole = getRole(role);
		accountService.updateUserRole(accountId, Role.fromString(newRole));
	}

	/**
	 * Reset password for a account
	 */
	@POST
	@Path("{accountId}/password/reset")
	public void resetAccountPassword(@PathParam("accountId") String accountId) {
		accountService.resetPasswordViaEmail(accountId);
	}

	/**
	 * Reset password for a account to default password
	 */
	@POST
	@Path("{accountId}/password/reset/default")
	public void resetAccountPasswordTo(@PathParam("accountId") String accountId) {
		accountService.resetPasswordTo(accountId, Constants.DEFAULT_PASSWORD);
	}

	/**
	 * Increase/decrease user points
	 */
	@POST
	@Path("{accountId}/points")
	public void updateUserPoints(@PathParam("accountId") String accountId, @NotNull JsonObject points) {
		long deltaPoints = getPoints(points);
		LOG.debug("Update user {} with point request: {}, deltaPoints: {}", accountId, points, deltaPoints);
		accountService.updateUserPoints(accountId, deltaPoints);
	}

	/**
	 * Enable a account
	 */
	@POST
	@Path("{accountId}/enable")
	public void enableAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.ENABLED);
	}

	/**
	 * Disable a account (normally means this account will not be used anymore)
	 */
	@POST
	@Path("{accountId}/disable")
	public void disableccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.DISABLED);
	}

	/**
	 * Lock a account (normally means it's a temporally state, may unlock it later)
	 */
	@POST
	@Path("{accountId}/lock")
	public void lockAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.LOCKED);
	}

	/**
	 * Unlock a account
	 */
	@POST
	@Path("{accountId}/unlock")
	public void unlockAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.ENABLED);
	}

	/**
	 * Verify a tenant account (tenant may provide certification materials to our platform to prove this identity)
	 */
	@POST
	@Path("{accountId}/verified")
	public void verifyTenantAccount(@PathParam("accountId") String accountId) {
		accountService.updateTenantVerificationStatus(accountId, VerificationStatus.VERIFIED);
	}

	/**
	 * Unverify a tenant account
	 */
	@POST
	@Path("{accountId}/unverified")
	public void unverifyTenantAccount(@PathParam("accountId") String accountId, JsonObject rejectReason) {
		String reason = getReason(rejectReason);
		accountService.updateTenantVerificationStatus(accountId, VerificationStatus.UNVERIFIED, reason);
	}

	/**
	 * Delete a account (NOTE: it remove all the related data of this account, use it with CAUTION )
	 */
	@Path("{accountId}")
	@DELETE
	public void deleteAccount(@PathParam("accountId") String accountId) {
		accountService.deleteAccount(accountId);
	}

}
