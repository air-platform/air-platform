package net.aircommunity.platform.service.internal.order;

import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * Abstract Standard order service support.
 * 
 * @author Bin.Zhang
 *
 * @param <T> order type
 */
abstract class AbstractStandardOrderService<T extends Order> extends AbstractBaseOrderService<T>
		implements StandardOrderService<T> {

	@Transactional
	@Override
	public T createOrder(String userId, T order) {
		return doCreateOrder(userId, order);
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public T updateOrder(String orderId, T order) {
		return doUpdateOrder(orderId, order);
	}

}
