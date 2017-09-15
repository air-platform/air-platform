package net.aircommunity.platform.service.internal.order;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

import io.micro.common.Strings;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.DomainEvent;
import net.aircommunity.platform.model.OrderEvent;
import net.aircommunity.platform.model.domain.AirTaxiOrder;
import net.aircommunity.platform.model.domain.AirTourOrder;
import net.aircommunity.platform.model.domain.AirTransportOrder;
import net.aircommunity.platform.model.domain.CharterOrder;
import net.aircommunity.platform.model.domain.Contact;
import net.aircommunity.platform.model.domain.CourseOrder;
import net.aircommunity.platform.model.domain.FerryFlightOrder;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.model.domain.FleetCandidate;
import net.aircommunity.platform.model.domain.JetTravelOrder;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.domain.PushNotification.BusinessType;
import net.aircommunity.platform.model.domain.PushNotification.Type;
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.PlatformService;
import net.aircommunity.platform.service.common.MailService;
import net.aircommunity.platform.service.common.SmsService;
import net.aircommunity.platform.service.common.TemplateService;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.order.OrderNotificationService;
import net.aircommunity.platform.service.product.FleetService;

/**
 * Order notification service implementation.
 *
 * @author Bin.Zhang
 */
@Service
public class OrderNotificationServiceImpl extends AbstractServiceSupport implements OrderNotificationService {
	private static final Logger LOG = LoggerFactory.getLogger(OrderNotificationServiceImpl.class);

	private static final String CTX_CUSTOMER = "customer";
	private static final String CTX_ACTION = "action";
	private static final String CTX_CLIENT_MANAGER = "clientManager";
	private static final String CTX_ORDER = "order";
	private static final String CTX_ORDER_NO = "orderNo";
	private static final String CTX_ORDER_OPERATION = "orderOperation";
	private static final String CTX_ORDER_TYPE = "orderType";
	private static final String CTX_CREATION_DATE = "creationDate";
	private static final String CTX_NOTE = "note";
	private static final String CTX_CONTACT = "contact";
	private static final String CTX_COMPANY = "company";
	private static final String CTX_AIRCRAFT_TYPE = "aircraftType";
	private static final String CTX_LICENSE = "license";
	private static final String CTX_LOCATION = "location";
	private static final String CTX_DATE = "date";
	private static final String CTX_TIMESLOT = "timeSlot";
	private static final String CTX_FLIGHT_LEGS = "flightLegs";
	private static final String CTX_FERRYFLIGHT = "ferryFlight";
	private static final String CTX_PASSENGERS = "passengers";
	private static final String CTX_JETTRAVEL = "jetTravel";
	private static final String CTX_AIRTAXI = "airTaxi";
	private static final String CTX_AIRTOUR = "airTour";
	private static final String CTX_AIRTRANSPORT = "airTransport";
	private static final String CTX_COURSE = "course";
	private static final Map<Product.Type, String> orderTypeMapping = new HashMap<>();
	private static final Map<Order.Status, String> orderOperationsMapping = new HashMap<>();

	@Resource
	private TemplateService templateService;

	@Resource
	private MailService mailService;

	@Resource
	private SmsService smsService;

	@Resource
	private FleetService fleetService;

	@Resource
	private PlatformService platformService;

	public static void main(String arg[]) {
		System.out.println(normalizeProductCategory(Category.AIR_JET));
	}

