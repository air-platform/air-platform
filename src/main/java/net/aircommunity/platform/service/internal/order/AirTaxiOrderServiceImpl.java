package net.aircommunity.platform.service.internal.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.AirTaxiOrder;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.AirTaxiOrderRepository;
import net.aircommunity.platform.service.order.AirTaxiOrderService;
import net.aircommunity.platform.service.order.OrderServiced;

/**
 * AirTaxiOrder service implementation.
 *
 * @author Bin.Zhang
 */
@OrderServiced(Product.Type.AIRTAXI)
@Transactional(readOnly = true)
public class AirTaxiOrderServiceImpl extends AbstractVendorAwareOrderService<AirTaxiOrder>
		implements AirTaxiOrderService {

	@Autowired
	public AirTaxiOrderServiceImpl(AirTaxiOrderRepository airTaxiOrderRepository) {
		super(airTaxiOrderRepository);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.TAXI_ORDER_NOT_FOUND;
	}

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.airtaxi-order";

	// @Override
	// public AirTaxiOrder createAirTaxiOrder(String userId, AirTaxiOrder order) {
	// return doCreateOrder(userId, order);
	// }
	//
	// @Cacheable(cacheNames = CACHE_NAME)
	// @Override
	// public AirTaxiOrder findAirTaxiOrder(String orderId) {
	// return doFindOrder(orderId);
	// }
	//
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public AirTaxiOrder updateAirTaxiOrder(String orderId, AirTaxiOrder newOrder) {
	// return doUpdateOrder(orderId, newOrder);
	// }

	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public AirTaxiOrder updateAirTaxiOrderStatus(String orderId, Status status) {
	// return doUpdateOrderStatus(orderId, status);
	// }

	// @Override
	// public Page<AirTaxiOrder> listUserAirTaxiOrders(String userId, Status status, int page, int pageSize) {
	// return doListUserOrders(userId, status, page, pageSize);
	// }
	//
	// @Override
	// public Page<AirTaxiOrder> listTenantAirTaxiOrders(String tenantId, Status status, int page, int pageSize) {
	// return doListTenantOrders(tenantId, status, page, pageSize);
	// }
	//
	// @Override
	// public Page<AirTaxiOrder> listAirTaxiOrders(Status status, int page, int pageSize) {
	// return doListAllOrders(status, page, pageSize);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public void deleteAirTaxiOrder(String orderId) {
	// doDeleteOrder(orderId);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	// @Override
	// public void deleteAirTaxiOrders(String userId) {
	// doDeleteOrders(userId);
	// }

}
