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
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.AirTaxiOrder;
import net.aircommunity.platform.model.Order.Status;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.repository.AirTaxiOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.repository.VendorAwareOrderRepository;
import net.aircommunity.platform.service.AirTaxiOrderService;
import net.aircommunity.platform.service.AirTaxiService;

/**
 * AirTaxiOrder service implementation.
 *
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirTaxiOrderServiceImpl extends AbstractVendorAwareOrderService<AirTaxiOrder>
		implements AirTaxiOrderService {
	private static final String CACHE_NAME = "cache.airtaxi-order";

	@Resource
	private AirTaxiOrderRepository airTaxiOrderRepository;

	@Resource
	private PassengerRepository passengerRepository;

	@Resource
	private AirTaxiService airTaxiService;

	@Override
	public AirTaxiOrder createAirTaxiOrder(String userId, AirTaxiOrder order) {
		return createOrder(userId, order);

	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirTaxiOrder findAirTaxiOrder(String orderId) {
		return findOrder(orderId);

	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public AirTaxiOrder updateAirTaxiOrder(String orderId, AirTaxiOrder newOrder) {
		return updateOrder(orderId, newOrder);
	}

	@Override
	protected void copyProperties(AirTaxiOrder src, AirTaxiOrder tgt) {
		tgt.setNote(src.getNote());
		tgt.setDate(src.getDate());
		tgt.setTimeSlot(src.getTimeSlot());
		Set<Passenger> passengers = src.getPassengers();
		//
		AirTaxi airTaxi = airTaxiService.findAirTaxi(src.getAirTaxi().getId());
		tgt.setAirTaxi(airTaxi);
		if (passengers != null) {
			// TODO better use service and throw NOT FOUND
			passengers = passengers.stream().map(passenger -> passengerRepository.findOne(passenger.getId()))
					.collect(Collectors.toSet());
			tgt.setPassengers(passengers);
		}
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public AirTaxiOrder updateAirTaxiOrderStatus(String orderId, Status status) {
		return updateOrderStatus(orderId, status);
	}

	@Override
	public Page<AirTaxiOrder> listUserAirTaxiOrders(String userId, Status status, int page, int pageSize) {
		return listUserOrders(userId, status, page, pageSize);
	}

	@Override
	public Page<AirTaxiOrder> listTenantAirTaxiOrders(String tenantId, Status status, int page, int pageSize) {
		return listTenantOrders(tenantId, status, page, pageSize);
	}

	@Override
	public Page<AirTaxiOrder> listAirTaxiOrders(Status status, int page, int pageSize) {
		return listAllOrders(status, page, pageSize);
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#orderId")
	@Override
	public void deleteAirTaxiOrder(String orderId) {
		deleteOrder(orderId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirTaxiOrders(String userId) {
		deleteOrders(userId);
	}

	@Override
	protected VendorAwareOrderRepository<AirTaxiOrder> getOrderRepository() {
		return airTaxiOrderRepository;
	}

	@Override
	protected Code orderNotFoundCode() {
		return Codes.TAXI_ORDER_NOT_FOUND;
	}

}
