package net.aircommunity.platform.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.CooperationIntention;
import net.aircommunity.platform.model.Feedback;
import net.aircommunity.platform.model.domain.Contact;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.PlatformService;
import net.aircommunity.platform.service.common.MailService;
import net.aircommunity.platform.service.common.TemplateService;

/**
 * Customer feedbacks RESTful API
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("customer-support")
public class CustomerSupportResource {
	private static final String TYPE_FEEDBACK = "feedback";
	private static final String TYPE_COOPERATION = "cooperation";
	private static final String CTX_FEEDBACK_TYPE = "feedbackType";
	private static final String CTX_FEEDBACK_TITLE = "feedbackTitle";
	private static final String CTX_FEEDBACK = "feedback";
	private static final String CTX_COMPANY = "company";
	private static final String CTX_FEEDBACK_CUSTOMER = "customer";

	@Resource
	private Configuration configuration;

	@Resource
	private PlatformService platformService;

	@Resource
	private TemplateService templateService;

	@Resource
	private MailService mailService;

	// TODO limit POST rate or use captcha

	/**
	 * Feedback
	 */
	@POST
	@Path("feedback")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendFeedback(@NotNull @Valid Feedback feedback) {
		Set<Contact> contacts = platformService.getPlatformClientManagers();
		if (contacts.isEmpty()) {
			return;
		}
		Map<String, Object> context = new HashMap<>();
		context.put(CTX_FEEDBACK_TYPE, TYPE_FEEDBACK);
		context.put(CTX_FEEDBACK_TITLE, M.msg(M.CUSTOMER_FEEDBACK));
		context.put(CTX_FEEDBACK_CUSTOMER, feedback.getPerson());
		context.put(CTX_FEEDBACK, feedback);
		context.put(CTX_COMPANY, configuration.getCompany());
		String mailBody = templateService.renderFile(Constants.TEMPLATE_FEEDBACK_NOTIFICATION, context);
		contacts.stream().forEach(contact -> {
			mailService.sendMail(contact.getEmail(),
					String.format("%s: %s", configuration.getCompany(), M.msg(M.CUSTOMER_FEEDBACK)), mailBody);
		});
	}

	/**
	 * Feedback
	 */
	@POST
	@Path("cooperation")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendCooperationIntention(@NotNull @Valid CooperationIntention intention) {
		Set<Contact> contacts = platformService.getPlatformClientManagers();
		if (contacts.isEmpty()) {
			return;
		}
		Map<String, Object> context = new HashMap<>();
		context.put(CTX_FEEDBACK_TYPE, TYPE_COOPERATION);
		context.put(CTX_FEEDBACK_TITLE, M.msg(M.CUSTOMER_COOPERATION_INTENTION));
		context.put(CTX_FEEDBACK_CUSTOMER, intention.getCompay());
		context.put(CTX_FEEDBACK, intention);
		context.put(CTX_COMPANY, configuration.getCompany());
		String mailBody = templateService.renderFile(Constants.TEMPLATE_FEEDBACK_NOTIFICATION, context);
		contacts.stream().forEach(contact -> {
			mailService.sendMail(contact.getEmail(),
					String.format("%s: %s", configuration.getCompany(), M.msg(M.CUSTOMER_COOPERATION_INTENTION)),
					mailBody);
		});
	}

	// TODO REMOVE
	public static void main(String arg[]) {
		// TemplateServiceImpl tmpl = new TemplateServiceImpl();
		// Feedback feedback = new Feedback();
		// feedback.setCategory("xx");
		// feedback.setContent("哈哈");
		// // feedback.setDepartureTime(new Date());
		// feedback.setEmail("test@ggg.com");
		// feedback.setPerson("锕3");
		// feedback.setPhone("0103395034903");
		// Map<String, Object> context = new HashMap<>();
		// context.put(CTX_FEEDBACK_TYPE, TYPE_COOPERATION);
		// context.put(CTX_FEEDBACK_TITLE, M.msg(M.CUSTOMER_COOPERATION_INTENTION));
		// context.put(CTX_FEEDBACK_CUSTOMER, feedback.getPerson());
		// context.put(CTX_FEEDBACK, feedback);
		// context.put(CTX_COMPANY, "AC");
		// String mailBody = tmpl.renderFile(Constants.TEMPLATE_FEEDBACK_NOTIFICATION, context);
		// System.out.println(mailBody);
	}

}
