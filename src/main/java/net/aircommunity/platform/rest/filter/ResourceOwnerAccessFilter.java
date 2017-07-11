package net.aircommunity.platform.rest.filter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.security.PrivilegedResource;
import net.aircommunity.platform.service.security.AccessControlService;

/**
 * Access check for agent sub-resources TODO
 * 
 * @author Bin.Zhang
 */
@AllowResourceOwner
@Provider
@Priority(Priorities.USER + 1)
public class ResourceOwnerAccessFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceOwnerAccessFilter.class);

	private static final Response RESPONSE_UNAUTHORIZED = Response.status(Response.Status.UNAUTHORIZED).build();
	private static final String TENANT_ID_PATH_PARAM = "tenantId";
	private static final String USER_ID_PATH_PARAM = "userId";
	private static final String AIRCRAFT = "aircraft";
	private static final String SCHOOL = "school";
	private static final String RESOURCE_ID_PATTERN = "([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})";
	private static final String PRODUCT_TYPE_PATTERN = Joiner.on("|").join(Product.Type.NAMES);

	// ************
	// USER
	// ************
	// e.g. /user/orders/7f000101-5c8b-1485-815c-8c00301a000a => group3: id
	private static final Pattern USER_ORDER_PATTERN = Pattern
			.compile("^\\/user(\\/(" + PRODUCT_TYPE_PATTERN + "))?\\/orders\\/" + RESOURCE_ID_PATTERN);

	// ************
	// TENANT
	// ************
	// e.g. /tenant/airtaxis/7f000101-5c8b-1485-815c-8c00301a000a => group1: type, group2: id
	private static final Pattern TENANT_PRODUCT_PATTERN = Pattern
			.compile("^\\/tenant\\/(" + PRODUCT_TYPE_PATTERN + ")s\\/" + RESOURCE_ID_PATTERN);

	// e.g. /tenant/airtour/orders/7f000101-5d2b-16f0-815d-2d42c74400d7 => group1: type, group2: id
	private static final Pattern TENANT_ORDER_PATTERN = Pattern
			.compile("^\\/tenant\\/(" + PRODUCT_TYPE_PATTERN + ")\\/orders\\/" + RESOURCE_ID_PATTERN);

	// e.g. /tenant/aircrafts/7f000101-5c81-1046-815c-855d1176000a => group1: type, group2: id
	// e.g. /tenant/schools/7f000101-5c8b-1485-815c-8c0ba768000b
	private static final Pattern TENANT_PRODUCT_RESOURCE_PATTERN = Pattern
			.compile("^\\/tenant\\/(aircraft|school)s\\/" + RESOURCE_ID_PATTERN);

	// e.g. /tenant/product/families/7f000101-5c81-1046-815c-8557ba990009 => group1: id
	private static final Pattern TENANT_PRODUCT_FAMILY_PATTERN = Pattern
			.compile("^\\/tenant\\/product\\/families\\/" + RESOURCE_ID_PATTERN);

	// orderNo and search TODO

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

		List<PathSegment> segs = info.getPathSegments();
		String path = segs.stream().map(seg -> seg.getPath()).collect(Collectors.joining("/", "/", ""));
		LOG.debug("Path segments: {}, normalized path: {}", segs, path); // [user, ferryflight, orders, a, 1]

		MultivaluedMap<String, String> pathParams = info.getPathParameters();
		// for tenant resource without tenantId passed from URI, but append it automatically
		if (securityContext.isUserInRole(Role.TENANT.name())) {
			String tenantId = securityContext.getUserPrincipal().getName();
			pathParams.add(TENANT_ID_PATH_PARAM, tenantId);
			// check tenant resource permission

			// TODO
			// check product
			// Matcher m = TENANT_PRODUCT_PATTERN.matcher(path);
			// if (m.find()) {
			// Product.Type productType = Product.Type.fromString(m.group(1));
			// PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.PRODUCT,
			// m.group(2)/* productId */, productType);
			// accessControlService.checkResourceAccess(tenantId, resource);
			// return;
			// }

			// check order
			// m = TENANT_ORDER_PATTERN.matcher(path);
			// if (m.find()) {
			// Product.Type productType = Product.Type.fromString(m.group(1));
			// PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.ORDER,
			// m.group(2)/* productId */, productType);
			// accessControlService.checkResourceAccess(tenantId, resource);
			// return;
			// }

			// check product related resource
			// m = TENANT_PRODUCT_RESOURCE_PATTERN.matcher(path);
			// if (m.find()) {
			// Product.Type productType = Product.Type.fromString(m.group(1));
			// PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.ORDER,
			// m.group(2)/* productId */, productType);
			// accessControlService.checkResourceAccess(tenantId, resource);
			// return;
			// }
		}

		if (securityContext.isUserInRole(Role.USER.name())) {
			String userId = securityContext.getUserPrincipal().getName();
			pathParams.add(USER_ID_PATH_PARAM, userId);

			// check user resource permission
			Matcher m = USER_ORDER_PATTERN.matcher(path);
			if (m.find()) {
				PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.ORDER,
						m.group(3)/* orderId */);
				accessControlService.checkResourceAccess(userId, resource);
			}
		}

		// SimplePrincipal principal = (SimplePrincipal) securityContext.getUserPrincipal();
		// System.out.println(principal.getClaims());

		// UriInfo uriInfo = context.getUriInfo();
		// // convert baseUri to http://example.com/delegate/rest
		// URI baseUri = uriInfo.getBaseUriBuilder().path(uriInfo.getPathSegments().get(0).getPath() + "/").build();
		// URI requestUri = uriInfo.getRequestUri();
		// // context.setRequestUri(baseUri, requestUri);

		// SimplePrincipal principal = (SimplePrincipal) securityContext.getUserPrincipal();
		// MultivaluedMap<String, String> pathParams = info.getPathParameters();
		// pathParams.add(TENANT_ID_PATH_PARAM, principal.getName());
		// String tenantId = pathParams.getFirst(TENANT_ID_PATH_PARAM);
		// if (!principal.getName().equals(tenantId)) {
		// LOG.warn("Cannot access account {} resource by account {}", tenantId, principal.getName());
		// context.abortWith(RESPONSE_UNAUTHORIZED);
		// return;
		// }

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

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		List<PathSegment> segs = info.getPathSegments();
		LOG.debug("segs {}, res: {}", segs, responseContext.getEntity()); // [user, ferryflight, orders, a, 1]

	}

}
