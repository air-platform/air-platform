package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class CommonOrderServiceImpl implements CommonOrderService {

	@Resource
	private BaseOrderRepository<Order> baseOrderRepository;

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

}
