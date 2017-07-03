package net.aircommunity.platform.service.internal.order;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.PaymentRepository;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.product.CommentService;

/**
 * Common order service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class CommonOrderServiceImpl extends AbstractBaseOrderService<Order> implements CommonOrderService {

	// TODO use orderRefRepository to improve performance

	@Resource
	private BaseOrderRepository<Order> baseOrderRepository;

	@Resource
	private PaymentRepository paymentRepository;

	@Resource
	private CommentService commentService;

	@Override
	public boolean existsOrderForUser(String userId) {
		return baseOrderRepository.existsByOwnerId(userId);
	}

	@Override
	public Optional<Payment> findPaymentByTradeNo(Payment.Method paymentMethod, String tradeNo) {
		return paymentRepository.findByTradeNoAndMethod(tradeNo, paymentMethod);
	}

	// NOTE: need update this cache in case order is updated
	@Cacheable(cacheNames = CACHE_NAME_ORDER_NO)
	@Override
	public Order findByOrderNo(String orderNo) {
		return doFindOrderByOrderNo(orderNo);
	}

	// NOTE: According to spring cache doc:
	// The cache abstraction supports java.util.Optional, using its content as cached value only if it present. #result
	// always refers to the business entity and never on a supported wrapper
	@Cacheable(cacheNames = CACHE_NAME_ORDER_NO)
	@Override
	public Optional<Order> lookupByOrderNo(String orderNo) {
		return doLookupOrderByOrderNo(orderNo);
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

	@Override
	public PaymentRequest requestOrderPayment(String orderId, Payment.Method paymentMethod) {
		return doRequestOrderPayment(orderId, paymentMethod);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order payOrder(String orderId, Payment payment) {
		return doPayOrder(orderId, payment);
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
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order requestOrderRefund(String orderId, String refundReason) {
		return doRequestOrderRefund(orderId, refundReason);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order handleOrderRefundFailure(String orderId, String refundFailureCause) {
		return doHandleOrderRefundFailure(orderId, refundFailureCause);
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

	@Override
	public Page<Order> listUserOrders(String userId, int days, int page, int pageSize) {
		return doListUserOrdersOfRecentDays(userId, days, page, pageSize);// OK ? full scan?
	}

	@Override
	public Page<Order> listUserOrdersInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		return doListUserOrdersInStatuses(userId, statuses, page, pageSize); // OK ? full scan?
	}

	@Override
	public Page<Order> listUserOrdersNotInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		return doListUserOrdersNotInStatuses(userId, statuses, page, pageSize); // NOT USED
	}

	// TODO: seldom called, improve cache without clear
	@Transactional
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_ORDER_NO }, allEntries = true)
	@Override
	public void purgeOrders(String userId) {
		commentService.deleteCommentsOfAccount(userId);
		doDeleteOrders(userId);
	}

	@Override
	protected BaseOrderRepository<Order> getOrderRepository() {
		return baseOrderRepository;
	}

}
