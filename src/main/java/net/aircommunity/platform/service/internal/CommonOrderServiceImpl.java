package net.aircommunity.platform.service.internal;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.RefundRequest;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.PaymentRepository;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Common order service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CommonOrderServiceImpl extends AbstractOrderService<Order> implements CommonOrderService {
	private static final String CACHE_NAME = "cache.order";

	@Resource
	private BaseOrderRepository<Order> baseOrderRepository;

	@Resource
	private PaymentRepository paymentRepository;

	@Override
	public Optional<Payment> findPaymentByTradeNo(Payment.Method paymentMethod, String tradeNo) {
		return paymentRepository.findByMethodAndTradeNo(paymentMethod, tradeNo);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Order findByOrderNo(String orderNo) {
		return doFindOrderByOrderNo(orderNo);
	}

	@Override
	public Optional<Order> lookupByOrderNo(String orderNo) {
		return doLookupOrderByOrderNo(orderNo);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Order findOrder(String orderId) {
		return doFindOrder(orderId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order updateOrderStatus(String orderId, Status status) {
		return doUpdateOrderStatus(orderId, status);
	}

	@Override
	public PaymentRequest requestOrderPayment(String orderId, Payment.Method paymentMethod) {
		return doRequestOrderPayment(orderId, paymentMethod);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order payOrder(String orderId, Payment payment) {
		return doPayOrder(orderId, payment);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order requestOrderRefund(String orderId, RefundRequest request) {
		return doRequestOrderRefund(orderId, request);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order acceptOrderRefund(String orderId, BigDecimal refundAmount) {
		return doAcceptOrderRefund(orderId, refundAmount);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order rejectOrderRefund(String orderId, String rejectReason) {
		return doRejectOrderRefund(orderId, rejectReason);
	}

	@Override
	public Order handleOrderRefundFailure(String orderId, String refundFailureCause) {
		return doHandleOrderRefundFailure(orderId, refundFailureCause);
	}

	// TODO check if #result.id works?
	// Cache cache = cacheManager.getCache(CACHE_NAME);
	// Order order = doUpdateOrderStatusByOrderNo(orderNo, status);
	// cache.put(order.getId(), order);
	// return order;
	@CachePut(cacheNames = CACHE_NAME, key = "#result.id")
	@Override
	public Order updateOrderStatusByOrderNo(String orderNo, Status status) {
		return doUpdateOrderStatusByOrderNo(orderNo, status);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order updateOrderPrice(String orderId, double newPrice) {
		return doUpdateOrderPrice(orderId, newPrice);
	}

	@Override
	public Order saveOrder(Order order) {
		return safeExecute(() -> baseOrderRepository.save(order), "Save order %s failed", order);
	}

	@Override
	public Page<Order> listAllOrders(Order.Status status, int page, int pageSize) {
		return doListAllOrders(status, page, pageSize);
	}

	@Override
	public Page<Order> listAllUserOrders(String userId, Order.Status status, int page, int pageSize) {
		return doListAllUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<Order> listUserOrders(String userId, Order.Status status, int page, int pageSize) {
		return doListUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<Order> listUserOrdersInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		return doListUserOrdersInStatuses(userId, statuses, page, pageSize);
	}

	@Override
	public Page<Order> listUserOrdersNotInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		return doListUserOrdersNotInStatuses(userId, statuses, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteOrder(String orderId) {
		doDeleteOrder(orderId);
	}

	@Override
	public void deleteOrders(String userId) {
		doDeleteOrders(userId);
	}

	@Override
	protected BaseOrderRepository<Order> getOrderRepository() {
		return baseOrderRepository;
	}
}
