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

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import io.micro.common.Strings;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.model.CharterOrder;
import net.aircommunity.platform.model.Contact;
import net.aircommunity.platform.model.FleetCandidate;
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
	private static final String ORDER_TYPE_AIRJET = "Air Jet";
	private static final String ORDER_TYPE_AIRTAXI = "Air Taxi";
	private static final String ORDER_TYPE_AIRTRANSPORTION = "Air Transportion";
	private static final String ORDER_TYPE_AIRTRAINING = "Air Training";

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
					for (FleetCandidate c : candidates) {
						System.out.println(c.getFleet());
						System.out.println(c.getFleet().getClientManagers());
						System.out.println(c.getFleet().getClientManagerContacts());
					}
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

		for (Contact clientManager : clientManagers) {
			Map<String, Object> context = Maps.newHashMap();
			User customer = order.getOwner();
			// use realName if available, otherwise use nickName
			context.put("customer",
					Strings.isNotBlank(customer.getRealName()) ? customer.getRealName() : customer.getNickName());
			context.put("clientManager", clientManager.getPerson());
			context.put("orderNo", order.getOrderNo());
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
				break;

			case JETCARD:
				break;

			case AIRTAXI:
				break;

			case AIRTOUR:
				break;

			case AIRTRANSPORT:
				break;

			case COURSE:
				break;

			default:
			}
			//
			String mailBody = templateService.renderFile(String.format(Constants.TEMPLATE_MAIL_ORDER_NOTIFICATION,
					order.getType().name().toLowerCase(Locale.ENGLISH)), context);
			mailService.sendMail(clientManager.getEmail(), configuration.getOrderEmailNotificationSubject(), mailBody);
		}
	}

	public static void main(String[] args) {
		// bindings.put(EMAIL_BINDING_USERNAME, account.getNickName());
		// bindings.put(EMAIL_BINDING_COMPANY, configuration.getCompany());
		// bindings.put(EMAIL_BINDING_WEBSITE, configuration.getWebsite());
		// bindings.put(EMAIL_BINDING_VERIFICATIONLINK, verificationLink);
		// 170421NNL6P2QG4400
		// 1704216HXO4QM7XMO0
		System.out.println(new OrderNoGenerator(1, 1).next());
		String s = "user1  : ,e@a.com,   user2:a@b.com";
		System.out.println(Splitter.on(",").trimResults().omitEmptyStrings().withKeyValueSeparator(":").split(s));
	}

}
