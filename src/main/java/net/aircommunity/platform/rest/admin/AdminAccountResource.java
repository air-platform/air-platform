package net.aircommunity.platform.rest.admin;

import java.net.URI;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.AccountRequest;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.Tenant.VerificationStatus;
import net.aircommunity.platform.service.AccountService;

/**
 * Account RESTful API for ADMIN.
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminAccountResource {
	private static final Logger LOG = LoggerFactory.getLogger(AdminAccountResource.class);

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
	public Page<Account> listAllAccounts(@QueryParam("role") Role role, @QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return accountService.listAccounts(role, page, pageSize);
	}

	/**
	 * Gets a account
	 */
	@GET
	@Path("{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account findAccount(@PathParam("accountId") String accountId) {
		return accountService.findAccount(accountId);
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
	 * Enable a account
	 */
	@POST
	@Path("{accountId}/enable")
	public void enableAccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.ENABLED);
	}

	/**
	 * Disable a account
	 */
	@POST
	@Path("{accountId}/disable")
	public void disableccount(@PathParam("accountId") String accountId) {
		accountService.updateAccountStatus(accountId, Account.Status.DISABLED);
	}

	/**
	 * Lock a account
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
	 * Verify a tenant account
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
	public void unverifyTenantAccount(@PathParam("accountId") String accountId) {
		accountService.updateTenantVerificationStatus(accountId, VerificationStatus.UNVERIFIED);
	}

	/**
	 * Delete a account
	 */
	@Path("{accountId}")
	@DELETE
	public void deleteAccount(@PathParam("accountId") String accountId) {
		accountService.deleteAccount(accountId);
	}

}
