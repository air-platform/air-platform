package net.aircommunity.platform.service.internal.order;

import java.math.BigDecimal;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;
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

	@Override
	public Page<T> listAllOrders(Order.Status status, Product.Type type, int page, int pageSize) {
		return doListAllOrders(status, type, page, pageSize); // ADMIN full scan
	}

	@Override
	public Page<T> listAllUserOrders(String userId, Order.Status status, Product.Type type, int page, int pageSize) {
		return doListAllUserOrders(userId, status, type, page, pageSize); // ADMIN full scan
	}

	@Override
	public Page<T> listUserOrders(String userId, Order.Status status, int page, int pageSize) {
		return doListUserOrders(userId, status, page, pageSize);// OK ? full scan?
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

}
