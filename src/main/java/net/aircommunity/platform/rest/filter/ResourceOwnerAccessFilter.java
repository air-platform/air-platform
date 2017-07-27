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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.security.PrivilegedResource;
import net.aircommunity.platform.service.security.AccessControlService;
import net.aircommunity.platform.service.security.AccountService;

/**
 * Access check for product, order and other related resources to ensure the ownership of the resource.
 * 
 * @author Bin.Zhang
 */
@Provider
@AllowResourceOwner
@Priority(Priorities.USER + 1)
public class ResourceOwnerAccessFilter implements ContainerRequestFilter {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceOwnerAccessFilter.class);
	private static final String TENANT_ID_PATH_PARAM = "tenantId";
	private static final String USER_ID_PATH_PARAM = "userId";
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

	@Context
	private UriInfo info;

	@Resource
	private AccessControlService accessControlService;

	@Resource
	private AccountService accountService;

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		SecurityContext securityContext = context.getSecurityContext();
		// ADMIN
		// allowed to access any resources if ADMIN
		if (securityContext.isUserInRole(Role.ADMIN.name())) {
			// skip check for ADMIN, but still need to set path if the the USER role=ADMIN
			String accountId = securityContext.getUserPrincipal().getName();
			Account account = accountService.findAccount(accountId);
			if (User.class.isAssignableFrom(account.getClass())) {
				info.getPathParameters().add(USER_ID_PATH_PARAM, accountId);
			}
			return;
		}

		// allow resource owner
		List<PathSegment> segs = info.getPathSegments();
		String path = segs.stream().map(seg -> seg.getPath()).collect(Collectors.joining("/", "/", ""));
		MultivaluedMap<String, String> pathParams = info.getPathParameters();
		LOG.debug("Path segments: {}, normalized path: {}, pathParams: {}", segs, path, pathParams);

		// TENANT
		// for tenant resource without tenantId passed from URI, but append it automatically
		if (securityContext.isUserInRole(Role.TENANT.name())) {
			Role role = Role.TENANT;
			String tenantId = securityContext.getUserPrincipal().getName();
			pathParams.add(TENANT_ID_PATH_PARAM, tenantId);
			// check tenant resource permission
			// a) check product
			Matcher m = TENANT_PRODUCT_PATTERN.matcher(path);
			if (m.find()) {
				Product.Type productType = Product.Type.fromString(m.group(1));
				PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.PRODUCT,
						m.group(2)/* productId */, productType);
				accessControlService.checkResourceAccess(tenantId, role, resource);
				return;
			}
			// b) check order
			m = TENANT_ORDER_PATTERN.matcher(path);
			if (m.find()) {
				PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.ORDER,
						m.group(2)/* orderId */);
				accessControlService.checkResourceAccess(tenantId, role, resource);
				return;
			}
			// c) check product related resource
			m = TENANT_PRODUCT_RESOURCE_PATTERN.matcher(path);
			if (m.find()) {
				PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.fromString(m.group(1)),
						m.group(2)/* resourceId */);
				accessControlService.checkResourceAccess(tenantId, role, resource);
				return;
			}
			// d) check product family resource
			m = TENANT_PRODUCT_FAMILY_PATTERN.matcher(path);
			if (m.find()) {
				PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.PRODUCT_FAMILY,
						m.group(1)/* resourceId */);
				accessControlService.checkResourceAccess(tenantId, role, resource);
				return;
			}
		}

		// USER
		if (securityContext.isUserInRole(Role.USER.name())) {
			String userId = securityContext.getUserPrincipal().getName();
			pathParams.add(USER_ID_PATH_PARAM, userId);
			// check user resource permission
			Matcher m = USER_ORDER_PATTERN.matcher(path);
			if (m.find()) {
				PrivilegedResource resource = PrivilegedResource.of(PrivilegedResource.Type.ORDER,
						m.group(3)/* orderId */);
				accessControlService.checkResourceAccess(userId, Role.USER, resource);
			}
		}
	}
}
