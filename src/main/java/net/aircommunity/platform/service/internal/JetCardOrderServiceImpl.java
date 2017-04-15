package net.aircommunity.platform.service.internal;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.JetCard;
import net.aircommunity.platform.model.JetCardOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.JetCardOrderRepository;
import net.aircommunity.platform.service.JetCardOrderService;
import net.aircommunity.platform.service.JetCardService;

/**
 * JetCardOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class JetCardOrderServiceImpl extends AbstractServiceSupport implements JetCardOrderService {
	private static final String CACHE_NAME = "cache.jetcardorder";

	@Resource
	private JetCardOrderRepository jetCardOrderRepository;

	@Resource
	private JetCardService jetCardService;

	@Override
	public JetCardOrder createJetCardOrder(String userId, JetCardOrder jetCardOrder) {
		User owner = findAccount(userId, User.class);
		JetCardOrder newJetCardOrder = new JetCardOrder();
		newJetCardOrder.setCreationDate(new Date());
		newJetCardOrder.setStatus(Order.Status.PENDING);
		copyProperties(jetCardOrder, newJetCardOrder);
		// set vendor
		newJetCardOrder.setOwner(owner);
		return jetCardOrderRepository.save(newJetCardOrder);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public JetCardOrder findJetCardOrder(String jetCardOrderId) {
		JetCardOrder jetCardOrder = jetCardOrderRepository.findOne(jetCardOrderId);
		if (jetCardOrder == null) {
			throw new AirException(Codes.JETCARD_ORDER_NOT_FOUND,
					String.format("JetCardOrder %s is not found", jetCardOrderId));
		}
		return jetCardOrder;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#jetCardOrderId")
	@Override
	public JetCardOrder updateJetCardOrder(String jetCardOrderId, JetCardOrder newJetCardOrder) {
		JetCardOrder jetCardOrder = findJetCardOrder(jetCardOrderId);
		copyProperties(newJetCardOrder, jetCardOrder);
		return jetCardOrderRepository.save(jetCardOrder);
	}

	private void copyProperties(JetCardOrder src, JetCardOrder tgt) {
		tgt.setContact(src.getContact());
		tgt.setNote(src.getNote());
		JetCard jetCard = tgt.getJetCard();
		if (jetCard != null) {
			jetCard = jetCardService.findJetCard(jetCard.getId());
			tgt.setJetCard(jetCard);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#jetCardOrderId")
	@Override
	public JetCardOrder updateJetCardOrderStatus(String jetCardOrderId, Status status) {
		JetCardOrder jetCardOrder = findJetCardOrder(jetCardOrderId);
		jetCardOrder.setStatus(status);
		return jetCardOrderRepository.save(jetCardOrder);
	}

	@Override
	public Page<JetCardOrder> listUserJetCardOrders(String userId, Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(jetCardOrderRepository.findByOwnerIdOrderByCreationDateDesc(userId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(jetCardOrderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<JetCardOrder> listTenantJetCardOrders(String tenantId, Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(jetCardOrderRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(jetCardOrderRepository.findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<JetCardOrder> listJetCardOrders(Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(
					jetCardOrderRepository.findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(jetCardOrderRepository.findByStatusOrderByCreationDateDesc(status,
				Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#jetCardOrderId")
	@Override
	public void deleteJetCardOrder(String jetCardOrderId) {
		jetCardOrderRepository.delete(jetCardOrderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteJetCardOrders(String userId) {
		jetCardOrderRepository.deleteByOwnerId(userId);
	}

}
