package net.aircommunity.platform.service.internal.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.AirTaxiOrder;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.repository.AirTaxiOrderRepository;
import net.aircommunity.platform.service.order.AirTaxiOrderService;
import net.aircommunity.platform.service.order.annotation.ManagedOrderService;

/**
 * AirTaxiOrder service implementation.
 *
 * @author Bin.Zhang
 */
@ManagedOrderService(Product.Type.AIRTAXI)
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
}
