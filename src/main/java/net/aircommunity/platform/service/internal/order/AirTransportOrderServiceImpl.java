package net.aircommunity.platform.service.internal.order;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.repository.AirTransportOrderRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.order.AirTransportOrderService;

/**
 * AirTransportOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class AirTransportOrderServiceImpl extends AbstractVendorAwareOrderService<AirTransportOrder>
		implements AirTransportOrderService {

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.airtransport-order";

	@Resource
	private AirTransportOrderRepository airTransportOrderRepository;

	// @Override
	// public AirTransportOrder createAirTransportOrder(String userId, AirTransportOrder order) {
	// return doCreateOrder(userId, order);
	// }
	//
	// @Cacheable(cacheNames = CACHE_NAME)
	// @Override
	// public AirTransportOrder findAirTransportOrder(String orderId) {
	// return doFindOrder(orderId);
	// }
	//
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public AirTransportOrder updateAirTransportOrder(String orderId, AirTransportOrder newOrder) {
	// return doUpdateOrder(orderId, newOrder);
	// }
	//
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public AirTransportOrder updateAirTransportOrderStatus(String orderId, Status status) {
	// return doUpdateOrderStatus(orderId, status);
	// }
	//
	// @Override
	// public Page<AirTransportOrder> listUserAirTransportOrders(String userId, Order.Status status, int page,
	// int pageSize) {
	// return doListUserOrders(userId, status, page, pageSize);
	// }
	//
	// @Override
	// public Page<AirTransportOrder> listTenantAirTransportOrders(String tenantId, Order.Status status, int page,
	// int pageSize) {
	// return doListTenantOrders(tenantId, status, page, pageSize);
	// }
	//
	// @Override
	// public Page<AirTransportOrder> listAirTransportOrders(Order.Status status, int page, int pageSize) {
	// return doListAllOrders(status, page, pageSize);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public void deleteAirTransportOrder(String orderId) {
	// doDeleteOrder(orderId);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	// @Override
	// public void deleteAirTransportOrders(String userId) {
	// doDeleteOrders(userId);
	// }

	@Override
	protected Code orderNotFoundCode() {
		return Codes.AIRTRANSPORT_ORDER_NOT_FOUND;
	}

	@Override
	protected VendorAwareOrderRepository<AirTransportOrder> getOrderRepository() {
		return airTransportOrderRepository;
	}

}
