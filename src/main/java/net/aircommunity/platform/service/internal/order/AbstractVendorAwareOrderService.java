package net.aircommunity.platform.service.internal.order;

import java.util.function.Function;

import com.codahale.metrics.Timer;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.VendorAwareOrder;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.internal.Pages;

/**
 * Abstract VendorAware order service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractVendorAwareOrderService<T extends VendorAwareOrder> extends AbstractStandardOrderService<T> {

	protected VendorAwareOrderRepository<T> orderRepository;

	public AbstractVendorAwareOrderService(VendorAwareOrderRepository<T> orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	protected void doInitialize() {
		setOrderProcessor(new StandardOrderProcessor<T>(configuration, orderRepository));
	}

	protected VendorAwareOrderRepository<T> getOrderRepository() {
		return orderRepository;
	}

	@Override
	public Function<String, T> getOrderFinder() {
		return orderId -> orderRepository.findOne(orderId);
	}

	@Override
	public Function<T, T> getOrderPersister() {
		return t -> orderRepository.save(t);
	}

	@Override
	public Page<T> listTenantOrders(String tenantId, Order.Status status, int page, int pageSize) {
		return doListTenantOrders(tenantId, status, page, pageSize);
	}

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
				return Pages.adapt(orderRepository.findByVendorIdAndStatusInOrderByCreationDateDesc(tenantId,
						Order.Status.VISIBLE_STATUSES, Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(orderRepository.findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
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
				return Pages.adapt(orderRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
						Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(orderRepository.findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
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
