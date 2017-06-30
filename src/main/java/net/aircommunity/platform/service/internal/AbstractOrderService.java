package net.aircommunity.platform.service.internal;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer;

import io.micro.common.DateFormats;
import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.common.OrderPrices;
import net.aircommunity.platform.model.OrderEvent;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PointRules;
import net.aircommunity.platform.model.PointsExchange;
import net.aircommunity.platform.model.PricePolicy;
import net.aircommunity.platform.model.RefundResponse;
import net.aircommunity.platform.model.UnitProductPrice;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.AircraftAwareOrder;
import net.aircommunity.platform.model.domain.CharterableOrder;
import net.aircommunity.platform.model.domain.Instalment;
import net.aircommunity.platform.model.domain.InstalmentOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.OrderRef;
import net.aircommunity.platform.model.domain.OrderSalesPackage;
import net.aircommunity.platform.model.domain.Passenger;
import net.aircommunity.platform.model.domain.PassengerItem;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Refund;
import net.aircommunity.platform.model.domain.SalesPackage;
import net.aircommunity.platform.model.domain.StandardOrder;
import net.aircommunity.platform.model.domain.StandardProduct;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BaseOrderRepository;
import net.aircommunity.platform.repository.OrderRefRepository;
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

	protected static final String CACHE_NAME = "cache.order";
	protected static final SimpleDateFormat DEPARTURE_DATE_FORMATTER = DateFormats.simple("yyyy-MM-dd");

	protected Class<T> type;

	@Resource
	private OrderNoGenerator orderNoGenerator;

	@Resource
	private OrderRefRepository orderRefRepository;

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
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_CREATE);
			timerContext = timer.time();
		}
		try {
			User owner = findAccount(userId, User.class);
			if (owner.getStatus() == Account.Status.LOCKED) {
				LOG.warn("Account {} is locked, cannot place orders", owner);
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
			// 1) retrieve product related to this order
			// fleet product is set in FleetCandidate, ignored
			if (order.getType() != Product.Type.FLEET) {
				Product product = order.getProduct();
				if (product == null) {
					LOG.error("Failed to create order {} for user {}, cause: product is not set", order, userId);
					throw newInternalException();
				}
				product = commonProductService.findProduct(product.getId());
				LOG.debug("Product: {}", product);
				order.setProduct(product);
				newOrder.setProduct(product);
				if (StandardProduct.class.isAssignableFrom(product.getClass())) {
					newOrder.setCurrencyUnit(StandardProduct.class.cast(product).getCurrencyUnit());
				}
			}
			// 2) set currency unit
			// should be SalesPackageProduct
			if (AircraftAwareOrder.class.isAssignableFrom(type)) {
				newOrder.setCurrencyUnit(AircraftAwareOrder.class.cast(order).getSalesPackage().getCurrencyUnit());
			}

			// 3) set basic properties
			newOrder.setOwner(owner);
			newOrder.setOrderNo(nextOrderNo());
			newOrder.setStatus(status);
			newOrder.setCreationDate(new Date());
			newOrder.setLastModifiedDate(newOrder.getCreationDate());
			newOrder.setCommented(false);
			// actually set when @PrePersist
			newOrder.setType(order.getType());

			// 4) copy common properties & copy order specific properties
			copyCommonProperties(order, newOrder);
			copyProperties(order, newOrder);

			LOG.info("[Creating order] User[{}][{}]: {}", owner.getId(), owner.getNickName(), order);
			BigDecimal totalPrice = newOrder.getTotalPrice();

			// 5.1) check if first order price off for this user (XXX DISABLED)
			// boolean existsOrder = getOrderRepository().existsByOwnerId(userId);
			// if (!existsOrder) {
			// long priceOff = memberPointsService.getPointsEarnedFromRule(Constants.PointRules.FIRST_ORDER_PRICE_OFF);
			// LOG.info("Offer first order price off [-{}] for user: {}", priceOff, userId);
			// BigDecimal newTotalPrice = totalPrice.subtract(BigDecimal.valueOf(priceOff));
			// if (newTotalPrice.compareTo(BigDecimal.ZERO) > 0) {
			// newOrder.setTotalPrice(newTotalPrice);
			// }
			// LOG.info("Updated total price [{}] for user: {}", newTotalPrice, userId);
			// }

			// 5.2) Calculate points to exchange
			// max: 50% percent of totalPrice
			int exchangePercent = memberPointsService.getPointsExchangePercent();
			int exchangeRate = memberPointsService.getPointsExchangeRate();
			LOG.debug("Point exchange percent: {},  exchange rate: {}", exchangePercent, exchangeRate);
			long pointsMax = totalPrice.multiply(BigDecimal.valueOf(exchangePercent))
					.multiply(BigDecimal.valueOf(exchangeRate)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
					.longValue();
			LOG.debug("Point exchange pointsMax: {} for order with total amount: {}", pointsMax, totalPrice);
			long pointToExchange = order.getPointsUsed();
			if (pointToExchange > 0) {
				pointToExchange = Math.min(pointsMax, owner.getPoints());
				PointsExchange pointsUsed = memberPointsService.exchangePoints(pointToExchange);
				LOG.info("[Exchanging points] User[{}][{}]: order[{}], exchanged points {}", owner.getId(),
						owner.getNickName(), newOrder.getOrderNo(), pointsUsed);
				BigDecimal moneyExchanged = BigDecimal.valueOf(pointsUsed.getMoneyExchanged());
				BigDecimal updatedTotalPrice = newOrder.getTotalPrice().subtract(moneyExchanged);
				// cannot be less than 0 after points exchange, just ignore points exchange if less or equal than 0
				if (updatedTotalPrice.compareTo(BigDecimal.ZERO) > 0) {
					newOrder.setPointsUsed(pointsUsed.getPointsExchanged());
					// update total price
					newOrder.setTotalPrice(updatedTotalPrice);
					newOrder.setOriginalTotalPrice(newOrder.getTotalPrice());
					User account = (User) accountService.updateUserPoints(owner.getId(),
							-pointsUsed.getPointsExchanged());
					LOG.info(
							"[Exchanged points] User[{}][{}]: order[{}], updated total price: {}, points used: {}, account points balance: {}",
							owner.getId(), owner.getNickName(), newOrder.getOrderNo(), updatedTotalPrice,
							pointsUsed.getPointsExchanged(), account.getPoints());
				}
				else {
					LOG.warn(
							"[Exchange points] User[{}][{}]: order[{}]: update total price is: {} (less than 0), points exchange ignored.",
							owner.getId(), owner.getNickName(), newOrder.getOrderNo(), updatedTotalPrice);
				}
			}
			// 6) perform save
			final T orderToSave = newOrder;
			T saved = safeExecute(() -> {
				T orderSaved = getOrderRepository().save(orderToSave);
				LOG.info("[Created order] User[{}][{}]: {}", owner.getId(), owner.getNickName(), orderSaved);
				eventBus.post(new OrderEvent(OrderEvent.EventType.CREATION, orderSaved));
				return orderSaved;
			}, "Create %s: %s for user %s failed", type.getSimpleName(), order, userId);

			// 7) create ref
			OrderRef orderRef = new OrderRef();
			orderRef.setOrderId(saved.getId());
			orderRef.setOrderNo(saved.getOrderNo());
			orderRef.setOwnerId(owner.getId());
			orderRef.setStatus(saved.getStatus());
			orderRef.setType(saved.getType());
			orderRef.setCreationDate(saved.getCreationDate());
			orderRefRepository.save(orderRef);
			// returning finally
			return saved;
		} // metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	private String nextOrderNo() {
		return retryTemplate.execute(context -> orderNoGenerator.next());
	}

	private void copyCommonProperties(Order src, Order tgt) {
		tgt.setChannel(src.getChannel());
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
			newCharterableOrder.setPassengers(charterableOrder.getPassengers());
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
		tgt.setOriginalTotalPrice(tgt.getTotalPrice());
		LOG.debug("final totalPrice: {}, OriginalTotalPrice: {}", tgt.getTotalPrice(), tgt.getOriginalTotalPrice());
	}

	/**
	 * To be overridden by subclass
	 * 
	 * @param src source
	 * @param tgt target
	 */
	protected void copyProperties(T src, T tgt) {
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
		tgt.setSalesPackage(new OrderSalesPackage(salesPackage));
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
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_READ);
			timerContext = timer.time();
		}
		try {
			T order = doFindOrder0(orderId);
			// XXX CANNOT load all passengers for tour, taxi , trans
			if (order == null || order.getStatus() == Order.Status.DELETED) {
				LOG.error("{}: {} is not found", type.getSimpleName(), orderId);
				throw new AirException(orderNotFoundCode(), M.msg(M.ORDER_NOT_FOUND, orderId));
			}
			return order;
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
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
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_UPDATE);
			timerContext = timer.time();
		}
		try {
			T order = doFindOrder(orderId);
			copyCommonProperties(newOrder, order);
			copyProperties(newOrder, order);
			order.setLastModifiedDate(new Date());
			return safeExecute(() -> getOrderRepository().save(order), "Update %s: %s with %s failed",
					type.getSimpleName(), orderId, newOrder);
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * Mark as commented
	 */
	protected T doUpdateOrderCommented(String orderId) {
		T order = doFindOrder(orderId);
		order.setCommented(true);
		return safeExecute(() -> getOrderRepository().save(order), "Update %s: %s to commented failed",
				type.getSimpleName(), orderId);
	}

	/**
	 * Update total price of an order
	 */
	protected T doUpdateOrderTotalAmount(String orderId, BigDecimal newTotalAmount) {
		ensureTotalAmount(newTotalAmount);
		T order = doFindOrder(orderId);
		order.setLastModifiedDate(new Date());
		order.setTotalPrice(newTotalAmount);
		return safeExecute(() -> getOrderRepository().save(order), "Update %s price: %s to %d failed",
				type.getSimpleName(), orderId, newTotalAmount);
	}

	protected void ensureTotalAmount(BigDecimal totalAmount) {
		if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new AirException(Codes.ORDER_INVALID_TOTAL_AMOUNT, M.msg(M.ORDER_INVALID_OFFER_TOTAL_AMOUNT));
		}
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
		T order = doFindOrder(orderId);
		return doPayOrder(order, null, payment);
	}

	protected T doPayOrder(T order, Payment payment) {
		return doPayOrder(order, null, payment);
	}

	/**
	 * Pay order
	 */
	protected T doPayOrder(T order, String instalmentId, Payment payment) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_PAYMENT);
			timerContext = timer.time();
		}
		try {
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
				Payment orderPayment = standardOrder.getPayment();
				// check existence of the payment
				if (orderPayment != null) {
					LOG.warn("Payment is already done for {}, skip current payment: {}", order, newPayment);
					return order;
				}
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
					LOG.error("Order {} instalment {} not found", order, instalmentId);
					throw new AirException(Codes.ORDER_INSTALMENT_NOT_FOUND, M.msg(M.ORDER_INSTALMENT_NOT_FOUND));
				}
				// TODO check existence of payment
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
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * Request refund
	 */
	protected T doRequestOrderRefund(String orderId, String refundReason) {
		T order = doFindOrder(orderId);
		String reason = refundReason;
		if (Strings.isBlank(reason)) {
			reason = M.msg(M.REFUND_REASON_MISSING);
		}
		order.setRefundReason(reason);
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
	protected T doAcceptOrderRefund(String orderId, Refund refund) {
		T orderRefund = null;
		T order = doUpdateOrderStatus(orderId, Order.Status.REFUNDING);
		if (StandardOrder.class.isAssignableFrom(order.getClass())) {
			StandardOrder standardOrder = StandardOrder.class.cast(order);
			refund.setVendor(standardOrder.getProduct().getVendor());
			refund.setOwner(standardOrder.getOwner());
			standardOrder.setRefund(refund);
			standardOrder.setRefundFailureCause(null);
			orderRefund = doUpdateOrderStatus(order, Order.Status.REFUNDED);
			eventBus.post(new OrderEvent(OrderEvent.EventType.REFUNDED, orderRefund));
		}
		// TODO NOT IMPLED
		// installment order
		// if (InstalmentOrder.class.isAssignableFrom(order.getClass())) {
		// InstalmentOrder instalmentOrder = InstalmentOrder.class.cast(order);
		// }
		return orderRefund;
	}

	/**
	 * Refund initiated from tenant and admin
	 */
	protected T doInitiateOrderRefund(String orderId, BigDecimal refundAmount, String refundReason) {
		T order = doFindOrder(orderId);
		order.setRefundReason(M.msg(M.REFUND_REASON_FORCE, refundReason));
		return doAcceptOrderRefund(orderId, refundAmount);
	}

	/**
	 * Accept refund
	 */
	protected T doAcceptOrderRefund(String orderId, BigDecimal refundAmount) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_REFUND);
			timerContext = timer.time();
		}
		try {
			// update to Order.Status.REFUNDING now
			T order = doUpdateOrderStatus(orderId, Order.Status.REFUNDING);
			T orderRefund = order;
			// standard order
			if (StandardOrder.class.isAssignableFrom(order.getClass())) {
				StandardOrder standardOrder = StandardOrder.class.cast(order);
				Payment payment = standardOrder.getPayment();
				LOG.debug("Order {} payment: {}", order, payment);
				if (payment == null) {
					throw new AirException(Codes.ORDER_ILLEGAL_STATUS,
							M.msg(M.ORDER_PAYMENT_NOT_FOUND, order.getOrderNo()));
				}
				RefundResponse refundResponse = paymentService.refundPayment(payment.getMethod(), order, refundAmount);
				int code = refundResponse.getCode();
				switch (code) {
				case RefundResponse.PENDING:
					// just left order status as REFUNDING
					break;

				case RefundResponse.SUCCESS:
					Refund refund = refundResponse.getRefund();
					refund.setVendor(standardOrder.getProduct().getVendor());
					refund.setOwner(standardOrder.getOwner());
					standardOrder.setRefund(refund);
					standardOrder.setRefundFailureCause(null);
					orderRefund = doUpdateOrderStatus(order, Order.Status.REFUNDED);
					eventBus.post(new OrderEvent(OrderEvent.EventType.REFUNDED, orderRefund));
					break;

				case RefundResponse.FAILURE:
					order.setRefundFailureCause(refundResponse.getFailureCause());
					orderRefund = doUpdateOrderStatus(order, Order.Status.REFUND_FAILED);
					eventBus.post(new OrderEvent(OrderEvent.EventType.REFUND_FAILED, orderRefund));
					break;

				default:
					// noops
				}
			}
			// TODO NOT IMPLED
			// installment order
			// if (InstalmentOrder.class.isAssignableFrom(order.getClass())) {
			// InstalmentOrder instalmentOrder = InstalmentOrder.class.cast(order);
			// }
			return orderRefund;
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * Payment failure
	 */
	protected T doHandleOrderPaymentFailure(T order, String paymentFailureCause) {
		order.setPaymentFailureCause(paymentFailureCause);
		return doUpdateOrderStatus(order, Order.Status.PAYMENT_FAILED);
	}

	/**
	 * Refund failure
	 */
	protected T doHandleOrderRefundFailure(String orderId, String refundFailureCause) {
		// NOTE: not from cache, in case it's updated
		T order = doFindOrder(orderId);
		return doHandleOrderRefundFailure(order, refundFailureCause);
	}

	protected T doHandleOrderRefundFailure(T order, String refundFailureCause) {
		order.setRefundFailureCause(refundFailureCause);
		return doUpdateOrderStatus(order, Order.Status.REFUND_FAILED);
	}

	/**
	 * Cancel
	 */
	protected T doCancelOrder(String orderId, String cancelReason) {
		// NOTE: not from cache, in case it's updated
		T order = doFindOrder(orderId);
		order.setCancelReason(cancelReason);
		return doUpdateOrderStatus(order, Order.Status.CANCELLED);
	}

	/**
	 * Close
	 */
	protected T doCloseOrder(String orderId, String closedReason) {
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

			// AirJet
		case CUSTOMER_CONFIRMED:
			LOG.debug("Expect order intial statuss & confirmationRequired");
			expectOrderStatusCondition(order.isInitialStatus());
			expectOrderStatusCondition(order.confirmationRequired());
			break;

		// AirJet, AirTravel
		case CONFIRMED:
			LOG.debug("Expect order confirmationRequired");
			expectOrderStatusCondition(order.confirmationRequired());
			break;

		// AirJet, Course
		case CONTRACT_SIGNED:
			// should be already confirmed
			LOG.debug("Expect order contractRequired");
			expectOrderStatusCondition(order.contractRequired());
			if (order.confirmationRequired()) {
				expectOrderStatus(order, newStatus, Order.Status.CONFIRMED);
			}
			break;

		// may called by client notification, ignored if already paid
		case PAYMENT_IN_PROCESS:
			if (order.isPaid()) {
				LOG.warn("Ignored status update to: {}, because this order is already PAID", newStatus);
				return order;
			}
			LOG.debug("Expect order isPayable");
			expectOrderStatusCondition(order.isPayable());
			break;

		case PARTIAL_PAID:
			// TODO check later
			LOG.debug("Check {} is NOT implemented yet", newStatus);
			break;

		case PAID:
			// we allow refund request to move back to paid status in case tenant rejected the refund request
			// should be already contract signed (if contract sign is required)
			if (order.contractRequired()) {
				LOG.debug("Order contractRequired, expect contract signed before payment");
				expectOrderStatus(order, newStatus,
						EnumSet.of(Order.Status.CONTRACT_SIGNED, Order.Status.PAYMENT_IN_PROCESS,
								Order.Status.PARTIAL_PAID, Order.Status.PAYMENT_FAILED, Order.Status.REFUND_REQUESTED));
			}
			// otherwise, should be in CREATED | PUBLISHED (XXX require confirmation? or just paid and then confirm)
			else {
				// expectOrderStatusCondition(order.isInitialStatus());
				expectOrderStatus(order, newStatus,
						EnumSet.of(Order.Status.CREATED, Order.Status.PUBLISHED, Order.Status.PAYMENT_IN_PROCESS,
								Order.Status.PARTIAL_PAID, Order.Status.PAYMENT_FAILED, Order.Status.REFUND_REQUESTED));
			}
			// already paid for REFUND_REQUESTED (cannot re-setPaymentDate)
			if (order.getStatus() != Order.Status.REFUND_REQUESTED) {
				order.setPaymentDate(new Date());
			}
			break;

		case PAYMENT_FAILED:
			// TODO: should handle PARTIAL_PAID once its implemented
			LOG.debug("Expect order isPayable");
			expectOrderStatusCondition(order.isPayable());
			break;

		//
		case REFUND_REQUESTED:
			// should be already paid
			expectOrderStatus(order, newStatus, EnumSet.of(Order.Status.REFUND_FAILED, Order.Status.PARTIAL_PAID,
					Order.Status.PAID, Order.Status.TICKET_RELEASED));
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
			returnBackPoints(order, newStatus);
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
			long pointsEarnedPercent = memberPointsService
					.getPointsEarnedFromRule(PointRules.getOrderFinishedRule(order.getProduct().getCategory()));
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
			returnBackPoints(order, newStatus);
			break;

		case CLOSED:
			LOG.debug("Expect order isCloseable");
			expectOrderStatusCondition(order.isCloseable());
			order.setClosedDate(new Date());
			returnBackPoints(order, newStatus);
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
		T updated = safeExecute(() -> {
			T orderUpdated = getOrderRepository().save(order);
			if (orderUpdated.getStatus() == Order.Status.CANCELLED) {
				eventBus.post(new OrderEvent(OrderEvent.EventType.CANCELLATION, orderUpdated));
				LOG.info("Notify client managers on order cancellation: {}", orderUpdated);
			}
			return orderUpdated;
		}, "Update %s: %s to status %s failed", type.getSimpleName(), order.getId(), newStatus);

		// update the status of order ref
		orderRefRepository.updateOrderStatus(updated.getId(), updated.getStatus());
		return updated;
	}

	private void returnBackPoints(Order order, Order.Status status) {
		long pointsUsed = order.getPointsUsed();
		if (pointsUsed > 0) {
			// add points back to the account
			accountService.updateUserPoints(order.getOwner().getId(), pointsUsed);
			order.setPointsUsed(0);
			LOG.info("Return points: {} back to account: {} on order action: {}", pointsUsed, order.getOwner(), status);
		}
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
	 * For USER (Exclude orders in DELETED status) (all)
	 */
	protected Page<T> doListUserOrders(String userId, Order.Status status, int page, int pageSize) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_USER_LIST);
			timerContext = timer.time();
		}
		try {
			if (Order.Status.DELETED == status) {
				return Page.emptyPage(page, pageSize);
			}
			if (status == null) {
				return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusInOrderByCreationDateDesc(userId,
						Order.Status.VISIBLE_STATUSES, Pages.createPageRequest(page, pageSize)));
				// return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusNotOrderByCreationDateDesc(userId,
				// Order.Status.DELETED, Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
					Pages.createPageRequest(page, pageSize)));
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * For USER (Exclude orders in DELETED status), IN statuses (different status group).
	 */
	protected Page<T> doListUserOrdersInStatuses(String userId, Set<Order.Status> statuses, int page, int pageSize) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_USER_LIST_GROUPED);
			timerContext = timer.time();
		}
		try {
			Set<Order.Status> incudedStatuses = new HashSet<>(statuses);
			incudedStatuses.remove(Order.Status.DELETED);
			return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusInOrderByCreationDateDesc(userId,
					incudedStatuses, Pages.createPageRequest(page, pageSize)));
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * For USER (Exclude orders in DELETED status)
	 */
	protected Page<T> doListUserOrdersOfRecentDays(String userId, int days, int page, int pageSize) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(type, ORDER_ACTION_USER_LIST_NDAYS);
			timerContext = timer.time();
		}
		try {
			if (days < 0) {
				days = 0;
			}
			if (days > 6 * 30) {
				days = 6 * 30; // max 6 months
			}
			Date creationDate = LocalDateTime.now().minusDays(days).toDate();
			LOG.debug("List recent {} days({}) orders for user: {}", days, creationDate, userId);
			return Pages.adapt(getOrderRepository()
					.findByOwnerIdAndStatusInAndCreationDateLessThanEqualOrderByCreationDateDesc(userId,
							Order.Status.VISIBLE_STATUSES, creationDate, Pages.createPageRequest(page, pageSize)));
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * For USER (Exclude orders in DELETED status), NOT IN statuses. (XXX NOT USED YET)
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
	protected Page<T> doListAllUserOrders(String userId, @Nullable Order.Status status, int page, int pageSize) {
		return doListAllUserOrders(userId, status, null, page, pageSize);
	}

	/**
	 * For ADMIN for a user (orders in any status and type), list all of a user
	 */
	protected Page<T> doListAllUserOrders(String userId, @Nullable Order.Status status, @Nullable Product.Type type,
			int page, int pageSize) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(this.type, ORDER_ACTION_ADMIN_LIST_USER);
			timerContext = timer.time();
		}
		try {
			if (status != null) {
				if (type != null) {
					return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusAndTypeOrderByCreationDateDesc(userId,
							status, type, Pages.createPageRequest(page, pageSize)));
				}
				return Pages.adapt(getOrderRepository().findByOwnerIdAndStatusOrderByCreationDateDesc(userId, status,
						Pages.createPageRequest(page, pageSize)));
			}
			if (type != null) {
				return Pages.adapt(getOrderRepository().findByOwnerIdAndTypeOrderByCreationDateDesc(userId, type,
						Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(getOrderRepository().findByOwnerIdOrderByCreationDateDesc(userId,
					Pages.createPageRequest(page, pageSize)));
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * For ADMIN (orders in any status)
	 */
	protected Page<T> doListAllOrders(@Nullable Order.Status status, int page, int pageSize) {
		return doListAllOrders(status, null, page, pageSize);
	}

	/**
	 * For ADMIN (orders in any status and type) (full table scan)
	 */
	protected Page<T> doListAllOrders(@Nullable Order.Status status, @Nullable Product.Type type, int page,
			int pageSize) {
		Timer.Context timerContext = null;
		if (isMetricsEnabled()) {
			Timer timer = orderOperationTimer(this.type, ORDER_ACTION_ADMIN_LIST);
			timerContext = timer.time();
		}
		try {
			if (status != null) {
				if (type != null) {
					return Pages.adapt(getOrderRepository().findByStatusAndTypeOrderByCreationDateDesc(status, type,
							Pages.createPageRequest(page, pageSize)));
				}
				return Pages.adapt(getOrderRepository().findByStatusOrderByCreationDateDesc(status,
						Pages.createPageRequest(page, pageSize)));
			}
			if (type != null) {
				return Pages.adapt(getOrderRepository().findByTypeOrderByCreationDateDesc(type,
						Pages.createPageRequest(page, pageSize)));
			}
			return Pages.adapt(
					getOrderRepository().findAllByOrderByCreationDateDesc(Pages.createPageRequest(page, pageSize)));
		}
		// metrics
		finally {
			if (isMetricsEnabled()) {
				timerContext.stop();
			}
		}
	}

	/**
	 * For ADMIN
	 */
	protected void doDeleteOrder(String orderId) {
		safeDeletion(getOrderRepository(), orderId, Codes.ORDER_CANNOT_BE_DELETED, M.msg(M.ORDER_CANNOT_BE_DELETED));
		orderRefRepository.delete(orderId);
	}

	/**
	 * For ADMIN
	 */
	protected void doDeleteOrders(String userId) {
		safeDeletion(getOrderRepository(), () -> getOrderRepository().deleteByOwnerId(userId),
				Codes.ORDER_CANNOT_BE_DELETED, M.msg(M.USER_ORDERS_CANNOT_BE_DELETED, userId));
		orderRefRepository.deleteByOwnerId(userId);
	}
}
