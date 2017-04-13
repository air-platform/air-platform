package net.aircommunity.platform.rest.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.AccessControlService;
import net.aircommunity.rest.core.security.SimplePrincipal;

/**
 * Access check for agent sub-resources TODO
 * 
 * @author Bin.Zhang
 */
@AllowResourceOwner
@Provider
@Priority(Priorities.USER + 1)
public class ResourceOwnerAccessFilter implements ContainerRequestFilter {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceOwnerAccessFilter.class);

	private static final Response RESPONSE_UNAUTHORIZED = Response.status(Response.Status.UNAUTHORIZED).build();
	private static final String TENANT_ID_PATH_PARAM = "tenantId";

	@Context
	private UriInfo info;

	@Resource
	private AccessControlService accessControlService;

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		SecurityContext securityContext = context.getSecurityContext();
		// allowed if admin or resource owner
		if (securityContext.isUserInRole(Role.ADMIN.name())) {
			// skip check for admin
			return;
		}

		SimplePrincipal principal = (SimplePrincipal) securityContext.getUserPrincipal();
		MultivaluedMap<String, String> pathParams = info.getPathParameters();
		String tenantId = pathParams.getFirst(TENANT_ID_PATH_PARAM);
		if (!principal.getName().equals(tenantId)) {
			LOG.warn("Cannot access account {} resource by account {}", tenantId, principal.getName());
			context.abortWith(RESPONSE_UNAUTHORIZED);
			return;
		}

		// TODO API KEY
		// String apiKey = (String) principal.getClaims().getClaimsMap().get(Constants.CLAIM_API_KEY);
		// if (apiKey == null) {
		// context.abortWith(RESPONSE_UNAUTHORIZED);
		// LOG.warn("apiKey not found for {}", principal.getName());
		// return;
		// }

		// TODO
		// NOTE: the path pattern should be:
		// 1) agents/<agentId>/[intents|entities]
		// 2) platform/accounts/<account>/agents/<agentId>/[intents|entities] (skipped check)
		// sub-resources
		// PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.ACCOUNT, accountId);
		// String username = principal.getName();
		// LOG.debug("Check user [{}] access to resource {}", username, resource);
		// accessControlService.checkResourceAccess(username, resource);
	}

}
