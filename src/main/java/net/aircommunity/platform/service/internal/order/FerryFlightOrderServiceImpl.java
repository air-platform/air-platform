package net.aircommunity.platform.service.internal.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.FerryFlightOrder;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.FerryFlightOrderRepository;
import net.aircommunity.platform.service.order.FerryFlightOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;

/**
 * FerryFlightOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedOrderService(Product.Type.FERRYFLIGHT)
@Transactional(readOnly = true)
public class FerryFlightOrderServiceImpl extends AbstractVendorAwareOrderService<FerryFlightOrder>
		implements FerryFlightOrderService {

	@Autowired
	public FerryFlightOrderServiceImpl(FerryFlightOrderRepository ferryFlightOrderRepository) {
		super(ferryFlightOrderRepository);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.FERRYFLIGHT_ORDER_NOT_FOUND;
	}
}
