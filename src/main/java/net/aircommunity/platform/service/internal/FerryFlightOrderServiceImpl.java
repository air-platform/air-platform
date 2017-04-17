package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.FerryFlight;
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
		return createOrder(userId, order);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public FerryFlightOrder findFerryFlightOrder(String orderId) {
		return findOrder(orderId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public FerryFlightOrder updateFerryFlightOrder(String orderId, FerryFlightOrder newOrder) {
		return updateOrder(orderId, newOrder);
	}

	@Override
	protected void copyProperties(FerryFlightOrder src, FerryFlightOrder tgt) {
		tgt.setContact(src.getContact());
		tgt.setNote(src.getNote());
		tgt.setPassengers(src.getPassengers());
		FerryFlight ferryFlight = src.getFerryFlight();
		if (ferryFlight != null) {
			ferryFlight = ferryFlightService.findFerryFlight(ferryFlight.getId());
			tgt.setFerryFlight(ferryFlight);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public FerryFlightOrder updateFerryFlightOrderStatus(String orderId, Status status) {
		return updateOrderStatus(orderId, status);
	}

	@Override
	public Page<FerryFlightOrder> listUserFerryFlightOrders(String userId, Order.Status status, int page,
			int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<FerryFlightOrder> listTenantFerryFlightOrders(String tenantId, Order.Status status, int page,
			int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	@Override
	public Page<FerryFlightOrder> listFerryFlightOrders(Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteFerryFlightOrder(String orderId) {
		deleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteFerryFlightOrders(String userId) {
		deleteOrders(userId);
	}

	@Override
	protected VendorAwareOrderRepository<FerryFlightOrder> getOrderRepository() {
		return ferryFlightOrderRepository;
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.FERRYFLIGHT_ORDER_NOT_FOUND;
	}
}