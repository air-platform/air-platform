package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Order;
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
public class CommonOrderServiceImpl implements CommonOrderService {
	private static final String CACHE_NAME = "cache.order";

	@Resource
	private BaseOrderRepository<Order> baseOrderRepository;

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Order findOrder(String orderId) {
		Order order = baseOrderRepository.findOne(orderId);
		if (order == null || order.getStatus() == Order.Status.DELETED) {
			throw new AirException(Codes.ORDER_NOT_FOUND,
					String.format("%s: %s is not found", order.getClass().getSimpleName(), orderId));
		}
		return order;
	}

	@Override
	public Page<Order> listAllUserOrders(String userId, Order.Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(baseOrderRepository.findByOwnerIdAndStatusNotOrderByCreationDateDesc(userId,
					Order.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(baseOrderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order cancelOrder(String orderId) {
		Order order = baseOrderRepository.findOne(orderId);
		order.setStatus(Order.Status.CANCELLED);
		return baseOrderRepository.save(order);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public Order deleteOrder(String orderId) {
		Order order = baseOrderRepository.findOne(orderId);
		order.setStatus(Order.Status.DELETED);
		return baseOrderRepository.save(order);
	}

}
