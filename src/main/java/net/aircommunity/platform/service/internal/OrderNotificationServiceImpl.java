package net.aircommunity.platform.service.internal;

import java.util.Collections;
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

import com.google.common.collect.Maps;
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
import net.aircommunity.platform.model.FleetCandidate;
import net.aircommunity.platform.model.JetCardOrder;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.User;
import net.aircommunity.platform.service.FleetService;
import net.aircommunity.platform.service.MailService;
import net.aircommunity.platform.service.OrderNotificationService;
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
	private static final String ORDER_TYPE_AIRJET = "Air Jet";
	private static final String ORDER_TYPE_AIRTAXI = "Air Taxi";
	private static final String ORDER_TYPE_AIRTRANSPORTION = "Air Transportion";
	private static final String ORDER_TYPE_AIRTRAINING = "Air Training";
	// i18n TODO
	private static final String ORDER_OPERATION_SUBMIT = "提交";
	private static final String ORDER_OPERATION_CANCEL = "取消";

	@Resource
	private Configuration configuration;

	@Resource
	private TemplateService templateService;

	@Resource
	private MailService mailService;

	@Resource
	private FleetService fleetService;

	@Resource
	private EventBus eventBus;

	@PostConstruct
	private void init() {
		orderOperationsMapping.put(Order.Status.CANCELLED, ORDER_OPERATION_CANCEL);
		orderOperationsMapping.put(Order.Status.PENDING, ORDER_OPERATION_SUBMIT);
		//
		orderTypeMapping.put(Order.Type.FLEET, ORDER_TYPE_AIRJET);
		orderTypeMapping.put(Order.Type.FERRYFLIGHT, ORDER_TYPE_AIRJET);
		orderTypeMapping.put(Order.Type.JETCARD, ORDER_TYPE_AIRJET);
		orderTypeMapping.put(Order.Type.AIRTAXI, ORDER_TYPE_AIRTAXI);
		orderTypeMapping.put(Order.Type.AIRTOUR, ORDER_TYPE_AIRTAXI);
		orderTypeMapping.put(Order.Type.AIRTRANSPORT, ORDER_TYPE_AIRTRANSPORTION);
		orderTypeMapping.put(Order.Type.COURSE, ORDER_TYPE_AIRTRAINING);
		eventBus.register(this);
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
			Set<Contact> contacts = Collections.emptySet();
			if (CharterOrder.class.isAssignableFrom(order.getClass())) {
				CharterOrder charterOrder = CharterOrder.class.cast(order);
				Set<FleetCandidate> candidates = charterOrder.getFleetCandidates();
				if (candidates.isEmpty()) {
					contacts = fleetService.listFleets().stream()
							.flatMap(fleet -> fleet.getClientManagerContacts().stream()).collect(Collectors.toSet());
				}
				else {
					contacts = candidates.stream()
							.flatMap(candidate -> candidate.getFleet().getClientManagerContacts().stream())
							.collect(Collectors.toSet());
				}
			}
			else {
				contacts = product.getClientManagerContacts();
			}
			notifyClientManager(contacts, order);
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
			LOG.warn("Order email notification is not enabled, cannot notify client manager {}, skipped",
					clientManagers);
			return;
		}
		LOG.debug("Notifying... clientManagers: {}", clientManagers);
		// TODO make constants
		for (Contact clientManager : clientManagers) {
			Map<String, Object> context = Maps.newHashMap();
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
			context.put("company", configuration.getCompany());
			//
			switch (order.getType()) {
			case FLEET:
				CharterOrder charterOrder = CharterOrder.class.cast(order);
				String aircraftType = "";
				Set<FleetCandidate> fleetCandidates = charterOrder.getFleetCandidates();
				if (!fleetCandidates.isEmpty()) {
					aircraftType = fleetCandidates.iterator().next().getFleet().getAircraftType();
				}
				context.put("flightLegs", charterOrder.getFlightLegs());
				context.put("aircraftType", aircraftType);
				context.put("contact", charterOrder.getContact());
				break;

			case FERRYFLIGHT:
				FerryFlightOrder ferryFlightOrder = FerryFlightOrder.class.cast(order);
				context.put("contact", ferryFlightOrder.getContact());
				context.put("ferryFlight", ferryFlightOrder.getFerryFlight());
				context.put("passengers", ferryFlightOrder.getPassengers());
				break;

			case JETCARD:
				JetCardOrder jetCardOrder = JetCardOrder.class.cast(order);
				context.put("jetCard", jetCardOrder.getJetCard());
				context.put("contact", jetCardOrder.getContact());
				break;

			case AIRTAXI:
				AirTaxiOrder airTaxiOrder = AirTaxiOrder.class.cast(order);
				context.put("airTaxi", airTaxiOrder.getAirTaxi());
				context.put("passengers", airTaxiOrder.getPassengers());
				context.put("aircraftType", airTaxiOrder.getAircraftItem().getAircraft().getName()); // XXX
				context.put("date", airTaxiOrder.getDate());
				context.put("timeSlot", airTaxiOrder.getTimeSlot());
				context.put("contact", airTaxiOrder.getContact());
				break;

			case AIRTOUR:
				AirTourOrder airTourOrder = AirTourOrder.class.cast(order);
				context.put("airTour", airTourOrder.getAirTour());
				context.put("passengers", airTourOrder.getPassengers());
				context.put("aircraftType", airTourOrder.getAircraftItem().getAircraft().getName());
				context.put("date", airTourOrder.getDate());
				context.put("timeSlot", airTourOrder.getTimeSlot());
				context.put("contact", airTourOrder.getContact());
				break;

			case AIRTRANSPORT:
				AirTransportOrder airTransportOrder = AirTransportOrder.class.cast(order);
				context.put("airTransport", airTransportOrder.getAirTransport());
				context.put("passengerNum", airTransportOrder.getPassengerNum() == 0
						? airTransportOrder.getPassengers().size() : airTransportOrder.getPassengerNum());
				context.put("passengers", airTransportOrder.getPassengers());
				context.put("aircraftType", airTransportOrder.getAircraftItem().getAircraft().getName());
				context.put("date", airTransportOrder.getDate());
				context.put("timeSlot", airTransportOrder.getTimeSlot());
				context.put("contact", airTransportOrder.getContact());
				break;

			case COURSE:
				Enrollment enrollment = Enrollment.class.cast(order);
				context.put("enrollment", enrollment);
				context.put("contact", enrollment.getContact());
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
