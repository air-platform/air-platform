package net.aircommunity.platform.service.internal.order;

import java.math.BigDecimal;
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
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.repository.AirTaxiOrderRepository;
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
@Transactional
public class CommonOrderServiceImpl extends AbstractOrderService<Order> implements CommonOrderService {
	protected static final String CACHE_NAME_ORDER_NO = "cache.orderno";

	@Resource
	private BaseOrderRepository<Order> baseOrderRepository;

	@Resource
	private PaymentRepository paymentRepository;

	@Resource
	private CommentService commentService;

	@Resource
	private AirTaxiOrderRepository airTaxiOrderRepository;

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

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Order findOrder(String orderId) {
		return doFindOrder(orderId);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"), //
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")//
	})
	@Override
	public Order updateOrderStatus(String orderId, Status status) {
		return doUpdateOrderStatus(orderId, status);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order updateOrderStatus(Order order, Status status) {
		return doUpdateOrderStatus(order, status);
	}

	@Override
	public PaymentRequest requestOrderPayment(String orderId, Payment.Method paymentMethod) {
		return doRequestOrderPayment(orderId, paymentMethod);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order payOrder(String orderId, Payment payment) {
		return doPayOrder(orderId, payment);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order payOrder(Order order, Payment payment) {
		return doPayOrder(order, payment);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order handleOrderPaymentFailure(Order order, String paymentFailureCause) {
		return doHandleOrderPaymentFailure(order, paymentFailureCause);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order requestOrderRefund(String orderId, String refundReason) {
		return doRequestOrderRefund(orderId, refundReason);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order initiateOrderRefund(String orderId, BigDecimal refundAmount, String refundReason) {
		return doInitiateOrderRefund(orderId, refundAmount, refundReason);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order acceptOrderRefund(String orderId, BigDecimal refundAmount) {
		return doAcceptOrderRefund(orderId, refundAmount);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order acceptOrderRefund(String orderId, Refund refund) {
		return doAcceptOrderRefund(orderId, refund);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order rejectOrderRefund(String orderId, String rejectReason) {
		return doRejectOrderRefund(orderId, rejectReason);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order handleOrderRefundFailure(String orderId, String refundFailureCause) {
		return doHandleOrderRefundFailure(orderId, refundFailureCause);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#order.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order handleOrderRefundFailure(Order order, String refundFailureCause) {
		return doHandleOrderRefundFailure(order, refundFailureCause);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order cancelOrder(String orderId, String cancelReason) {
		return doCancelOrder(orderId, cancelReason);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order closeOrder(String orderId, String closeReason) {
		return doCloseOrder(orderId, closeReason);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#result.id"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order updateOrderStatusByOrderNo(String orderNo, Status status) {
		return doUpdateOrderStatusByOrderNo(orderNo, status);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order updateOrderTotalAmount(String orderId, BigDecimal newTotalAmount) {
		return doUpdateOrderTotalAmount(orderId, newTotalAmount);
	}

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order updateOrderCommented(String orderId) {
		return doUpdateOrderCommented(orderId);
	}

	@Override
	public Page<Order> listAllOrders(Order.Status status, Product.Type type, int page, int pageSize) {
		return doListAllOrders(status, type, page, pageSize); // ADMIN full scan
	}

	@Override
	public Page<Order> listAllUserOrders(String userId, Order.Status status, Product.Type type, int page,
			int pageSize) {
		return doListAllUserOrders(userId, status, type, page, pageSize); // ADMIN full scan
	}

	@Override
	public Page<Order> listUserOrders(String userId, Order.Status status, int page, int pageSize) {
		return doListUserOrders(userId, status, page, pageSize);// OK ? full scan?
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

	@Caching(put = { //
			@CachePut(cacheNames = CACHE_NAME, key = "#orderId"),
			@CachePut(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order softDeleteOrder(String orderId) {
		return doUpdateOrderStatus(orderId, Order.Status.DELETED);
	}

	@Caching(evict = { //
			@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId"),
			@CacheEvict(cacheNames = CACHE_NAME_ORDER_NO, key = "#result.orderNo")
			//
	})
	@Override
	public Order deleteOrder(String orderId) {
		Order order = doFindOrder(orderId);
		doDeleteOrder(orderId);
		return order;
	}

	// TODO: seldom called, improve cache without clear
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_ORDER_NO }, allEntries = true)
	@Override
	public void deleteOrders(String userId) {
		doDeleteOrders(userId);
	}

	// TODO: seldom called, improve cache without clear
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
