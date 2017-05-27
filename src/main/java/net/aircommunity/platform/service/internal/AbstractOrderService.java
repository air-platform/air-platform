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
import net.aircommunity.platform.model.CharterableOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.PassengerItem;
import net.aircommunity.platform.model.SalesPackage;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.repository.SalesPackageRepository;

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
	private SalesPackageRepository salesPackageRepository;

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
			throw newInternalException();
		}
		newOrder.setCreationDate(new Date());
		newOrder.setStatus(status);
		newOrder.setCommented(false);
		newOrder.setOrderNo(nextOrderNo());
		if (CharterableOrder.class.isAssignableFrom(newOrder.getClass())) {
			CharterableOrder newCharterableOrder = CharterableOrder.class.cast(newOrder);
			CharterableOrder charterableOrder = CharterableOrder.class.cast(order);
			newCharterableOrder.setChartered(charterableOrder.isChartered());
		}
		copyProperties(order, newOrder);
		// set vendor
		newOrder.setOwner(owner);
		final T orderToSave = newOrder;
		return safeExecute(() -> {
			T orderSaved = getOrderRepository().save(orderToSave);
			eventBus.post(new OrderEvent(OrderEvent.EventType.CREATION, orderSaved));
			return orderSaved;
		}, "Create %s: %s for user %s failed", type.getSimpleName(), order, userId);
	}

	private String nextOrderNo() {
		return retryTemplate.execute(context -> {
			return orderNoGenerator.next();
		});
	}

	protected void copyPropertiesAircraftAware(AircraftAwareOrder src, AircraftAwareOrder tgt) {
		SalesPackage salesPackage = src.getSalesPackage();
		SalesPackage found = salesPackageRepository.findOne(salesPackage.getId());
		if (found == null) {
			LOG.error("SalesPackage {} is not found", salesPackage);
			throw new AirException(Codes.SALESPACKAGE_NOT_FOUND, M.msg(M.SALESPACKAGE_NOT_FOUND));
		}
		tgt.setSalesPackage(salesPackage);
		tgt.setSalesPackageNum(src.getSalesPackageNum());
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

	protected T doUpdateOrder(String orderId, T newOrder) {
		T order = doFindOrder(orderId); // FIXME findOrder is NOT cached?
		copyProperties(newOrder, order);
		return safeExecute(() -> getOrderRepository().save(order), "Update %s: %s with %s failed", type.getSimpleName(),
				orderId, newOrder);
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
		return safeExecute(() -> {
			T orderUpdated = getOrderRepository().save(order);
			if (orderUpdated.getStatus() == Order.Status.CANCELLED) {
				eventBus.post(new OrderEvent(OrderEvent.EventType.CANCELLATION, orderUpdated));
				LOG.info("Notify client managers on order cancellation: {}", orderUpdated);
			}
			return orderUpdated;
		}, "Update %s: %s to status %s failed", type.getSimpleName(), orderId, status);
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
		safeExecute(() -> getOrderRepository().delete(orderId), "Hard delete order %s failed", orderId);
	}

	/**
	 * For ADMIN
	 */
	protected void doDeleteOrders(String userId) {
		safeExecute(() -> getOrderRepository().deleteByOwnerId(userId), "Hard delete all orders for user %s failed",
				userId);
	}
}
