package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.BaseOrderRepository;
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
	public Order saveOrder(Order order) {
		return baseOrderRepository.save(order);
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
