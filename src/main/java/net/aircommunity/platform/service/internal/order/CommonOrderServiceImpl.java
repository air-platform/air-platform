package net.aircommunity.platform.service.internal.order;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.OrderRef;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.model.metrics.OrderMetrics;
import net.aircommunity.platform.model.metrics.PerOrderMetrics;
import net.aircommunity.platform.model.metrics.UserOrderMetrics;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;

/**
 * Common order service implementation using OrderRef as index to accelerate lookup and then delegate to corresponding
 * concrete order service to do the real job via {@code OrderProcessor}.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class CommonOrderServiceImpl extends AbstractBaseOrderService<Order>
		implements CommonOrderService, ApplicationContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(CommonOrderServiceImpl.class);

	private Map<Product.Type, StandardOrderService<Order>> serviceRegistry = Collections.emptyMap();

	@Override
	protected void doInitialize() {
		setOrderProcessor(new CommonOrderProcessor(configuration, orderRefRepository, serviceRegistry));
		// !!! NOTE:
		// use it with caution when production, it will load all data, ONLY for initialization
		if (configuration.isOrderRebuildRef()) {
			rebuildOrderRef();
			LOG.debug("Finished order rebuild with order refs: {}", orderRefRepository.count());
		}
	}

	// XXX NOTE: @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	// orderRepository.findAllByOrderByCreationDateDesc() streaming is NOT working with lifecycle callback
	// always try-resource when streaming data from DB:
	private void rebuildOrderRef() {
		orderRefRepository.deleteAllInBatch();
		try (Stream<Order> stream = orderRepository
				.findAllByOrderByCreationDateDesc(Pages.createPageRequest(1, Integer.MAX_VALUE)).getContent()
				.stream()) {
			stream.forEach(order -> {
				OrderRef orderRef = new OrderRef();
				orderRef.setOrderId(order.getId());
				orderRef.setOrderNo(order.getOrderNo());
				Product product = order.getProduct();
				orderRef.setVendorId(product == null ? null : product.getVendor().getId());
				orderRef.setOwnerId(order.getOwner().getId());
				orderRef.setStatus(order.getStatus());
				orderRef.setType(order.getType());
				orderRef.setCreationDate(order.getCreationDate());
				orderRefRepository.save(orderRef);
			});
		}
		finally {
			orderRefRepository.flush();
		}
	}

	@Override
	public boolean existsOrderForUser(String userId) {
		return orderRefRepository.existsByOwnerId(userId);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order updateOrderStatus(Order order, Status status) {
		return doUpdateOrderStatus(order, status);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order acceptOrderPayment(Order order, Payment payment) {
		return doAcceptOrderPayment(order, payment);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order acceptOrderRefund(Order order, Refund refund) {
		return doAcceptOrderRefund(order, refund);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order handleOrderPaymentFailure(Order order, String paymentFailureCause) {
		return doHandleOrderPaymentFailure(order, paymentFailureCause);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order handleOrderRefundFailure(Order order, String refundFailureCause) {
		return doHandleOrderRefundFailure(order, refundFailureCause);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		Map<String, StandardOrderService> orderServices = context.getBeansOfType(StandardOrderService.class);
		ImmutableMap.Builder<Product.Type, StandardOrderService<Order>> builder = ImmutableMap.builder();
		orderServices.values().stream().forEach(service -> {
			ManagedOrderService serviced = service.getClass().getAnnotation(ManagedOrderService.class);
			if (serviced != null) {
				builder.put(serviced.value(), service);
			}
		});
		serviceRegistry = builder.build();
		LOG.debug("Order service registry: {}", serviceRegistry);
	}

	@Override
	public OrderMetrics getOrderMetrics() {
		// NOTE: No need to set all for buildAndSum
		// long totalCount = orderRefRepository.count();
		// long countThisMonth = orderRefRepository.countAllThisMonth();
		// long countToday = orderRefRepository.countAllToday();
		// OrderMetrics.Builder builder = OrderMetrics.builder().setTotalCount(totalCount)
		// .setCountThisMonth(countThisMonth).setCountToday(countToday);
		OrderMetrics.Builder builder = OrderMetrics.builder();
		for (Product.Type type : Product.Type.values()) {
			long perTypeTotalCount = orderRefRepository.countByType(type);
			long perTypeCountThisMonth = orderRefRepository.countAllThisMonth(type);
			long perTypeCountToday = orderRefRepository.countAllToday(type);
			builder.setPerOrderMetrics(type,
					new PerOrderMetrics(perTypeTotalCount, perTypeCountThisMonth, perTypeCountToday));
		}
		return builder.buildAndSum();
	}

	@Override
	public OrderMetrics getOrderMetrics(String tenantId) {
		// NOTE: No need to set all for buildAndSum
		// long totalCount = orderRefRepository.countByVendorId(tenantId);
		// long countThisMonth = orderRefRepository.countTenantThisMonth(tenantId);
		// long countToday = orderRefRepository.countTenantToday(tenantId);
		// OrderMetrics.Builder builder = OrderMetrics.builder().setTotalCount(totalCount)
		// .setCountThisMonth(countThisMonth).setCountToday(countToday);

		OrderMetrics.Builder builder = OrderMetrics.builder();
		for (Product.Type type : Product.Type.values()) {
			long perTypeTotalCount = orderRefRepository.countByVendorIdAndType(tenantId, type);
			long perTypeCountThisMonth = orderRefRepository.countTenantThisMonth(tenantId, type);
			long perTypeCountToday = orderRefRepository.countTenantToday(tenantId, type);
			builder.setPerOrderMetrics(type,
					new PerOrderMetrics(perTypeTotalCount, perTypeCountThisMonth, perTypeCountToday));
		}
		return builder.buildAndSum();
	}

	@Override
	public UserOrderMetrics getUserOrderMetrics(String userId) {
		long totalCount = orderRefRepository.countByOwnerId(userId);
		long finishedCount = orderRefRepository.countByOwnerIdAndStatusIn(userId, Order.COMPLETED_STATUSES);
		long pendingCount = orderRefRepository.countByOwnerIdAndStatusIn(userId, Order.PENDING_STATUSES);
		long cancelledCount = orderRefRepository.countByOwnerIdAndStatus(userId, Order.Status.CANCELLED);
		long refundCount = orderRefRepository.countByOwnerIdAndStatusIn(userId, Order.REFUND_STATUSES);
		return new UserOrderMetrics(totalCount, finishedCount, pendingCount, cancelledCount, refundCount);
	}

}
