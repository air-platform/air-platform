package net.aircommunity.platform.service.internal;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.AirTourOrder;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.repository.AirTourOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.AirTourOrderService;
import net.aircommunity.platform.service.AirTourService;

/**
 * AirTourOrder service implementation.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTourOrderServiceImpl extends AbstractVendorAwareOrderService<AirTourOrder>
		implements AirTourOrderService {
	private static final String CACHE_NAME = "cache.airtour-order";

	@Resource
	private AirTourOrderRepository airTourOrderRepository;

	@Resource
	private PassengerRepository passengerRepository;

	@Resource
	private AirTourService airTourService;

	@Override
	public AirTourOrder createAirTourOrder(String userId, AirTourOrder order) {
		return createOrder(userId, order);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTourOrder findAirTourOrder(String orderId) {
		return findOrder(orderId);
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public AirTourOrder updateAirTourOrder(String orderId, AirTourOrder newOrder) {
		return updateOrder(orderId, newOrder);
	}

	@Override
	protected void copyProperties(AirTourOrder src, AirTourOrder tgt) {
		AirTour airTour = airTourService.findAirTour(src.getAirTour().getId());
		tgt.setAirTour(airTour);
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

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public AirTourOrder updateAirTourOrderStatus(String orderId, Status status) {
		return updateOrderStatus(orderId, status);
	}

	@Override
	public Page<AirTourOrder> listUserAirTourOrders(String userId, Status status, int page, int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<AirTourOrder> listTenantAirTourOrders(String tenantId, Status status, int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	@Override
	public Page<AirTourOrder> listAirTourOrders(Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteAirTourOrder(String orderId) {
		deleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteAirTourOrders(String userId) {
		deleteOrders(userId);
	}

	@Override
	protected VendorAwareOrderRepository<AirTourOrder> getOrderRepository() {
		return airTourOrderRepository;
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.TOUR_ORDER_NOT_FOUND;
	}

}
