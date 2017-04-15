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
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.AirTaxiOrder;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.repository.AirTaxiOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.service.AirTaxiOrderService;
import net.aircommunity.platform.service.AirTaxiService;

/**
 * AirTaxiOrder service implementation.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTaxiOrderServiceImpl extends AbstractServiceSupport implements AirTaxiOrderService {
	private static final String CACHE_NAME = "cache.airtaxiorder";

	@Resource
	private AirTaxiOrderRepository airTaxiOrderRepository;

	@Resource
	private PassengerRepository passengerRepository;

	@Resource
	private AirTaxiService airTaxiService;

	@Override
	public AirTaxiOrder createAirTaxiOrder(String userId, AirTaxiOrder AirTaxiOrder) {
		User owner = findAccount(userId, User.class);
		AirTaxiOrder newAirTaxiOrder = new AirTaxiOrder();
		newAirTaxiOrder.setCreationDate(new Date());
		newAirTaxiOrder.setStatus(Status.PENDING);
		copyProperties(AirTaxiOrder, newAirTaxiOrder);
		// set vendor
		newAirTaxiOrder.setOwner(owner);
		return airTaxiOrderRepository.save(newAirTaxiOrder);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTaxiOrder findAirTaxiOrder(String airTaxiOrderId) {
		AirTaxiOrder airTaxiOrder = airTaxiOrderRepository.findOne(airTaxiOrderId);
		if (airTaxiOrder == null) {
			throw new AirException(Codes.TAXI_ORDER_NOT_FOUND,
					String.format("AirTaxiOrder %s is not found", airTaxiOrderId));
		}
		return airTaxiOrder;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTaxiOrderId")
	@Override
	public AirTaxiOrder updateAirTaxiOrder(String airTaxiOrderId, AirTaxiOrder newAirTaxiOrder) {
		AirTaxiOrder airTaxiOrder = findAirTaxiOrder(airTaxiOrderId);
		copyProperties(newAirTaxiOrder, airTaxiOrder);
		return airTaxiOrderRepository.save(airTaxiOrder);
	}

	private void copyProperties(AirTaxiOrder src, AirTaxiOrder tgt) {
		AirTaxi airTaxi = airTaxiService.findAirTaxi(src.getAirTaxi().getId());
		tgt.setAirTaxi(airTaxi);
		tgt.setNote(src.getNote());
		tgt.setDate(src.getDate());
		tgt.setTimeSlot(src.getTimeSlot());
		Set<Passenger> passengers = src.getPassengers();
		if (passengers != null) {
			// TODO better use service and throw NOT FOUND
			passengers = passengers.stream().map(passenger -> passengerRepository.findOne(passenger.getId()))
					.collect(Collectors.toSet());
			tgt.setPassengers(passengers);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airTaxiOrderId")
	@Override
	public AirTaxiOrder updateAirTaxiOrderStatus(String airTaxiOrderId, Status status) {
		AirTaxiOrder airTaxiOrder = findAirTaxiOrder(airTaxiOrderId);
		airTaxiOrder.setStatus(status);
		return airTaxiOrderRepository.save(airTaxiOrder);
	}

	@Override
	public Page<AirTaxiOrder> listUserAirTaxiOrders(String userId, Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(airTaxiOrderRepository.findByOwnerIdOrderByCreationDateDesc(userId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(airTaxiOrderRepository.findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTaxiOrder> listTenantAirTaxiOrders(String tenantId, Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(airTaxiOrderRepository.findByVendorIdOrderByCreationDateDesc(tenantId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(airTaxiOrderRepository.findByVendorIdAndStatusOrderByCreationDateDesc(tenantId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	@Override
	public Page<AirTaxiOrder> listAirTaxiOrders(Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(
					airTaxiOrderRepository.findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(airTaxiOrderRepository.findByStatusOrderByCreationDateDesc(status,
				Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airTaxiOrderId")
	@Override
	public void deleteAirTaxiOrder(String airTaxiOrderId) {
		airTaxiOrderRepository.delete(airTaxiOrderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteAirTaxiOrders(String userId) {
		airTaxiOrderRepository.deleteByOwnerId(userId);
	}

}
