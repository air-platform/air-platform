package net.aircommunity.platform.service.internal.order;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.OrderRef;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.service.internal.Pages;

/**
 * Common order service for ADMIN ONLY.
 * 
 * @author Bin.Zhang
 */
@Service("adminOrderService")
@Transactional(readOnly = true)
public class AdminCommonOrderServiceImpl extends CommonOrderServiceImpl {
	private static final Logger LOG = LoggerFactory.getLogger(AdminCommonOrderServiceImpl.class);

	@Override
	protected void doInitialize() {
		setOrderProcessor(new CommonOrderProcessor(false/* visibleOrderOnly */, configuration, orderRefRepository,
				serviceRegistry));
		// !!! NOTE:
		// use it with caution when production, it will load all data, ONLY for initialization
		if (configuration.isOrderRebuildRef()) {
			rebuildOrderRef();
			LOG.debug("Finished order rebuild with order refs: {}", orderRefRepository.count());
		}
	}

	// XXX NOTE: @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	// orderRepository.findAllByOrderByCreationDateDesc() streaming is NOT working with lifecycle callback
	// always try-resource when streaming data from DB:
	private void rebuildOrderRef() {
		orderRefRepository.deleteAllInBatch();
		try (Stream<Order> stream = orderRepository
				.findAllByOrderByCreationDateDesc(Pages.createPageRequest(1, Integer.MAX_VALUE)).getContent()
				.stream()) {
			stream.forEach(order -> {
				OrderRef orderRef = new OrderRef();
				orderRef.setOrderId(order.getId());
				orderRef.setOrderNo(order.getOrderNo());
				Product product = order.getProduct();
				orderRef.setVendorId(product == null ? null : product.getVendor().getId());
				orderRef.setOwnerId(order.getOwner().getId());
				orderRef.setStatus(order.getStatus());
				orderRef.setType(order.getType());
				orderRef.setCreationDate(order.getCreationDate());
				orderRefRepository.save(orderRef);
			});
		}
		finally {
			orderRefRepository.flush();
		}
	}
}
