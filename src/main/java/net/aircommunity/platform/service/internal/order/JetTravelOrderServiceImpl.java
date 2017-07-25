package net.aircommunity.platform.service.internal.order;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.JetTravelOrderRepository;
import net.aircommunity.platform.service.order.JetTravelOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;

/**
 * JetTravelOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedOrderService(Product.Type.JETTRAVEL)
@Transactional(readOnly = true)
public class JetTravelOrderServiceImpl extends AbstractVendorAwareOrderService<JetTravelOrder>
		implements JetTravelOrderService {

	@Inject
	public JetTravelOrderServiceImpl(JetTravelOrderRepository jetTravelOrderRepository) {
		super(jetTravelOrderRepository);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.JETTRAVEL_ORDER_NOT_FOUND;
	}
}
