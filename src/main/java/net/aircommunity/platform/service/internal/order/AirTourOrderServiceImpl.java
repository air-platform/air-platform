package net.aircommunity.platform.service.internal.order;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.AirTourOrder;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.AirTourOrderRepository;
import net.aircommunity.platform.service.order.AirTourOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;

/**
 * AirTourOrder service implementation.
 *
 * @author Bin.Zhang
 */
@ManagedOrderService(Product.Type.AIRTOUR)
@Transactional(readOnly = true)
public class AirTourOrderServiceImpl extends AbstractVendorAwareOrderService<AirTourOrder>
		implements AirTourOrderService {

	@Inject
	public AirTourOrderServiceImpl(AirTourOrderRepository airTourOrderRepository) {
		super(airTourOrderRepository);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.TOUR_ORDER_NOT_FOUND;
	}
}
