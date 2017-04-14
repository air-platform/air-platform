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
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.FerryFlightOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.FerryFlightOrderRepository;
import net.aircommunity.platform.service.FerryFlightOrderService;
import net.aircommunity.platform.service.FerryFlightService;

/**
 * FerryFlightOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class FerryFlightOrderServiceImpl extends AbstractServiceSupport implements FerryFlightOrderService {
	private static final String CACHE_NAME = "cache.ferryflightorder";

	@Resource
	private FerryFlightOrderRepository ferryFlightOrderRepository;

	@Resource
	private FerryFlightService ferryFlightService;

	@Override
	public FerryFlightOrder createFerryFlightOrder(String userId, FerryFlightOrder FerryFlightOrder) {
		User owner = findAccount(userId, User.class);
		FerryFlightOrder newFerryFlightOrder = new FerryFlightOrder();
		newFerryFlightOrder.setCreationDate(new Date());
		newFerryFlightOrder.setStatus(Order.Status.PENDING);
		copyProperties(FerryFlightOrder, newFerryFlightOrder);
		// set vendor
		newFerryFlightOrder.setOwner(owner);
		return ferryFlightOrderRepository.save(newFerryFlightOrder);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public FerryFlightOrder findFerryFlightOrder(String FerryFlightOrderId) {
		FerryFlightOrder FerryFlightOrder = ferryFlightOrderRepository.findOne(FerryFlightOrderId);
		if (FerryFlightOrder == null) {
			throw new AirException(Codes.FERRYFLIGHT_ORDER_NOT_FOUND,
					String.format("FerryFlightOrder %s is not found", FerryFlightOrderId));
		}
		return FerryFlightOrder;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#ferryFlightOrderId")
	@Override
	public FerryFlightOrder updateFerryFlightOrder(String FerryFlightOrderId, FerryFlightOrder newFerryFlightOrder) {
		FerryFlightOrder FerryFlightOrder = findFerryFlightOrder(FerryFlightOrderId);
		copyProperties(newFerryFlightOrder, FerryFlightOrder);
		return ferryFlightOrderRepository.save(FerryFlightOrder);
	}

	private void copyProperties(FerryFlightOrder src, FerryFlightOrder tgt) {
		tgt.setContact(src.getContact());
		tgt.setNote(src.getNote());
		tgt.setPassengers(src.getPassengers());
		FerryFlight ferryFlight = tgt.getFerryFlight();
		if (ferryFlight != null) {
			ferryFlight = ferryFlightService.findFerryFlight(ferryFlight.getId());
			tgt.setFerryFlight(ferryFlight);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#ferryFlightOrderId")
	@Override
	public FerryFlightOrder updateFerryFlightOrderStatus(String FerryFlightOrderId, Status status) {
		FerryFlightOrder FerryFlightOrder = findFerryFlightOrder(FerryFlightOrderId);
		FerryFlightOrder.setStatus(status);
		return ferryFlightOrderRepository.save(FerryFlightOrder);
	}

	@Override
	public Page<FerryFlightOrder> listUserFerryFlightOrders(String userId, Order.Status status, int page,
			int pageSize) {
		if (status == null) {
			return Pages.adapt(ferryFlightOrderRepository.findByOwnerIdOrderByCreationDateDesc(userId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(ferryFlightOrderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}


	@Override
	public Page<FerryFlightOrder> listTenantFerryFlightOrders(String tenantId, Order.Status status, int page,
			int pageSize) {
		if (status == null) {
			return Pages.adapt(ferryFlightOrderRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(ferryFlightOrderRepository.findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
				Pages.createPageRequest(page, pageSize)));
	}
	
	@Override
	public Page<FerryFlightOrder> listFerryFlightOrders(Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(ferryFlightOrderRepository
					.findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(ferryFlightOrderRepository.findByStatusOrderByCreationDateDesc(status,
				Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#ferryFlightOrderId")
	@Override
	public void deleteFerryFlightOrder(String FerryFlightOrderId) {
		ferryFlightOrderRepository.delete(FerryFlightOrderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteFerryFlightOrders(String userId) {
		ferryFlightOrderRepository.deleteByOwnerId(userId);
	}

}
