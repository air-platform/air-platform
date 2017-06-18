package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.FerryFlightOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.FerryFlightOrderRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.FerryFlightOrderService;
import net.aircommunity.platform.service.FerryFlightService;

/**
 * FerryFlightOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class FerryFlightOrderServiceImpl extends AbstractVendorAwareOrderService<FerryFlightOrder>
		implements FerryFlightOrderService {
	private static final String CACHE_NAME = "cache.ferryflight-order";

	@Resource
	private FerryFlightOrderRepository ferryFlightOrderRepository;

	@Resource
	private FerryFlightService ferryFlightService;

	@Override
	public FerryFlightOrder createFerryFlightOrder(String userId, FerryFlightOrder order) {
		return doCreateOrder(userId, order);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public FerryFlightOrder findFerryFlightOrder(String orderId) {
		return doFindOrder(orderId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public FerryFlightOrder updateFerryFlightOrder(String orderId, FerryFlightOrder newOrder) {
		return doUpdateOrder(orderId, newOrder);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public FerryFlightOrder updateFerryFlightOrderStatus(String orderId, Status status) {
		return doUpdateOrderStatus(orderId, status);
	}

	@Override
	public Page<FerryFlightOrder> listUserFerryFlightOrders(String userId, Order.Status status, int page,
			int pageSize) {
		return doListUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<FerryFlightOrder> listTenantFerryFlightOrders(String tenantId, Order.Status status, int page,
			int pageSize) {
		return doListTenantOrders(tenantId, status, page, pageSize);
	}

	@Override
	public Page<FerryFlightOrder> listFerryFlightOrders(Order.Status status, int page, int pageSize) {
		return doListAllOrders(status, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteFerryFlightOrder(String orderId) {
		doDeleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteFerryFlightOrders(String userId) {
		doDeleteOrders(userId);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.FERRYFLIGHT_ORDER_NOT_FOUND;
	}

	@Override
	protected VendorAwareOrderRepository<FerryFlightOrder> getOrderRepository() {
		return ferryFlightOrderRepository;
	}

}
