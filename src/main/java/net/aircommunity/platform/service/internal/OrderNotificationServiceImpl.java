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
import net.aircommunity.platform.model.AirTaxiOrder;
import net.aircommunity.platform.model.AirTourOrder;
import net.aircommunity.platform.model.AirTransportOrder;
import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Contact;
import net.aircommunity.platform.model.Enrollment;
import net.aircommunity.platform.model.FerryFlightOrder;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.FleetCandidate;
import net.aircommunity.platform.model.JetCardOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Product.Category;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.FleetService;
import net.aircommunity.platform.service.MailService;
import net.aircommunity.platform.service.OrderNotificationService;
import net.aircommunity.platform.service.PlatformService;
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
		String airtraining = normalizeProductCategory(Category.AIR_TRAINING);
		orderTypeMapping.put(Order.Type.FLEET, airjet);
		orderTypeMapping.put(Order.Type.FERRYFLIGHT, airjet);
		orderTypeMapping.put(Order.Type.JETCARD, airjet);
		orderTypeMapping.put(Order.Type.AIRTAXI, airtaxi);
		orderTypeMapping.put(Order.Type.AIRTOUR, airtaxi);
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
		if (!configuration.isOrderEmailNotificationEnabled()) {
			LOG.warn("Order email notification is not enabled, cannot notify client manager for order {}.", event);
			return;
		}
		switch (event.getType()) {
		case CREATION:
		case CANCELLATION:
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
			notifyClientManager(contactsBuilder.build(), order);
			break;

		case UPDATE:
			break;

		case DELETION:
			break;

		default:// noop
		}
	}

	@Override
	public void notifyClientManager(Set<Contact> clientManagers, Order order) {
		if (!configuration.isOrderEmailNotificationEnabled()) {
			LOG.warn(
					"Order email notification is not enabled, cannot notify client managers {}, order notification ignored.",
					clientManagers);
			return;
		}
		LOG.debug("Notifying... clientManagers: {}", clientManagers);
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

			case JETCARD:
				JetCardOrder jetCardOrder = JetCardOrder.class.cast(order);
				context.put("jetCard", jetCardOrder.getProduct());
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
				Enrollment enrollment = Enrollment.class.cast(order);
				context.put("enrollment", enrollment);
				break;

			default:
			}
			//
			String mailBody = templateService.renderFile(String.format(Constants.TEMPLATE_MAIL_ORDER_NOTIFICATION,
					order.getType().name().toLowerCase(Locale.ENGLISH)), context);
			LOG.debug("Mail body: {}", mailBody);
			if (Strings.isNotBlank(clientManager.getEmail())) {
				mailService.sendMail(clientManager.getEmail(), configuration.getOrderEmailNotificationSubject(),
						mailBody);
				LOG.debug("Sent order notification {} to clientManager {}", order, clientManager);
			}
		}
	}
}