	@PostConstruct
	private void init() {
		orderOperationsMapping.put(Order.Status.CANCELLED, M.msg(M.ORDER_OPERATION_CANCEL));
		orderOperationsMapping.put(Order.Status.CREATED, M.msg(M.ORDER_OPERATION_SUBMIT));
		orderOperationsMapping.put(Order.Status.PAID, M.msg(M.ORDER_OPERATION_PAY));
		orderOperationsMapping.put(Order.Status.REFUND_REQUESTED, M.msg(M.ORDER_OPERATION_REFUND_REQUESTED));
		//
		String airjet = normalizeProductCategory(Category.AIR_JET);
		String airtaxi = normalizeProductCategory(Category.AIR_TAXI);
		String airtrans = normalizeProductCategory(Category.AIR_TRANS);
		String airtour = normalizeProductCategory(Category.AIR_TOUR);
		String airtraining = normalizeProductCategory(Category.AIR_TRAINING);
		String airtquick = normalizeProductCategory(Category.AIR_QUICKFLIGHT);
		orderTypeMapping.put(Product.Type.FLEET, airjet);
		orderTypeMapping.put(Product.Type.FERRYFLIGHT, airjet);
		orderTypeMapping.put(Product.Type.JETTRAVEL, airjet);
		orderTypeMapping.put(Product.Type.AIRTAXI, airtaxi);
		orderTypeMapping.put(Product.Type.AIRTOUR, airtour);
		orderTypeMapping.put(Product.Type.AIRTRANSPORT, airtrans);
		orderTypeMapping.put(Product.Type.COURSE, airtraining);
		orderTypeMapping.put(Product.Type.QUICKFLIGHT, airtquick);
		eventBus.register(this);

		// register push notification
		registerPushNotificationEvent(EnumSet.of(DomainEvent.DomainType.ORDER));

	}

	private static String normalizeProductCategory(Category category) {
		String[] parts = category.name().toLowerCase(Locale.ENGLISH).split("_");
		return new StringBuilder(Strings.capitalize(parts[0])).append(Strings.capitalize(parts[1])).toString();
	}

	@Subscribe
	private void onOrderEvent(OrderEvent event) {
		LOG.debug("Received order event: {}", event);
		Order o = event.getOrder();
		User u = o.getOwner();
		DomainEvent de = new DomainEvent(DomainEvent.DomainType.ORDER, DomainEvent.Operation.PUSH_NOTIFICATION);
		de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_ACCOUNTID, u.getId());
		String extras = new StringBuffer().append(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS_CONTENT_TYPE).append(":")
				.append(Type.PLAIN_TEXT.toString().toLowerCase()).append(";")
				.append(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS_BUSINESS_TYPE).append(":")
				.append(BusinessType.ORDER.toString().toLowerCase()).append(";")
				.append(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS_ORDER_ID).append(":").append(o.getId()).append(";")
				.append(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS_ORDER_TYPE).append(":")
				.append(o.getType().toString().toLowerCase()).toString();

