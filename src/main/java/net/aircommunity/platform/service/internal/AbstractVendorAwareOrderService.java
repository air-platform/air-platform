package net.aircommunity.platform.service.internal;

import com.codahale.metrics.Timer;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.VendorAwareOrder;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;

/**
 * Abstract VendorAwareOrder service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractVendorAwareOrderService<T extends VendorAwareOrder> extends AbstractOrderService<T> {

	protected abstract VendorAwareOrderRepository<T> getOrderRepository();

	/**
	 * For TENANT (Exclude orders in DELETED status)
	 */
	protected Page<T> doListTenantOrders(String tenantId, Order.Status status, int page, int pageSize) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_TENANT_LIST);
			timerContext = timer.time();
		}
		try {
			if (Order.Status.DELETED == status) {
				return Page.emptyPage(page, pageSize);
			}
			if (status == null) {
				return Pages.adapt(getOrderRepository().findByVendorIdAndStatusInOrderByCreationDateDesc(tenantId,
						Order.Status.visibleStatus(), Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(getOrderRepository().findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
					Pages.createPageRequest(page, pageSize)));
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * For ADMIN (orders in any status)
	 */
	protected Page<T> doListAllTenantOrders(String tenantId, Order.Status status, int page, int pageSize) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_ADMIN_LIST_TENANT);
			timerContext = timer.time();
		}
		try {
			if (status == null) {
				return Pages.adapt(getOrderRepository().findByVendorIdOrderByCreationDateDesc(tenantId,
						Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(getOrderRepository().findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
					Pages.createPageRequest(page, pageSize)));
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}
}
