package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.AirTransportOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.AirTransportOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.service.AirTransportOrderService;
import net.aircommunity.platform.service.AirTransportService;
import net.aircommunity.platform.service.FerryFlightService;

/**
 * AirTransportOrder service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTransportOrderServiceImpl extends AbstractServiceSupport implements AirTransportOrderService {
	private static final String CACHE_NAME = "cache.airtransportorder";

	@Resource
	private AirTransportService airTransportService;

	@Resource
	private AirTransportOrderRepository airTransportOrderRepository;

	@Resource
	private FerryFlightService ferryFlightService;

	@Resource
	private PassengerRepository passengerRepository;

	@Override
	public AirTransportOrder createAirTransportOrder(String userId, AirTransportOrder airTransportOrder) {
		User owner = findAccount(userId, User.class);
		AirTransportOrder newAirTransportOrder = new AirTransportOrder();
		newAirTransportOrder.setCreationDate(new Date());
		newAirTransportOrder.setStatus(Order.Status.PENDING);
		copyProperties(airTransportOrder, newAirTransportOrder);
		// set vendor
		newAirTransportOrder.setOwner(owner);
		return airTransportOrderRepository.save(newAirTransportOrder);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTransportOrder findAirTransportOrder(String airTransportOrderId) {
		AirTransportOrder AirTransportOrder = airTransportOrderRepository.findOne(airTransportOrderId);
		if (AirTransportOrder == null) {
			throw new AirException(Codes.AIRTRANSPORT_ORDER_NOT_FOUND,
					String.format("AirTransportOrder %s is not found", airTransportOrderId));
		}
		return AirTransportOrder;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTransportOrderId")
	@Override
	public AirTransportOrder updateAirTransportOrder(String airTransportOrderId,
			AirTransportOrder newAirTransportOrder) {
		AirTransportOrder airTransportOrder = findAirTransportOrder(airTransportOrderId);
		copyProperties(newAirTransportOrder, airTransportOrder);
		return airTransportOrderRepository.save(airTransportOrder);
	}

	private void copyProperties(AirTransportOrder src, AirTransportOrder tgt) {
		AirTransport airTransport = airTransportService.findAirTransport(src.getAirTransport().getId());
		tgt.setAirTransport(airTransport);
		tgt.setDate(src.getDate());
		tgt.setNote(src.getNote());
		tgt.setTimeSlot(src.getTimeSlot());
		Set<Passenger> passengers = src.getPassengers();
		if (passengers != null) {
			// TODO better use service and throw NOT FOUND
			passengers = passengers.stream().map(passenger -> passengerRepository.findOne(passenger.getId()))
					.collect(Collectors.toSet());
			tgt.setPassengers(passengers);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTransportOrderId")
	@Override
	public AirTransportOrder updateAirTransportOrderStatus(String airTransportOrderId, Status status) {
		AirTransportOrder AirTransportOrder = findAirTransportOrder(airTransportOrderId);
		AirTransportOrder.setStatus(status);
		return airTransportOrderRepository.save(AirTransportOrder);
	}

	@Override
	public Page<AirTransportOrder> listUserAirTransportOrders(String userId, Order.Status status, int page,
			int pageSize) {
		if (status == null) {
			return Pages.adapt(airTransportOrderRepository.findByOwnerIdOrderByCreationDateDesc(userId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(airTransportOrderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransportOrder> listTenantAirTransportOrders(String tenantId, Order.Status status, int page,
			int pageSize) {
		if (status == null) {
			return Pages.adapt(airTransportOrderRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(airTransportOrderRepository.findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTransportOrder> listAirTransportOrders(Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(airTransportOrderRepository
					.findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(airTransportOrderRepository.findByStatusOrderByCreationDateDesc(status,
				Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTransportOrderId")
	@Override
	public void deleteAirTransportOrder(String airTransportOrderId) {
		airTransportOrderRepository.delete(airTransportOrderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteAirTransportOrders(String userId) {
		airTransportOrderRepository.deleteByOwnerId(userId);
	}

}
