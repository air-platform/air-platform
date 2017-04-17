package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.JetCardOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.repository.JetCardOrderRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.JetCardOrderService;
import net.aircommunity.platform.service.JetCardService;

/**
 * JetCardOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class JetCardOrderServiceImpl extends AbstractVendorAwareOrderService<JetCardOrder>
		implements JetCardOrderService {
	private static final String CACHE_NAME = "cache.jetcard-order";

	@Resource
	private JetCardOrderRepository jetCardOrderRepository;

	@Resource
	private JetCardService jetCardService;

	@Override
	public JetCardOrder createJetCardOrder(String userId, JetCardOrder order) {
		return createOrder(userId, order);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public JetCardOrder findJetCardOrder(String orderId) {
		return findOrder(orderId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public JetCardOrder updateJetCardOrder(String orderId, JetCardOrder newOrder) {
		return updateOrder(orderId, newOrder);
	}

	@Override
	protected void copyProperties(JetCardOrder src, JetCardOrder tgt) {
		tgt.setContact(src.getContact());
		tgt.setNote(src.getNote());
		JetCard jetCard = tgt.getJetCard();
		if (jetCard != null) {
			jetCard = jetCardService.findJetCard(jetCard.getId());
			tgt.setJetCard(jetCard);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public JetCardOrder updateJetCardOrderStatus(String orderId, Status status) {
		return updateOrderStatus(orderId, status);
	}

	@Override
	public Page<JetCardOrder> listUserJetCardOrders(String userId, Order.Status status, int page, int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<JetCardOrder> listTenantJetCardOrders(String tenantId, Order.Status status, int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	@Override
	public Page<JetCardOrder> listJetCardOrders(Order.Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteJetCardOrder(String orderId) {
		deleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteJetCardOrders(String userId) {
		deleteOrders(userId);
	}

	@Override
	protected VendorAwareOrderRepository<JetCardOrder> getOrderRepository() {
		return jetCardOrderRepository;
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.JETCARD_ORDER_NOT_FOUND;
	}

}
