package net.aircommunity.platform.service.internal.security;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.security.PrivilegedResource;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.security.AccessControlService;

/**
 * Access control service implementation.
 */
@Service
public class AccessControlServiceImpl implements AccessControlService {

	@Resource
	private CommonOrderService commonOrderService;

	@Override
	public void checkResourceAccess(String accountId, PrivilegedResource resource) {
		boolean allowed = true;
		switch (resource.getResourceType()) {
		case ORDER:
			Order order = commonOrderService.findOrder(resource.getResourceId());
			allowed = order.isOwner(accountId);
			break;

		case PRODUCT:
			break;

		default:
		}

		if (!allowed) {
			throw new AirException(Codes.ORDER_ACCESS_DENIED, M.msg(M.ORDER_ACCESS_DENIED));
		}
	}

}
