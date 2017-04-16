package net.aircommunity.platform.service.internal;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.VendorAwareOrder;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;

/**
 * Abstract VendorAwareOrder service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractVendorAwareOrderService<T extends VendorAwareOrder> extends AbstractOrderService<T> {
	// private static final Logger LOG = LoggerFactory.getLogger(AbstractVendorAwareOrderService.class);

	protected abstract VendorAwareOrderRepository<T> getOrderRepository();

	/**
	 * For TENANT (Exclude orders in DELETED status)
	 */
	protected Page<T> listTenantOrders(String tenantId, Order.Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(getOrderRepository().findByVendorIdAndStatusNotOrderByCreationDateDesc(tenantId,
					Order.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN (orders in any status)
	 */
	protected Page<T> listAllTenantOrders(String tenantId, Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(getOrderRepository().findByVendorIdOrderByCreationDateDesc(tenantId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
				Pages.createPageRequest(page, pageSize)));
	}
}
