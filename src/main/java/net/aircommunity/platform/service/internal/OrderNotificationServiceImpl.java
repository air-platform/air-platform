package net.aircommunity.platform.service.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.common.OrderNoGenerator;
import net.aircommunity.platform.model.Contact;
import net.aircommunity.platform.model.Order;
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

	@Resource
	private Configuration configuration;

	@Resource
	private TemplateService templateService;

	@Resource
	private MailService mailService;

	@Override
	public void notifyClientManager(Set<Contact> clientManagers, Order order) {
		if (!configuration.isOrderEmailNotificationEnabled()) {
			LOG.warn("Order email notification is not enabled, cannot notify client manager {}, skipped",
					clientManagers);
			return;
		}
		// TODO
		Map<String, Object> bindings = new HashMap<>(4);
		// bindings.put(EMAIL_BINDING_USERNAME, account.getNickName());
		// bindings.put(EMAIL_BINDING_COMPANY, configuration.getCompany());
		// bindings.put(EMAIL_BINDING_WEBSITE, configuration.getWebsite());
		// bindings.put(EMAIL_BINDING_VERIFICATIONLINK, verificationLink);
		String mailBody = templateService.renderFile(Constants.TEMPLATE_MAIL_VERIFICATION, bindings);
		for (Contact contact : clientManagers) {
			String subject = "AirCommunity客户订单通知" + contact.getPerson();
			mailService.sendMail(contact.getEmail(), subject, mailBody);
		}
	}

	public static void main(String[] args) {
		// 170421NNL6P2QG4400
		// 1704216HXO4QM7XMO0
		System.out.println(new OrderNoGenerator(1, 1).next());
		String s = "user1  : ,e@a.com,   user2:a@b.com";
		System.out.println(Splitter.on(",").trimResults().omitEmptyStrings().withKeyValueSeparator(":").split(s));
	}

}
