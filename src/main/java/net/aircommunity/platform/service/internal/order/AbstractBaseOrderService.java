package net.aircommunity.platform.service.internal.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.model.domain.Trade;
import net.aircommunity.platform.model.payment.PaymentRequest;
import net.aircommunity.platform.service.order.OrderService;

/**
 * Abstract base order service support. (orders should extends this class to simplify implementation)
 * 
 * @author Bin.Zhang
 *
 * @param <T> the order type
 */
abstract class AbstractBaseOrderService<T extends Order> extends AbstractOrderService<T> implements OrderService<T> {

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public T findOrder(String orderId) {
		return doFindOrder(orderId);
	}

	@Override
	public List<T> searchOrder(String orderNo) {
		return orderProcessor.searchOrder(orderNo);
	}

	@Override
	public List<T> findOrders(List<String> orderIds) {
		return orderProcessor.findOrders(orderIds);
	}

	@Override
	public Optional<Payment> findPaymentByTradeNo(Trade.Method method, String tradeNo) {
		return paymentRepository.findByTradeNoAndMethod(tradeNo, method);
	}

	// NOTE: need update this cache in case order is updated
	@Cacheable(cacheNames = CACHE_NAME_ORDER_NO)
	@Override
	public T findByOrderNo(String orderNo) {
		return doFindOrderByOrderNo(orderNo);
	}

	// NOTE: According to spring cache doc:
	// The cache abstraction supports java.util.Optional, using its content as cached value only if it present. #result
	// always refers to the business entity and never on a supported wrapper
	@Cacheable(cacheNames = CACHE_NAME_ORDER_NO)
	@Override
	public Optional<T> lookupByOrderNo(String orderNo) {
		return doLookupOrderByOrderNo(orderNo);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public T updateOrderStatus(String orderId, Order.Status status) {
		return doUpdateOrderStatus(orderId, status);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public T updateOrderTotalAmount(String orderId, BigDecimal newTotalAmount) {
		return doUpdateOrderTotalAmount(orderId, newTotalAmount);
	}

	@Transactional
	@Override
	public PaymentRequest requestOrderPayment(String orderId, Trade.Method method) {
		return doRequestOrderPayment(orderId, method);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order acceptOrderPayment(String orderId, Payment payment) {
		return doAcceptOrderPayment(orderId, payment);
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
	public Order handleOrderPaymentFailure(String orderNo, String paymentFailureCause) {
		return doHandleOrderPaymentFailure(orderNo, paymentFailureCause);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order handleOrderRefundFailure(String orderNo, String refundFailureCause) {
		return doHandleOrderRefundFailure(orderNo, refundFailureCause);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"), //
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public T softDeleteOrder(String orderId) {
		return doUpdateOrderStatus(orderId, Order.Status.DELETED);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order initiateOrderRefund(String orderId, BigDecimal refundAmount, String refundReason) {
		return doInitiateOrderRefund(orderId, refundAmount, refundReason);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order acceptOrderRefund(String orderId, BigDecimal refundAmount) {
		return doAcceptOrderRefund(orderId, refundAmount);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order acceptOrderRefund(String orderId, Refund refund) {
		return doAcceptOrderRefund(orderId, refund);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order rejectOrderRefund(String orderId, String rejectReason) {
		return doRejectOrderRefund(orderId, rejectReason);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order cancelOrder(String orderId, String cancelReason) {
		return doCancelOrder(orderId, cancelReason);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order closeOrder(String orderId, String closeReason) {
		return doCloseOrder(orderId, closeReason);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#result.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order updateOrderStatusByOrderNo(String orderNo, Status status) {
		return doUpdateOrderStatusByOrderNo(orderNo, status);
	}

	@Transactional
	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order updateOrderCommented(String orderId) {
		return doUpdateOrderCommented(orderId);
	}

	@Transactional
	@Caching(evict = { //
			@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId"),
			@CacheEvict(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public T deleteOrder(String orderId) {
		T order = doFindOrder(orderId);
		doDeleteOrder(orderId);
		return order;
	}

	// TODO: seldom called, improve cache without clear
	@Transactional
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_ORDER_NO }, allEntries = true)
	@Override
	public void deleteOrders(String userId) {
		doDeleteOrders(userId);
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
	public Page<T> listAllOrders(Order.Status status, Product.Type type, int page, int pageSize) {
		return doListAllOrders(status, type, page, pageSize); // ADMIN
	}

	@Override
	public Page<T> listAllUserOrders(String userId, Order.Status status, Product.Type type, int page, int pageSize) {
		return doListAllUserOrders(userId, status, type, page, pageSize); // ADMIN
	}

	@Override
	public Page<T> listUserOrders(String userId, Order.Status status, int page, int pageSize) {
		return doListUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<T> listUserOrders(String userId, int days, int page, int pageSize) {
		return doListUserOrdersOfRecentDays(userId, days, page, pageSize);
	}

	@Override
	public Page<T> listUserOrders(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		return doListUserOrdersInStatuses(userId, statuses, page, pageSize);
	}
}