		de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_EXTRAS, extras);
		// FIXME String tips;
		// if(event.getType() != OrderEvent.EventType.CREATION){

		// }

		switch (event.getType()) {
		case CREATION:
			doNotifyClientManager(getClientManagerContacts(event), event.getOrder());
			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_CREATION);
			postDomainEvent(de);

			break;
		case CANCELLATION:
			doNotifyClientManager(getClientManagerContacts(event), event.getOrder());
			break;

		// fleet is offer by a tenant
		case FLEET_OFFERED:
			// TODO SMS notification to customer
			Contact contactPerson = event.getOrder().getContact();
			notifyCustomer(ImmutableSet.of(contactPerson), event);

			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_OFFER);
			postDomainEvent(de);

			break;

		// flight is offer by a platform
		case QUICK_FLIGHT_OFFERED:
			// TODO SMS notification to customer

			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_OFFER);
			postDomainEvent(de);

			break;

		case CONFIRMED:
			// TODO SMS notification to customer

			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_OFFER_CONFIRM);
			postDomainEvent(de);

			break;

		case PAYMENT:

			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_PAY);
			postDomainEvent(de);
			doNotifyClientManager(getClientManagerContacts(event), event.getOrder());
			break;

		case REFUND_REQUESTED:
			doNotifyClientManager(getClientManagerContacts(event), event.getOrder());
			break;

		case REFUNDED:
		case REFUND_FAILED:
			// TODO send SMS to notify customer and tenant customer manager ?
			break;

		case UPDATE:

			break;

		case DELETION:
			break;

		case CONTRACT_SIGNED:
			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_SIGN);
			postDomainEvent(de);
			break;

		case TICKET_RELEASED:
			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_TICKET);
			postDomainEvent(de);
			break;

		case FINISHED:
			de.addParam(Constants.TEMPLATE_PUSHNOTIFICATION_MESSAGE, Constants.TEMPLATE_PUSHNOTIFICATION_FINISH);
			postDomainEvent(de);
			break;

		default:// noop
		}
	}

	private Set<Contact> getClientManagerContacts(OrderEvent event) {
		Order order = event.getOrder();
		Product.Type type = order.getType();
		ImmutableSet.Builder<Contact> contactsBuilder = ImmutableSet.builder();
		switch (type) {
		case QUICKFLIGHT:
			// just notify platform client managers
			break;

		case FLEET:
			CharterOrder charterOrder = CharterOrder.class.cast(order);
			Set<FleetCandidate> candidates = charterOrder.getCandidates();
			if (candidates.isEmpty()) {
				Set<Contact> contacts = fleetService.listFleets().stream()
						.flatMap(fleet -> fleet.getClientManagerContacts().stream()).collect(Collectors.toSet());
				contactsBuilder.addAll(contacts);
			}
			else {
				Set<Contact> contacts = candidates.stream()
						.flatMap(candidate -> candidate.getFleet().getClientManagerContacts().stream())
						.collect(Collectors.toSet());
				contactsBuilder.addAll(contacts);
			}
			break;

		default:
			Product product = order.getProduct();
			contactsBuilder.addAll(product.getClientManagerContacts());
			break;
		}
		Set<Contact> platfromContacts = platformService.getPlatformClientManagers();
		LOG.debug("Platfrom clientManagers: {}", platfromContacts);
		contactsBuilder.addAll(platfromContacts);
		return contactsBuilder.build();
	}

	@Override
	public void notifyCustomer(Set<Contact> customerContacts, OrderEvent event) {
		LOG.debug("Notifying... customers {} for order evnet {}", customerContacts, event);
		Map<String, Object> context = new HashMap<>();
		customerContacts.stream().forEach(contact -> {
			String customer = contact.getPerson();
			if (Strings.isBlank(customer)) {
				customer = contact.getMobile();
			}
			context.put(CTX_CUSTOMER, customer);
			context.put(CTX_ORDER, event.getOrder());
			context.put(CTX_ACTION, getActionForOrderEvent(event));
			String message = templateService.renderFile(String.format(Constants.TEMPLATE_SMS_ORDER_EVENT_NOTIFICATION,
					event.getType().name().replace("_", "-").toLowerCase(Locale.ENGLISH)), context);
			LOG.debug("SMS message: {}", message);
			smsService.sendSms(contact.getMobile(), message);
		});

	}

	private String getActionForOrderEvent(OrderEvent event) {
		String action = "";
		switch (event.getType()) {
		case FLEET_OFFERED:
			// TODO convert Currency
			// http://www.infoq.com/cn/articles/JSR-354-Java-Money-Currency-API
			// https://github.com/JavaMoney/jsr354-api
			action = M.msg(M.FLEET_OFFERED, event.getOrder().getTotalPrice());
			break;
		default:
		}
		return action;
	}

	@Override
	public void notifyClientManager(Set<Contact> clientManagers, OrderEvent event) {
		switch (event.getType()) {
		case CREATION:
		case CANCELLATION:
			doNotifyClientManager(clientManagers, event.getOrder());
			break;

		case UPDATE:
			break;

		case DELETION:
			break;

		default:// noop
		}
	}

	private void doNotifyClientManager(Set<Contact> clientManagers, Order order) {
		if (!configuration.isOrderEmailNotificationEnabled()) {
			LOG.warn(
					"Order email notification is not enabled, cannot notify client managers {}, order notification ignored.",
					clientManagers);
			return;
		}
		LOG.debug("Notifying... clientManagers {} for order {}", clientManagers, order);
		// TODO make constants
		for (Contact clientManager : clientManagers) {
			Map<String, Object> context = new HashMap<>();
			User customer = order.getOwner();
			// use realName if available, otherwise use nickName
			context.put(CTX_CUSTOMER,
					Strings.isNotBlank(customer.getRealName()) ? customer.getRealName() : customer.getNickName());
			context.put(CTX_CLIENT_MANAGER, clientManager.getPerson());
			context.put(CTX_ORDER_NO, order.getOrderNo());
			context.put(CTX_ORDER_OPERATION, orderOperationsMapping.get(order.getStatus()));
			context.put(CTX_ORDER_TYPE, orderTypeMapping.get(order.getType()));
			context.put(CTX_CREATION_DATE, order.getCreationDate());
			context.put(CTX_NOTE, order.getNote());
			context.put(CTX_CONTACT, order.getContact());
			context.put(CTX_COMPANY, configuration.getCompany());
			//
			switch (order.getType()) {
			case FLEET:
				CharterOrder charterOrder = CharterOrder.class.cast(order);
				String aircraftType = M.msg(M.FLEET_NOT_SELECTED);
				Fleet fleet = charterOrder.getProduct();
				if (fleet != null) {
					aircraftType = fleet.getAircraftType();
				}
				else {
					Set<FleetCandidate> fleetCandidates = charterOrder.getCandidates();
					if (!fleetCandidates.isEmpty()) {
						aircraftType = fleetCandidates.stream().map(candidate -> candidate.getFleet().getAircraftType())
								.distinct().collect(Collectors.joining(", "));
					}
				}
				context.put(CTX_AIRCRAFT_TYPE, aircraftType);
				context.put(CTX_FLIGHT_LEGS, charterOrder.getFlightLegs());
				break;

			case FERRYFLIGHT:
				FerryFlightOrder ferryFlightOrder = FerryFlightOrder.class.cast(order);
				context.put(CTX_FERRYFLIGHT, ferryFlightOrder.getProduct());
				context.put(CTX_PASSENGERS, ferryFlightOrder.getPassengers());
				break;

			case JETTRAVEL:
				JetTravelOrder jetTravelOrder = JetTravelOrder.class.cast(order);
				context.put(CTX_JETTRAVEL, jetTravelOrder.getProduct());
				break;

			case AIRTAXI:
				AirTaxiOrder airTaxiOrder = AirTaxiOrder.class.cast(order);
				context.put(CTX_AIRTAXI, airTaxiOrder.getProduct());
				context.put(CTX_PASSENGERS, airTaxiOrder.getPassengers());
				context.put(CTX_AIRCRAFT_TYPE, airTaxiOrder.getSalesPackage().getAircraft().getName());
				context.put(CTX_DATE, airTaxiOrder.getDepartureDate());
				context.put(CTX_TIMESLOT, airTaxiOrder.getTimeSlot());
				break;

			case AIRTOUR:
				AirTourOrder airTourOrder = AirTourOrder.class.cast(order);
				context.put(CTX_AIRTOUR, airTourOrder.getProduct());
				context.put(CTX_PASSENGERS, airTourOrder.getPassengers());
				context.put(CTX_AIRCRAFT_TYPE, airTourOrder.getSalesPackage().getAircraft().getName());
				context.put(CTX_DATE, airTourOrder.getDepartureDate());
				context.put(CTX_TIMESLOT, airTourOrder.getTimeSlot());
				break;

			case AIRTRANSPORT:
				AirTransportOrder airTransportOrder = AirTransportOrder.class.cast(order);
				context.put(CTX_AIRTRANSPORT, airTransportOrder.getProduct());
				context.put(CTX_PASSENGERS, airTransportOrder.getPassengers());
				context.put(CTX_AIRCRAFT_TYPE, airTransportOrder.getSalesPackage().getAircraft().getName());
				context.put(CTX_DATE, airTransportOrder.getDepartureDate());
				context.put(CTX_TIMESLOT, airTransportOrder.getTimeSlot());
				break;

			case COURSE:
				CourseOrder course = CourseOrder.class.cast(order);
				context.put(CTX_COURSE, course.getProduct());
				context.put(CTX_AIRCRAFT_TYPE, course.getAircraftType());
				context.put(CTX_LICENSE, course.getLicense());
				context.put(CTX_LOCATION, course.getLocation());
				break;

			default:
			}
			//
			String mailBody = templateService.renderFile(String.format(Constants.TEMPLATE_MAIL_ORDER_NOTIFICATION,
					order.getType().name().toLowerCase(Locale.ENGLISH)), context);
			LOG.trace("Mail body: {}", mailBody);
			if (Strings.isNotBlank(clientManager.getEmail())) {
				mailService.sendMail(clientManager.getEmail(), configuration.getOrderEmailNotificationSubject(),
						mailBody);
				LOG.debug("Sent order notification {} to clientManager {}", order, clientManager);
			}
		}
	}
}
