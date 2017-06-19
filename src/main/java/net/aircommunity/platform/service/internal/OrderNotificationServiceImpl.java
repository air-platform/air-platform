package net.aircommunity.platform.service.internal;

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
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import io.micro.common.Strings;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
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
import net.aircommunity.platform.model.domain.User;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.FleetService;
import net.aircommunity.platform.service.MailService;
import net.aircommunity.platform.service.OrderNotificationService;
import net.aircommunity.platform.service.PlatformService;
import net.aircommunity.platform.service.SmsService;
import net.aircommunity.platform.service.TemplateService;

/**
 * Order notification service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class OrderNotificationServiceImpl implements OrderNotificationService {
	private static final Logger LOG = LoggerFactory.getLogger(OrderNotificationServiceImpl.class);

	private static final Map<Order.Type, String> orderTypeMapping = new HashMap<>();
	private static final Map<Order.Status, String> orderOperationsMapping = new HashMap<>();

	@Resource
	private Configuration configuration;

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

	@Resource
	private EventBus eventBus;

	@PostConstruct
	private void init() {
		orderOperationsMapping.put(Order.Status.CANCELLED, M.msg(M.ORDER_OPERATION_CANCEL)); // TODO CHECK
		orderOperationsMapping.put(Order.Status.CREATED, M.msg(M.ORDER_OPERATION_SUBMIT)); // TODO CHECK
		//
		String airjet = normalizeProductCategory(Category.AIR_JET);
		String airtaxi = normalizeProductCategory(Category.AIR_TAXI);
		String airtrans = normalizeProductCategory(Category.AIR_TRANS);
		String airtour = normalizeProductCategory(Category.AIR_TOUR);
		String airtraining = normalizeProductCategory(Category.AIR_TRAINING);
		orderTypeMapping.put(Order.Type.FLEET, airjet);
		orderTypeMapping.put(Order.Type.FERRYFLIGHT, airjet);
		orderTypeMapping.put(Order.Type.JETTRAVEL, airjet);
		orderTypeMapping.put(Order.Type.AIRTAXI, airtaxi);
		orderTypeMapping.put(Order.Type.AIRTOUR, airtour);
		orderTypeMapping.put(Order.Type.AIRTRANSPORT, airtrans);
		orderTypeMapping.put(Order.Type.COURSE, airtraining);
		eventBus.register(this);
	}

	private static String normalizeProductCategory(Category category) {
		String[] parts = category.name().split("_");
		return new StringBuilder(Strings.capitalize(parts[0])).append(Strings.capitalize(parts[1])).toString();
	}

	@Subscribe
	private void onOrderEvent(OrderEvent event) {
		LOG.debug("Received order event: {}", event);
		switch (event.getType()) {
		case CREATION:
		case CANCELLATION:
			Set<Contact> clientManagers = getClientManagerContacts(event);
			doNotifyClientManager(clientManagers, event.getOrder());
			break;

		// fleet is offer by a tenant
		case FLEET_OFFERED:
			Contact contactPerson = event.getOrder().getContact();
			notifyCustomer(ImmutableSet.of(contactPerson), event);
			break;

		case PAYMENT:
		case REFUNDED:
		case REFUND_FAILED:
			// TODO send SMS to notify customer and tenant customer manager ?
			break;

		case UPDATE:
			break;

		case DELETION:
			break;

		default:// noop
		}
	}

	private Set<Contact> getClientManagerContacts(OrderEvent event) {
		Order order = event.getOrder();
		Product product = order.getProduct();
		ImmutableSet.Builder<Contact> contactsBuilder = ImmutableSet.builder();
		if (CharterOrder.class.isAssignableFrom(order.getClass())) {
			CharterOrder charterOrder = CharterOrder.class.cast(order);
			Set<FleetCandidate> candidates = charterOrder.getFleetCandidates();
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
		}
		else {
			contactsBuilder.addAll(product.getClientManagerContacts());
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
			// TODO make constants
			context.put("customer", customer);
			context.put("order", event.getOrder());
			context.put("action", getActionForOrderEvent(event));
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
			context.put("customer",
					Strings.isNotBlank(customer.getRealName()) ? customer.getRealName() : customer.getNickName());
			context.put("clientManager", clientManager.getPerson());
			context.put("orderNo", order.getOrderNo());
			context.put("orderOperation", orderOperationsMapping.get(order.getStatus()));
			context.put("orderType", orderTypeMapping.get(order.getType()));
			context.put("creationDate", order.getCreationDate());
			context.put("note", order.getNote());
			context.put("contact", order.getContact());
			context.put("company", configuration.getCompany());
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
					Set<FleetCandidate> fleetCandidates = charterOrder.getFleetCandidates();
					if (!fleetCandidates.isEmpty()) {
						aircraftType = fleetCandidates.stream().map(candidate -> candidate.getFleet().getAircraftType())
								.distinct().collect(Collectors.joining(", "));
					}
				}
				context.put("aircraftType", aircraftType);
				context.put("flightLegs", charterOrder.getFlightLegs());
				break;

			case FERRYFLIGHT:
				FerryFlightOrder ferryFlightOrder = FerryFlightOrder.class.cast(order);
				context.put("ferryFlight", ferryFlightOrder.getProduct());
				context.put("passengers", ferryFlightOrder.getPassengers());
				break;

			case JETTRAVEL:
				JetTravelOrder jetTravelOrder = JetTravelOrder.class.cast(order);
				context.put("jetTravel", jetTravelOrder.getProduct());
				break;

			case AIRTAXI:
				AirTaxiOrder airTaxiOrder = AirTaxiOrder.class.cast(order);
				context.put("airTaxi", airTaxiOrder.getProduct());
				context.put("passengers", airTaxiOrder.getPassengers());
				// XXX need aircraft info in salesPackageInfo, just add Aircraft name in getSalesPackageInfo()?
				context.put("aircraftType", airTaxiOrder.getSalesPackage().getAircraft().getName());
				context.put("date", airTaxiOrder.getDepartureDate());
				context.put("timeSlot", airTaxiOrder.getTimeSlot());
				break;

			case AIRTOUR:
				AirTourOrder airTourOrder = AirTourOrder.class.cast(order);
				context.put("airTour", airTourOrder.getProduct());
				context.put("passengers", airTourOrder.getPassengers());
				context.put("aircraftType", airTourOrder.getSalesPackage().getAircraft().getName());
				context.put("date", airTourOrder.getDepartureDate());
				context.put("timeSlot", airTourOrder.getTimeSlot());
				break;

			case AIRTRANSPORT:
				AirTransportOrder airTransportOrder = AirTransportOrder.class.cast(order);
				context.put("airTransport", airTransportOrder.getProduct());
				// TODO REMOVE
				// context.put("passengerNum", airTransportOrder.getPassengerNum() == 0
				// ? airTransportOrder.getPassengers().size() : airTransportOrder.getPassengerNum());
				context.put("passengers", airTransportOrder.getPassengers());
				context.put("aircraftType", airTransportOrder.getSalesPackage().getAircraft().getName());
				context.put("date", airTransportOrder.getDepartureDate());
				context.put("timeSlot", airTransportOrder.getTimeSlot());
				break;

			case COURSE:
				CourseOrder enrollment = CourseOrder.class.cast(order);
				context.put("enrollment", enrollment);
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
