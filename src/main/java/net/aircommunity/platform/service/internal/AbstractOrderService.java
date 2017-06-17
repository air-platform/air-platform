package net.aircommunity.platform.service.internal;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
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
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.AircraftAwareOrder;
import net.aircommunity.platform.model.CharterableOrder;
import net.aircommunity.platform.model.Instalment;
import net.aircommunity.platform.model.InstalmentOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Order.Type;
import net.aircommunity.platform.model.OrderEvent;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Passenger;
import net.aircommunity.platform.model.PassengerItem;
import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PointsExchange;
import net.aircommunity.platform.model.PricePolicy;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Refund;
import net.aircommunity.platform.model.RefundRequest;
import net.aircommunity.platform.model.RefundResponse;
import net.aircommunity.platform.model.SalesPackage;
import net.aircommunity.platform.model.StandardOrder;
import net.aircommunity.platform.model.UnitProductPrice;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.PassengerRepository;
import net.aircommunity.platform.repository.SalesPackageRepository;
import net.aircommunity.platform.service.AccountService;
import net.aircommunity.platform.service.CommonProductService;
import net.aircommunity.platform.service.MemberPointsService;
import net.aircommunity.platform.service.PaymentService;

/**
 * Abstract Order service support.
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

	@Resource
	private MemberPointsService memberPointsService;

	@Resource
	private PaymentService paymentService;

	@Resource
	private AccountService accountService;

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
		// fleet product is set in FleetCandidate, ignored
		if (order.getType() != Type.FLEET) {
			Product product = order.getProduct();
			if (product == null) {
				LOG.error("Failed to create order {} for user {}, cause: product is not set", order, userId);
				throw newInternalException();
			}
			product = commonProductService.findProduct(product.getId());
			LOG.debug("Product: {}", product);
			order.setProduct(product);
			newOrder.setProduct(product);
		}
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

		// Calculate points to exchange
		long pointToExchange = order.getPointsUsed();
		if (pointToExchange > 0 && pointToExchange > owner.getPoints()) {
			pointToExchange = owner.getPoints();
		}
		if (pointToExchange > 0) {
			PointsExchange pointsUsed = memberPointsService.exchangePoints(pointToExchange);
			newOrder.setPointsUsed(pointsUsed.getPointsExchanged());
			// update total price
			newOrder.setTotalPrice(newOrder.getTotalPrice().subtract(BigDecimal.valueOf(pointsUsed.getMoney())));
			LOG.info("User[{}] {} used points {} for order {}", owner.getId(), owner.getNickName(), pointsUsed,
					newOrder);
		}
		LOG.info("Creating order {} for user[{}] {}", newOrder, owner.getId(), owner.getNickName());
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

		// Set order price
		// --------------------------------------------------------------------------------
		// Three types of products regarding pricing:
		// Product (has no price defined)
		// 1) StandardProduct (full price): totalPrice= price * quantity
		// 2) CharterableProduct (seat price & full price) - special type of PricedProduct
		// 2.1) chartered(true): totalPrice = price
		// 2.2) chartered(false): totalPrice = seatPrice *
		// 3) SalesPackageProduct (SalesPackages)
		// --------------------------------------------------------------------------------
		// NOTE: price is not set for CharterOrder (AirJet), only price/per hour
		// need to set manually after customer service calculated,
		// so total price would be 0 for CharterOrder

		// -----------------------
		// Charterable
		// -----------------------
		// (FerryFlight) CharterableOrder => VendorAwareOrder => StandardOrder
		if (CharterableOrder.class.isAssignableFrom(tgt.getClass())) {
			CharterableOrder newCharterableOrder = (CharterableOrder) tgt;
			CharterableOrder charterableOrder = (CharterableOrder) src;
			newCharterableOrder.setChartered(charterableOrder.isChartered());
		}
		// -----------------------
		// SalesPackage
		// -----------------------
		// (Taxi, Tour, Trans) AircraftAwareOrder => VendorAwareOrder => StandardOrder
		if (AircraftAwareOrder.class.isAssignableFrom(tgt.getClass())) {
			AircraftAwareOrder newAircraftAwareOrder = (AircraftAwareOrder) tgt;
			AircraftAwareOrder aircraftAwareOrder = (AircraftAwareOrder) src;
			copyPropertiesAircraftAware(aircraftAwareOrder, newAircraftAwareOrder);
		}
		// -----------------------
		// Standard
		// -----------------------
		// 1) (Fleet) StandardOrder (XXX No price is available, need provide manually or calculated via external system)
		// 2) (JetTravel, Course) VendorAwareOrder => StandardOrder

		// unit product price
		BigDecimal unitPrice = BigDecimal.ZERO;
		UnitProductPrice unitProductPrice = tgt.getUnitProductPrice();
		LOG.debug("unitProductPrice: {}", unitProductPrice);
		if (unitProductPrice.getPricePolicy() == PricePolicy.PER_HOUR) {
			// TODO calculated via external system if possible, just make it zero for now
		}
		else {
			unitPrice = unitProductPrice.getUnitPrice();
		}
		LOG.debug("final unitPrice: {}, quantity: {}", unitPrice, tgt.getQuantity());
		tgt.setTotalPrice(OrderPrices.normalizePrice(unitPrice.multiply(BigDecimal.valueOf(tgt.getQuantity()))));
		LOG.debug("final totalPrice: {}", tgt.getTotalPrice());
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
		tgt.setSalesPackagePrice(salesPackage.getPrice(days));
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

	protected Optional<T> doLookupOrderByOrderNo(String orderNo) {
		T order = getOrderRepository().findByOrderNo(orderNo);
		if (order == null || order.getStatus() == Order.Status.DELETED) {
			LOG.error("{}: NO. {} is not found or already soft deleted", type.getSimpleName(), orderNo);
			return Optional.empty();
		}
		return Optional.of(order);
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
		order.setTotalPrice(BigDecimal.valueOf(newPrice));
		return safeExecute(() -> getOrderRepository().save(order), "Update %s price: %s to %d failed",
				type.getSimpleName(), orderId, newPrice);
	}

	/**
	 * Payment request
	 */
	protected PaymentRequest doRequestOrderPayment(String orderId, Payment.Method paymentMethod) {
		T order = doFindOrder(orderId);
		return paymentService.createPaymentRequest(paymentMethod, order);
	}

	/**
	 * Pay order
	 */
	protected T doPayOrder(String orderId, Payment payment) {
		return doPayOrder(orderId, null, payment);
	}

	/**
	 * Pay order
	 */
	protected T doPayOrder(String orderId, String instalmentId, Payment payment) {
		T order = doFindOrder(orderId);
		Payment newPayment = new Payment();
		newPayment.setAmount(payment.getAmount());
		newPayment.setMethod(payment.getMethod());
		newPayment.setOrderNo(payment.getOrderNo());
		newPayment.setTimestamp(payment.getTimestamp());
		newPayment.setTradeNo(payment.getTradeNo());
		newPayment.setOwner(order.getOwner());
		newPayment.setVendor(order.getProduct().getVendor());

		T orderPaid = null;
		// standard order
		if (StandardOrder.class.isAssignableFrom(order.getClass())) {
			StandardOrder standardOrder = StandardOrder.class.cast(order);
			standardOrder.setPayment(newPayment);
			orderPaid = doUpdateOrderStatus(order, Order.Status.PAID);
		}

		// TODO CHECK if works (NOT USED ATM)
		// installment order
		if (InstalmentOrder.class.isAssignableFrom(order.getClass())) {
			InstalmentOrder instalmentOrder = InstalmentOrder.class.cast(order);
			// XXX NOTE: we can also check the Instalment payment in order of stage sequence if needed here
			// e.g. stage1 should be paid before stage2
			// Instalment instalment = instalmentRepository.findOne(instalmentId);
			Optional<Instalment> instalment = instalmentOrder.findInstalment(instalmentId);
			if (!instalment.isPresent()) {
				LOG.error("Order {} instalment {} not found", orderId, instalmentId);
				throw new AirException(Codes.ORDER_INSTALMENT_NOT_FOUND, M.msg(M.ORDER_INSTALMENT_NOT_FOUND));
			}
			instalment.get().setPayment(newPayment);
			if (instalmentOrder.isPaidFully()) {
				orderPaid = doUpdateOrderStatus(order, Order.Status.PAID);
			}
			else {
				orderPaid = doUpdateOrderStatus(order, Order.Status.PARTIAL_PAID);
			}
		}
		eventBus.post(new OrderEvent(OrderEvent.EventType.PAYMENT, orderPaid));
		return orderPaid;
	}

	/**
	 * Request refund
	 */
	protected T doRequestOrderRefund(String orderId, RefundRequest request) {
		T order = doFindOrder(orderId);
		order.setRefundReason(request.getRefundReason());
		return doUpdateOrderStatus(order, Order.Status.REFUND_REQUESTED);
	}

	/**
	 * Reject refund
	 */
	protected T doRejectOrderRefund(String orderId, String rejectReason) {
		// rollback status to PAID
		T order = doFindOrder(orderId);
		order.setRefundFailureCause(M.msg(M.REFUND_REQUEST_REJECTED, rejectReason));
		return doUpdateOrderStatus(order, Order.Status.PAID);
	}

	/**
	 * Accept refund
	 */
	protected T doAcceptOrderRefund(String orderId, BigDecimal refundAmount) {
		// update to Order.Status.REFUNDING now
		T order = doUpdateOrderStatus(orderId, Order.Status.REFUNDING);
		T orderRefund = order;
		// standard order
		if (StandardOrder.class.isAssignableFrom(order.getClass())) {
			StandardOrder standardOrder = StandardOrder.class.cast(order);
			Payment payment = standardOrder.getPayment();
			RefundResponse refundResponse = paymentService.refundPayment(payment.getMethod(), order, refundAmount);
			if (refundResponse.isSuccess()) {
				Refund refund = refundResponse.getRefund();
				refund.setVendor(standardOrder.getProduct().getVendor());
				refund.setOwner(standardOrder.getOwner());
				standardOrder.setRefund(refund);
				standardOrder.setRefundFailureCause(null);
				orderRefund = doUpdateOrderStatus(order, Order.Status.REFUNDED);
				eventBus.post(new OrderEvent(OrderEvent.EventType.REFUNDED, orderRefund));
			}
			else {
				order.setRefundFailureCause(refundResponse.getFailureCause());
				orderRefund = doUpdateOrderStatus(order, Order.Status.REFUND_FAILED);
				eventBus.post(new OrderEvent(OrderEvent.EventType.REFUND_FAILED, orderRefund));
			}
		}
		// TODO NOT IMPLED
		// installment order
		// if (InstalmentOrder.class.isAssignableFrom(order.getClass())) {
		// InstalmentOrder instalmentOrder = InstalmentOrder.class.cast(order);
		// }
		return orderRefund;
	}

	/**
	 * Refund failure
	 */
	protected T doHandleOrderRefundFailure(String orderId, String refundFailureCause) {
		// NOTE: not from cache, in case it's updated
		T order = doFindOrder(orderId);
		order.setRefundFailureCause(refundFailureCause);
		return doUpdateOrderStatus(order, Order.Status.REFUND_FAILED);
	}

	/**
	 * Close
	 */
	protected T doCloseOrder(String orderId, String closedReason) {
		// NOTE: not from cache, in case it's updated
		T order = doFindOrder(orderId);
		order.setClosedReason(closedReason);
		return doUpdateOrderStatus(order, Order.Status.CLOSED);
	}

	/**
	 * Update order status to new status
	 */
	protected T doUpdateOrderStatusByOrderNo(String orderNo, Order.Status newStatus) {
		T order = doFindOrderByOrderNo(orderNo);
		return doUpdateOrderStatus(order, newStatus);
	}

	/**
	 * Update order status to new status
	 */
	protected T doUpdateOrderStatus(String orderId, Order.Status newStatus) {
		T order = doFindOrder(orderId);
		return doUpdateOrderStatus(order, newStatus);
	}

	/**
	 * Update order status to new status
	 */
	protected T doUpdateOrderStatus(T order, Order.Status newStatus) {
		switch (newStatus) {
		case PUBLISHED:
		case CREATED:
			// order initial status
			LOG.debug("It's order initial status, cannot update status to {}", newStatus);
			throw invalidOrderStatus(order, newStatus);

			// AirJet, AirTravel
		case CONFIRMED:
			LOG.debug("Expect order confirmationRequired");
			expectOrderStatusCondition(order.confirmationRequired());
			break;

		// AirJet, Course
		case CONTRACT_SIGNED:
			// should be already confirmed
			LOG.debug("Expect order signContractRequired");
			expectOrderStatusCondition(order.signContractRequired());
			expectOrderStatus(order, newStatus, Order.Status.CONFIRMED);
			break;

		//
		case PARTIAL_PAID:
			// TODO CHECK
			LOG.debug("TODO CHECK for {}", newStatus);
			break;

		case PAID:
			// should be already contract signed (if contract sign is required)
			if (order.signContractRequired()) {
				LOG.debug("Order signContractRequired, expect contract signed before paid");
				expectOrderStatus(order, newStatus, Order.Status.CONTRACT_SIGNED);
			}
			// otherwise, should be in CREATED | PUBLISHED (XXX require confirmation? or just paid and then confirm)
			// we allow refund request to move back to paid status in case tenant rejected the refund request
			else {
				// expectOrderStatusCondition(order.isInitialStatus());
				expectOrderStatus(order, newStatus,
						EnumSet.of(Order.Status.CREATED, Order.Status.PUBLISHED, Order.Status.REFUND_REQUESTED));
			}
			// already paid for REFUND_REQUESTED (cannot re-setPaymentDate)
			if (order.getStatus() != Order.Status.REFUND_REQUESTED) {
				order.setPaymentDate(new Date());
			}
			break;

		//
		case REFUND_REQUESTED:
			// should be already paid
			expectOrderStatus(order, newStatus, EnumSet.of(Order.Status.REFUND_FAILED, Order.Status.PAID));
			break;

		//
		case REFUNDING:
			// should be already paid (directly by TENANT) or refund requested (by USER)
			expectOrderStatus(order, newStatus,
					EnumSet.of(Order.Status.REFUND_FAILED, Order.Status.REFUND_REQUESTED, Order.Status.PAID));
			break;

		//
		case REFUNDED:
			// should be already refunding in progress
			expectOrderStatus(order, newStatus, Order.Status.REFUNDING);
			order.setRefundedDate(new Date());
			// add points back to the account
			accountService.updateUserPoints(order.getOwner().getId(), order.getPointsUsed());
			LOG.info("Refund success, add used points: {} back to account: {}", order.getPointsUsed(),
					order.getOwner());
			break;

		//
		case REFUND_FAILED:
			expectOrderStatus(order, newStatus, Order.Status.REFUNDING);
			break;

		case TICKET_RELEASED:
			expectOrderStatus(order, newStatus, Order.Status.PAID);
			break;

		case FINISHED:
			// expect paid (TICKET_RELEASED also indicates already paid)
			expectOrderStatus(order, newStatus, EnumSet.of(Order.Status.PAID, Order.Status.TICKET_RELEASED));
			order.setFinishedDate(new Date());
			// update points earned
			long pointsEarnedPercent = memberPointsService.getPointsEarnedFromRule(Constants.PointRules.ORDER_FINISHED);
			long pointsEarned = Math.round(
					order.getTotalPrice().multiply(BigDecimal.valueOf(pointsEarnedPercent / 100d)).doubleValue());
			LOG.info("User earned points {}({}%)", pointsEarned, pointsEarnedPercent);
			if (pointsEarned > 0) {
				accountService.updateUserPoints(order.getOwner().getId(), pointsEarned);
			}
			commonProductService.increaseProductSales(order.getProduct().getId());
			break;

		case CANCELLED:
			LOG.debug("Expect order isCancellable");
			expectOrderStatusCondition(order.isCancellable());
			order.setCancelledDate(new Date());
			break;

		case CLOSED:
			LOG.debug("Expect order isCloseable");
			order.setClosedDate(new Date());
			break;

		case DELETED:
			LOG.debug("Expect order isCompleted");
			expectOrderStatusCondition(order.isCompleted());
			order.setDeletedDate(new Date());
			break;

		default:// noop
			LOG.warn("Unhandled order status: {}", newStatus);
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
		}, "Update %s: %s to status %s failed", type.getSimpleName(), order.getId(), newStatus);
	}

	private void expectOrderStatusCondition(boolean expectCondition) {
		if (!expectCondition) {
			throw new AirException(Codes.ORDER_ILLEGAL_STATUS, M.msg(M.ORDER_ILLEGAL_STATUS));
		}
	}

	private void expectOrderStatus(T order, Order.Status newStatus, Order.Status expect) {
		if (order.getStatus() != expect) {
			throw invalidOrderStatus(order, newStatus);
		}
	}

	private void expectOrderStatus(T order, Order.Status newStatus, EnumSet<Order.Status> expects) {
		if (!expects.contains(order.getStatus())) {
			throw invalidOrderStatus(order, newStatus);
		}
	}

	private AirException invalidOrderStatus(T order, Order.Status status) {
		LOG.error("{} ({}) order cannot be update to status {}", type.getSimpleName(), order.getId(), status);
		return new AirException(Codes.ORDER_ILLEGAL_STATUS, M.msg(M.ORDER_ILLEGAL_STATUS));
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
	 * For USER (Exclude orders in DELETED status), IN statuses.
	 */
	protected Page<T> doListUserOrdersInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		Set<Order.Status> incudedStatuses = new HashSet<>(statuses);
		incudedStatuses.remove(Order.Status.DELETED);
		return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusInOrderByCreationDateDesc(userId, incudedStatuses,
				Pages.createPageRequest(page, pageSize)));
	}

	/**
	 * For USER (Exclude orders in DELETED status), NOT IN statuses.
	 */
	protected Page<T> doListUserOrdersNotInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		Set<Order.Status> excludedStatuses = new HashSet<>(statuses);
		excludedStatuses.add(Order.Status.DELETED);
		return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusNotInOrderByCreationDateDesc(userId,
				excludedStatuses, Pages.createPageRequest(page, pageSize)));
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
