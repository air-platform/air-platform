package net.aircommunity.platform.service.internal.security;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.VendorAware;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.security.PrivilegedResource;
import net.aircommunity.platform.security.PrivilegedResource.Type;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.product.AircraftService;
import net.aircommunity.platform.service.product.CommonProductService;
import net.aircommunity.platform.service.product.ProductFamilyService;
import net.aircommunity.platform.service.product.SchoolService;
import net.aircommunity.platform.service.security.AccessControlService;

/**
 * Access control service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class AccessControlServiceImpl implements AccessControlService {

	@Resource(name = "commonOrderService")
	private CommonOrderService commonOrderService;

	@Resource
	private CommonProductService commonProductService;

	@Resource
	private AircraftService aircraftService;

	@Resource
	private SchoolService schoolService;

	@Resource
	private ProductFamilyService productFamilyService;

	private final Map<Type, BiPredicate<String, PrivilegedResource>> vendorAwareChecker = new HashMap<>();

	@PostConstruct
	private void init() {
		vendorAwareChecker.put(Type.PRODUCT, (accountId, resource) -> {
			Product.Type productType = (Product.Type) resource.getContext();
			VendorAware product = commonProductService.findProduct(productType, resource.getResourceId());
			return product.isOwner(accountId);
		});
		vendorAwareChecker.put(Type.AIRCRAFT, (accountId, resource) -> {
			VendorAware aircraft = aircraftService.findAircraft(resource.getResourceId());
			return aircraft.isOwner(accountId);
		});
		vendorAwareChecker.put(Type.SCHOOL, (accountId, resource) -> {
			VendorAware school = schoolService.findSchool(resource.getResourceId());
			return school.isOwner(accountId);
		});
		vendorAwareChecker.put(Type.PRODUCT_FAMILY, (accountId, resource) -> {
			VendorAware family = productFamilyService.findProductFamily(resource.getResourceId());
			return family.isOwner(accountId);
		});
	}

	@Override
	public void checkResourceAccess(String accountId, Role role, PrivilegedResource resource) {
		boolean allowed = false;
		String resourceId = resource.getResourceId();
		Type type = resource.getResourceType();
		switch (type) {
		case ORDER:
			Order order = commonOrderService.findOrder(resourceId);
			if (Role.USER == role) {
				allowed = order.isOwner(accountId);
			}
			// TODO NOTE: also allow Role.CUSTOMER_SERVICE == role, once it's implemented
			if (Role.TENANT == role) {
				allowed = order.isForTenant(accountId);
			}
			if (!allowed) {
				throw new AirException(Codes.ORDER_ACCESS_DENIED, M.msg(M.ORDER_ACCESS_DENIED));
			}

		default:
			BiPredicate<String, PrivilegedResource> checker = vendorAwareChecker.get(type);
			if (checker != null) {
				allowed = checker.test(accountId, resource);
			}
			if (!allowed) {
				throw new AirException(Codes.ORDER_ACCESS_DENIED, M.msg(M.PRODUCT_ACCESS_DENIED));
			}
		}
	}

}
