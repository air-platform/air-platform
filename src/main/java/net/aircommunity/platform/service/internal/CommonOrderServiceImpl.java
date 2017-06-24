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
import net.aircommunity.platform.model.domain.Order.Status;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.PaymentRepository;
import net.aircommunity.platform.service.CommentService;
import net.aircommunity.platform.service.CommonOrderService;

/**
 * Common order service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CommonOrderServiceImpl extends AbstractOrderService<Order> implements CommonOrderService {

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

	// XXX REMOVE we cannot cache by orderNo, need update cache
	// @Cacheable(cacheNames = CACHE_NAME)
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
	public Order acceptOrderRefund(String orderId, Refund refund) {
		return doAcceptOrderRefund(orderId, refund);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order rejectOrderRefund(String orderId, String rejectReason) {
		return doRejectOrderRefund(orderId, rejectReason);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order handleOrderRefundFailure(String orderId, String refundFailureCause) {
		return doHandleOrderRefundFailure(orderId, refundFailureCause);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order cancelOrder(String orderId, String cancelReason) {
		return doCancelOrder(orderId, cancelReason);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#result.id")
	@Override
	public Order updateOrderStatusByOrderNo(String orderNo, Status status) {
		return doUpdateOrderStatusByOrderNo(orderNo, status);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order updateOrderTotalAmount(String orderId, BigDecimal newTotalAmount) {
		return doUpdateOrderTotalAmount(orderId, newTotalAmount);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order updateOrderCommented(String orderId) {
		return doUpdateOrderCommented(orderId);
	}

	@Override
	public Page<Order> listAllOrders(Order.Status status, Order.Type type, int page, int pageSize) {
		return doListAllOrders(status, type, page, pageSize); // ADMIN full scan
	}

	@Override
	public Page<Order> listAllUserOrders(String userId, Order.Status status, Order.Type type, int page, int pageSize) {
		return doListAllUserOrders(userId, status, type, page, pageSize); // ADMIN full scan
	}

	@Override
	public Page<Order> listUserOrders(String userId, Order.Status status, int page, int pageSize) {
		return doListUserOrders(userId, status, page, pageSize);// OK
	}

	@Override
	public Page<Order> listUserOrdersInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		return doListUserOrdersInStatuses(userId, statuses, page, pageSize); // OK
	}

	@Override
	public Page<Order> listUserOrdersNotInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		return doListUserOrdersNotInStatuses(userId, statuses, page, pageSize); // NOT USED
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteOrder(String orderId) {
		doDeleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteOrders(String userId) {
		doDeleteOrders(userId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
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
