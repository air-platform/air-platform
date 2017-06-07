package net.aircommunity.platform.service.internal;

import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.support.RetryTemplate;

import com.google.common.eventbus.EventBus;

import io.micro.common.DateFormats;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.AircraftAwareOrder;
import net.aircommunity.platform.model.CharterableOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Type;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.PassengerItem;
import net.aircommunity.platform.model.PricedProduct;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.SalesPackage;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.repository.SalesPackageRepository;
import net.aircommunity.platform.service.CommonProductService;

/**
 * Abstract Order service support. TODO order status transition
 * 
 * @author Bin.Zhang
 */
abstract class AbstractOrderService<T extends Order> extends AbstractServiceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractOrderService.class);

	private static final SimpleDateFormat DEPARTURE_DATE_FORMATTER = DateFormats.simple("yyyy-MM-dd");

	protected Class<T> type;

	@Resource
	private Configuration configuration;

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

	@Resource
	private CommonProductService commonProductService;

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
		return doCreateOrder(userId, order, Order.Status.CREATED);
	}

	protected T doCreateOrder(String userId, T order, Order.Status status) {
		User owner = findAccount(userId, User.class);
		if (owner.getStatus() == Account.Status.LOCKED) {
			LOG.warn("Account {} is locked, cannot be place orders", owner);
			throw new AirException(Codes.ACCOUNT_PERMISSION_DENIED, M.msg(M.ACCOUNT_PERMISSION_DENIED_LOCKED));
		}

		T newOrder = null;
		try {
			newOrder = type.newInstance();
		}
		catch (Exception unexpected) {
			LOG.error(String.format("Failed to create instance %s for user %s, cause: %s", type, userId,
					unexpected.getMessage()), unexpected);
			throw newInternalException();
		}
		// retrieve product related to this order
		String productId = order.getProduct().getId();
		Product product = commonProductService.findProduct(productId);
		newOrder.setProduct(product);
		newOrder.setOwner(owner);

		// set base properties
		newOrder.setOrderNo(nextOrderNo());
		newOrder.setStatus(status);
		newOrder.setCreationDate(new Date());
		newOrder.setLastModifiedDate(newOrder.getCreationDate());
		newOrder.setCommented(false);

		// copy common properties
		copyCommonProperties(order, newOrder);
		// copy order specific properties
		copyProperties(order, newOrder);
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

	protected void copyProperties(T src, T tgt) {
	}

	private void copyCommonProperties(Order src, Order tgt) {
		tgt.setQuantity(src.getQuantity());
		tgt.setNote(src.getNote());
		tgt.setContact(src.getContact());
		// TODO newOrder.setPaymentInfo
		// TODO newOrder.setConfirmation (reject reason)

		// XXX NOTE: price is not set for CharterOrder (AirJet), only price/per hour
		// need to set manually after customer service calculated, so total price would be 0 for CharterOrder
		double unitPrice = 0;
		// priced
		if (PricedProduct.class.isAssignableFrom(src.getProduct().getClass())) {
			PricedProduct pricedProduct = PricedProduct.class.cast(src.getProduct());
			unitPrice = pricedProduct.getPrice();
		}
		// charterable (FerryFlightOrder only ATM)
		if (CharterableOrder.class.isAssignableFrom(tgt.getClass())) {
			CharterableOrder newCharterableOrder = CharterableOrder.class.cast(tgt);
			CharterableOrder charterableOrder = CharterableOrder.class.cast(src);
			newCharterableOrder.setChartered(charterableOrder.isChartered());
		}
		// sales package (AircraftAwareOrder)
		if (AircraftAwareOrder.class.isAssignableFrom(tgt.getClass())) {
			AircraftAwareOrder newAircraftAwareOrder = AircraftAwareOrder.class.cast(tgt);
			AircraftAwareOrder aircraftAwareOrder = AircraftAwareOrder.class.cast(src);
			copyPropertiesAircraftAware(aircraftAwareOrder, newAircraftAwareOrder);
			unitPrice = newAircraftAwareOrder.getSalesPackagePrice();
		}
		double totalPrice = Math.floor(unitPrice * src.getQuantity());
		tgt.setTotalPrice(totalPrice);
	}

	private void copyPropertiesAircraftAware(AircraftAwareOrder src, AircraftAwareOrder tgt) {
		SalesPackage salesPackage = salesPackageRepository.findOne(src.getSalesPackage().getId());
		if (salesPackage == null) {
			LOG.error("SalesPackage ID: {} is not found", src.getSalesPackage().getId());
			throw new AirException(Codes.SALESPACKAGE_NOT_FOUND, M.msg(M.SALESPACKAGE_NOT_FOUND));
		}

		// caculate price based on DepartureDate of the order
		Date date = src.getDepartureDate();
		LocalDate departureDate = date.toInstant().atZone(ZoneId.of(configuration.getTimeZone())).toLocalDate();
		int days = (int) ChronoUnit.DAYS.between(LocalDate.now(), departureDate);
		if (!SalesPackage.DAYS_RANGE.contains(days)) {
			throw new AirException(Codes.PRODUCT_INVALID_DEPARTURE_DATE,
					M.msg(M.PRODUCT_INVALID_DEPARTURE_DATE, DEPARTURE_DATE_FORMATTER.format(date)));
		}
		double salesPackagePrice = salesPackage.getPrice(days);
		tgt.setSalesPackagePrice(salesPackagePrice);
		tgt.setSalesPackage(salesPackage);
		// set properties
		tgt.setTimeSlot(src.getTimeSlot());
		tgt.setDepartureDate(src.getDepartureDate());
		Set<PassengerItem> passengers = src.getPassengers();
		if (passengers != null) {
			tgt.setPassengers(applyPassengers(passengers));
		}
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
		copyCommonProperties(newOrder, order);
		copyProperties(newOrder, order);
		order.setLastModifiedDate(new Date());
		return safeExecute(() -> getOrderRepository().save(order), "Update %s: %s with %s failed", type.getSimpleName(),
				orderId, newOrder);
	}

	/**
	 * Update total price of an order
	 */
	protected T doUpdateOrderPrice(String orderId, double newPrice) {
		T order = doFindOrder(orderId);
		order.setLastModifiedDate(new Date());
		order.setTotalPrice(newPrice);
		return safeExecute(() -> getOrderRepository().save(order), "Update %s price: %s to %d failed",
				type.getSimpleName(), orderId, newPrice);
	}

	// TODO REMOVE or refactor ?
	// BiConsumer<Order, Order.Status> fleetOrderStatusHanlder = (order, newStatus) -> {
	// switch (newStatus) {
	// case PUBLISHED:
	// case CREATED:
	// throw invalidOrderStatus(orderId, newStatus);
	//
	// // AIRJET ONLY
	// case CONFIRMED:
	// if (order.getType() != Type.FLEET) {
	// throw invalidOrderStatus(orderId, newStatus);
	// }
	// break;
	//
	// // AIRJET ONLY
	// case CONTRACT_SIGNED:
	// if (order.getType() != Type.FLEET) {
	// throw invalidOrderStatus(orderId, newStatus);
	// }
	// break;
	// }
	// };

	/**
	 * Update order status to new status (TODO update )
	 */
	protected T doUpdateOrderStatus(String orderId, Order.Status newStatus) {
		T order = doFindOrder(orderId);
		switch (newStatus) {
		case PUBLISHED:
		case CREATED:
			throw invalidOrderStatus(orderId, newStatus);

			// airjet ONLY
		case CONFIRMED:
			if (order.getType() != Type.FLEET) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			break;

		// airjet & course ONLY
		case CONTRACT_SIGNED:
			// should be already confirmed
			if (!(order.getStatus() == Order.Status.CONTRACT_SIGNED && order.signContractRequired())) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			break;

		//
		case PAID:
			// should be already contract signed
			if (!(order.getStatus() == Order.Status.CONTRACT_SIGNED && order.signContractRequired())) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			if (order.getStatus() != Order.Status.CREATED) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			order.setPaymentDate(new Date());
			break;

		//
		case REFUNDING:
			// should be already paid
			if (order.getStatus() != Order.Status.PAID) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			break;

		//
		case REFUNDED:
			// should be already contract signed
			if (order.getStatus() != Order.Status.REFUNDING) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			order.setRefundedDate(new Date());
			break;

		case TICKET_RELEASED:
			if (order.getStatus() != Order.Status.PAID) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			break;

		case FINISHED:
			if (order.getStatus() != Order.Status.PAID || order.getStatus() != Order.Status.TICKET_RELEASED) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			order.setFinishedDate(new Date());
			break;

		case CANCELLED:
			if (!order.isCancellable()) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			order.setCancelledDate(new Date());
			break;

		case CLOSED:
			if (!order.isCancellable()) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			order.setClosedDate(new Date());
			break;

		case DELETED:
			if (!order.isCompleted()) {
				throw invalidOrderStatus(orderId, newStatus);
			}
			order.setDeletedDate(new Date());
			break;

		default:// noop
		}
		order.setStatus(newStatus);
		order.setLastModifiedDate(new Date());
		return safeExecute(() -> {
			T orderUpdated = getOrderRepository().save(order);
			if (orderUpdated.getStatus() == Order.Status.CANCELLED) {
				eventBus.post(new OrderEvent(OrderEvent.EventType.CANCELLATION, orderUpdated));
				LOG.info("Notify client managers on order cancellation: {}", orderUpdated);
			}
			return orderUpdated;
		}, "Update %s: %s to status %s failed", type.getSimpleName(), orderId, newStatus);
	}

	private AirException invalidOrderStatus(String orderId, Order.Status status) {
		LOG.error("{} ({}) order cannot be update to status {}", type.getSimpleName(), orderId, status);
		return new AirException(Codes.ORDER_ILLEGAL_STATUS, M.msg(M.ORDER_ILLEGAL_STATUS));
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
