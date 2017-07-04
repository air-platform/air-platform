package net.aircommunity.platform.service.internal.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.JetTravelOrderRepository;
import net.aircommunity.platform.service.order.JetTravelOrderService;
import net.aircommunity.platform.service.order.OrderServiced;

/**
 * JetTravelOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@OrderServiced(Product.Type.JETTRAVEL)
@Transactional(readOnly = true)
public class JetTravelOrderServiceImpl extends AbstractVendorAwareOrderService<JetTravelOrder>
		implements JetTravelOrderService {

	@Autowired
	public JetTravelOrderServiceImpl(JetTravelOrderRepository jetTravelOrderRepository) {
		super(jetTravelOrderRepository);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.JETTRAVEL_ORDER_NOT_FOUND;
	}

	// TODO REMOVE
	// private static final String CACHE_NAME = "cache.jettravel-order";
	// @Override
	// public JetTravelOrder createJetTravelOrder(String userId, JetTravelOrder order) {
	// return doCreateOrder(userId, order);
	// }
	//
	// @Cacheable(cacheNames = CACHE_NAME)
	// @Override
	// public JetTravelOrder findJetTravelOrder(String orderId) {
	// return doFindOrder(orderId);
	// }
	//
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public JetTravelOrder updateJetTravelOrder(String orderId, JetTravelOrder newOrder) {
	// return doUpdateOrder(orderId, newOrder);
	// }
	//
	// @CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public JetTravelOrder updateJetTravelOrderStatus(String orderId, Status status) {
	// return doUpdateOrderStatus(orderId, status);
	// }
	//
	// @Override
	// public Page<JetTravelOrder> listUserJetTravelOrders(String userId, Order.Status status, int page, int pageSize) {
	// return doListUserOrders(userId, status, page, pageSize);
	// }
	//
	// @Override
	// public Page<JetTravelOrder> listTenantJetTravelOrders(String tenantId, Order.Status status, int page,
	// int pageSize) {
	// return doListTenantOrders(tenantId, status, page, pageSize);
	// }
	//
	// @Override
	// public Page<JetTravelOrder> listJetTravelOrders(Order.Status status, int page, int pageSize) {
	// return doListAllOrders(status, page, pageSize);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	// @Override
	// public void deleteJetTravelOrder(String orderId) {
	// doDeleteOrder(orderId);
	// }
	//
	// @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	// @Override
	// public void deleteJetTravelOrders(String userId) {
	// doDeleteOrders(userId);
	// }
}
