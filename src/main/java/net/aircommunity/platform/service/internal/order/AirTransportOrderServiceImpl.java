package net.aircommunity.platform.service.internal.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.AirTransportOrderRepository;
import net.aircommunity.platform.service.order.AirTransportOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;

/**
 * AirTransportOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@ManagedOrderService(Product.Type.AIRTRANSPORT)
@Transactional(readOnly = true)
public class AirTransportOrderServiceImpl extends AbstractVendorAwareOrderService<AirTransportOrder>
		implements AirTransportOrderService {

	@Autowired
	public AirTransportOrderServiceImpl(AirTransportOrderRepository airTransportOrderRepository) {
		super(airTransportOrderRepository);
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.AIRTRANSPORT_ORDER_NOT_FOUND;
	}
}
