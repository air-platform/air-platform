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
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.order.OrderServiced;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * Common order service implementation.
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
	// orderRepository.findAllByOrderByCreationDateDesc() NOT working
	private void rebuildOrderRef() {
		// always try-resource when streaming data from DB:
		orderRefRepository.deleteAllInBatch();
		try (Stream<Order> stream = orderRepository
				.findAllByOrderByCreationDateDesc(Pages.createPageRequest(1, Integer.MAX_VALUE)).getContent()
				.stream()) {
			stream.forEach(order -> {
				OrderRef orderRef = new OrderRef();
				orderRef.setOrderId(order.getId());
				orderRef.setOrderNo(order.getOrderNo());
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
	public Order payOrder(Order order, Payment payment) {
		return doPayOrder(order, payment);
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
			OrderServiced serviced = service.getClass().getAnnotation(OrderServiced.class);
			if (serviced != null) {
				builder.put(serviced.value(), service);
			}
		});
		serviceRegistry = builder.build();
		LOG.debug("Order service registry: {}", serviceRegistry);
	}

}
