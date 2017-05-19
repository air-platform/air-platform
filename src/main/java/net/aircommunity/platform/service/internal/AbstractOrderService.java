package net.aircommunity.platform.service.internal;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.support.RetryTemplate;

import com.google.common.eventbus.EventBus;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.model.AircraftAwareOrder;
import net.aircommunity.platform.model.AircraftItem;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.PassengerItem;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AircraftItemRepository;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;

/**
 * Abstract Order service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractOrderService<T extends Order> extends AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOrderService.class);

	protected Class<T> type;

	@Resource
	private EventBus eventBus;

	@Resource
	private RetryTemplate retryTemplate;

	@Resource
	private OrderNoGenerator orderNoGenerator;

	@Resource
	private AircraftItemRepository aircraftItemRepository;

	@Resource
	private PassengerRepository passengerRepository;

	@PostConstruct
	@SuppressWarnings("unchecked")
	private void init() {
		ParameterizedType pt = ParameterizedType.class.cast(getClass().getGenericSuperclass());
		type = (Class<T>) pt.getActualTypeArguments()[0];
	}

	protected Code orderNotFoundCode() {
		return Codes.ORDER_NOT_FOUND;
	}

	protected abstract BaseOrderRepository<T> getOrderRepository();

	// *********************
	// Generic CRUD shared
	// *********************

	protected T doCreateOrder(String userId, T order) {
		return doCreateOrder(userId, order, Order.Status.PENDING);
	}

	protected T doCreateOrder(String userId, T order, Order.Status status) {
		User owner = findAccount(userId, User.class);
		T newOrder = null;
		try {
			newOrder = type.newInstance();
		}
		catch (Exception unexpected) {
			LOG.error(String.format("Failed to create instance %s for user %s, cause: %s", type, userId,
					unexpected.getMessage()), unexpected);
			throw new AirException(Codes.INTERNAL_ERROR, M.msg(M.INTERNAL_SERVER_ERROR));
		}
		newOrder.setCreationDate(new Date());
		newOrder.setStatus(status);
		newOrder.setCommented(false);
		newOrder.setOrderNo(nextOrderNo());
		copyProperties(order, newOrder);
		// set vendor
		newOrder.setOwner(owner);
		try {
			T orderSaved = getOrderRepository().save(newOrder);
			eventBus.post(new OrderEvent(OrderEvent.EventType.CREATION, orderSaved));
			return orderSaved;
		}
		catch (Exception e) {
			LOG.error(String.format("Create %s: %s for user %s failed, cause: %s", type.getSimpleName(), order, userId,
					e.getMessage()), e);
			throw new AirException(Codes.SERVICE_UNAVAILABLE, M.msg(M.SERVICE_UNAVAILABLE));
		}
	}

	private String nextOrderNo() {
		return retryTemplate.execute(context -> {
			return orderNoGenerator.next();
		});
	}

	protected void copyPropertiesAircraftAware(AircraftAwareOrder src, AircraftAwareOrder tgt) {
		AircraftItem aircraftItem = src.getAircraftItem();
		AircraftItem found = aircraftItemRepository.findOne(aircraftItem.getId());
		if (found == null) {
			LOG.error("Aircraftitem {} is not found", aircraftItem);
			throw new AirException(Codes.AIRCRAFT_ITEM_NOT_FOUND, M.msg(M.AIRCRAFT_ITEM_NOT_FOUND));
		}
		tgt.setAircraftItem(found);
	}

	/**
	 * @param passengers the input passengers
	 * @return update passenger entities
	 */
	protected Set<PassengerItem> applyPassengers(Set<PassengerItem> passengers) {
		if (passengers == null || passengers.isEmpty()) {
			return Collections.emptySet();
		}
		for (PassengerItem item : passengers) {
			Passenger passenger = item.getPassenger();
			if (passenger == null) {
				throw new AirException(Codes.PASSENGER_REQUIRED, M.msg(M.PASSENGER_INFO_REQUIRED));
			}
			Passenger found = passengerRepository.findOne(passenger.getId());
			if (found == null) {
				LOG.error("Passenger {} is not found", passenger);
				throw new AirException(Codes.PASSENGER_NOT_FOUND, M.msg(M.PASSENGER_NOT_FOUND));
			}
			item.setPassenger(found);
		}
		return passengers;
	}

	protected T doFindOrder0(String orderId) {
		return getOrderRepository().findOne(orderId);
	}

	protected T doFindOrder(String orderId) {
		T order = doFindOrder0(orderId);
		// XXX CANNOT load all passengers for tour, taxi , trans
		// T order = getOrderRepository().findOne(orderId);
		if (order == null || order.getStatus() == Order.Status.DELETED) {
			LOG.error("{}: {} is not found", type.getSimpleName(), orderId);
			throw new AirException(orderNotFoundCode(), M.msg(M.ORDER_NOT_FOUND, orderId));
		}
		return order;
	}

	protected T doFindOrderByOrderNo(String orderNo) {
		T order = getOrderRepository().findByOrderNo(orderNo);
		if (order == null || order.getStatus() == Order.Status.DELETED) {
			LOG.error("{}: NO. {} is not found", type.getSimpleName(), orderNo);
			throw new AirException(orderNotFoundCode(), M.msg(M.ORDER_NOT_FOUND, orderNo));
		}
		return order;
	}

	// XXX
	protected T doUpdateOrder(String orderId, T newOrder) {
		T order = doFindOrder(orderId); // FIXME findOrder is NOT cached?
		copyProperties(newOrder, order);
		try {
			return getOrderRepository().save(order);
		}
		catch (Exception e) {
			LOG.error(String.format("Update %s: %s with %s failed, cause: %s", type.getSimpleName(), orderId, newOrder,
					e.getMessage()), e);
			throw new AirException(Codes.INTERNAL_ERROR, M.msg(M.INTERNAL_SERVER_ERROR));
		}
	}

	protected void copyProperties(T src, T tgt) {
	}

	protected T doUpdateOrderStatus(String orderId, Order.Status status) {
		T order = doFindOrder(orderId);
		switch (status) {
		case PUBLISHED:
			throwInvalidOrderStatus(orderId, status);

		case PENDING:
			throwInvalidOrderStatus(orderId, status);

		case PAID:
			if (order.getStatus() != Order.Status.PENDING) {
				throwInvalidOrderStatus(orderId, status);
			}
			order.setPaymentDate(new Date());
			break;

		case FINISHED:
			if (order.getStatus() != Order.Status.PAID) {
				throwInvalidOrderStatus(orderId, status);
			}
			order.setFinishedDate(new Date());
			break;

		case CANCELLED:
			if (order.getStatus() != Order.Status.PENDING) {
				throwInvalidOrderStatus(orderId, status);
			}
			order.setCancelledDate(new Date());
			break;

		case DELETED:
			if (order.getStatus() == Order.Status.PAID) {
				throwInvalidOrderStatus(orderId, status);
			}
			order.setDeletedDate(new Date());
			break;

		default:// noop
		}
		order.setStatus(status);
		T orderUpdated = getOrderRepository().save(order);
		if (orderUpdated.getStatus() == Order.Status.CANCELLED) {
			eventBus.post(new OrderEvent(OrderEvent.EventType.CANCELLATION, orderUpdated));
			LOG.info("Notify client managers on order cancellation: {}", orderUpdated);
		}
		return orderUpdated;
	}

	private void throwInvalidOrderStatus(String orderId, Order.Status status) {
		LOG.error("{} ({}) order cannot be update to status {}", type.getSimpleName(), orderId, status);
		throw new AirException(Codes.ORDER_ILLEGAL_STATUS, M.msg(M.ORDER_ILLEGAL_STATUS));
		// throw new AirException(Codes.ORDER_ILLEGAL_STATUS,
		// String.format("%s (%s) order cannot be update to status %s", type.getSimpleName(), orderId, status));
	}

	/**
	 * For USER (Exclude orders in DELETED status)
	 */
	protected Page<T> doListUserOrders(String userId, Order.Status status, int page, int pageSize) {
		if (Order.Status.DELETED == status) {
			return Page.emptyPage(page, pageSize);
		}
		if (status == null) {
			return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusNotOrderByCreationDateDesc(userId,
					Order.Status.DELETED, Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN for a user (orders in any status)
	 */
	protected Page<T> doListAllUserOrders(String userId, Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(getOrderRepository().findByOwnerIdOrderByCreationDateDesc(userId,
					Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN (orders in any status)
	 */
	protected Page<T> doListAllOrders(Order.Status status, int page, int pageSize) {
		if (status == null) {
			return Pages.adapt(
					getOrderRepository().findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		return Pages.adapt(getOrderRepository().findByStatusOrderByCreationDateDesc(status,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For ADMIN
	 */
	protected void doDeleteOrder(String orderId) {
		getOrderRepository().delete(orderId);
	}

	/**
	 * For ADMIN
	 */
	protected void doDeleteOrders(String userId) {
		getOrderRepository().deleteByOwnerId(userId);
	}
}
