package net.aircommunity.platform.rest;

import static net.aircommunity.platform.model.payment.Payments.KEY_PAYMENT_METHOD;
import static net.aircommunity.platform.model.payment.Payments.PAYMENT_CONFIRMATION_STATUS;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.base.Joiner;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.model.domain.Trade;
import net.aircommunity.platform.model.payment.PaymentNotification;
import net.aircommunity.platform.model.payment.PaymentResponse;
import net.aircommunity.platform.model.payment.PaymentVerification;
import net.aircommunity.platform.model.payment.Payments;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.payment.PaymentService;
import net.aircommunity.platform.service.payment.PaymentSynchronizer;

/**
 * Payment RESTful API.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("payment")
public class PaymentResource {
	private static final Logger LOG = LoggerFactory.getLogger(Payments.LOGGER_NAME);

	@Resource
	private PaymentService paymentService;

	@Resource
	private CommonOrderService commonOrderService;

	@Resource
	private PaymentSynchronizer paymentSynchronizer;

	/**
	 * Client notification JSON (APP). On payment success, payment notification from client APP.
	 * 
	 * (e.g. Alipay, https://doc.open.alipay.com/doc2/detail.htm?treeId=204&articleId=105302&docType=1)
	 */
	@POST
	@Path("{paymentMethod}/client/notify")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject notifyPaymentFromClient(@PathParam("paymentMethod") Trade.Method method,
			@NotNull Map<String, Object> result) throws Exception {
		MDC.put(KEY_PAYMENT_METHOD, method.name());
		try {
			LOG.info("Payment notification(JSON) from client: {}", result);
			PaymentVerification verification = paymentService.verifyClientNotification(method,
					new PaymentNotification(result));
			LOG.info("Payment notification verification result: {}", verification);
			return Json.createObjectBuilder()
					.add(PAYMENT_CONFIRMATION_STATUS, verification.name().toLowerCase(Locale.ENGLISH)).build();
		}
		finally {
			MDC.remove(KEY_PAYMENT_METHOD);
		}
	}

	/**
	 * Client notification (return from gateway) RAW
	 */
	@POST
	@PermitAll
	@Path("{paymentMethod}/client/return")
	public void notifyPaymentFromClient(@PathParam("paymentMethod") Trade.Method method,
			@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		MDC.put(KEY_PAYMENT_METHOD, method.name());
		Map<String, String> params = extractRequestParams(request);
		PrintWriter out = response.getWriter();
		try {
			LOG.debug("Payment notification(RAW) from client: {}", params);
			PaymentVerification verification = paymentService.verifyClientNotification(method,
					new PaymentNotification(params));
			LOG.debug("Payment notification verification result: {}", verification);
			out.write(verification.name());
		}
		finally {
			out.flush();
			MDC.remove(KEY_PAYMENT_METHOD);
		}
	}

	/**
	 * ALIPAY: On payment success, server notification from payment gateway (RAW)
	 */
	@POST
	@PermitAll
	@Path("alipay/notify")
	public void notifyPaymentAlipay(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws Exception {
		Map<String, String> params = extractRequestParams(request);
		handleNotification(Payment.Method.ALIPAY, new PaymentNotification(params), response);
	}

	/**
	 * NEWPAY: On payment success, server notification from payment gateway (RAW)
	 */
	@POST
	@PermitAll
	@Path("newpay/notify")
	public void notifyPaymentNewpay(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws Exception {
		Map<String, String> params = extractRequestParams(request);
		handleNotification(Trade.Method.NEWPAY, new PaymentNotification(params), response);
	}

	/**
	 * WECHAT: On payment success, server notification from payment gateway (RAW)
	 */
	@POST
	@PermitAll
	@Path("wechat/notify")
	public void notifyPaymentWechat(String data, @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws Exception {
		handleNotification(Trade.Method.WECHAT, new PaymentNotification(data), response);
	}

	private void handleNotification(Trade.Method method, PaymentNotification notification, HttpServletResponse response)
			throws IOException {
		MDC.put(KEY_PAYMENT_METHOD, method.name());
		PrintWriter out = response.getWriter();
		try {
			LOG.debug("Payment notification(RAW) data from server: {}", notification.getData());
			PaymentResponse paymentResponse = paymentService.processServerNotification(method, notification);
			LOG.debug("Payment response to server: {}", method, paymentResponse);
			response.setStatus(paymentResponse.getStatus().getHttpStatusCode());
			out.println(paymentResponse.getMessage());
		}
		finally {
			out.flush();
			MDC.remove(KEY_PAYMENT_METHOD);
		}
	}

	private Map<String, String> extractRequestParams(HttpServletRequest request) {
		Map<String, String> params = new HashMap<>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = Joiner.on(",").join(values);
			params.put(name, valueStr);
		}
		return params;
	}

	// *****************
	// ADMIN ONLY
	// *****************
	/**
	 * Sync payment
	 */
	@POST
	@Path("sync")
	@RolesAllowed(Roles.ROLE_ADMIN)
	public void syncPayments(@QueryParam("days") @DefaultValue("0") int days) {
		paymentSynchronizer.syncPayments(days);
	}

	// TODO FIXME why this works only called directly from orderRefRepository, NOT working if its called from a service
	// just keep it for reference
	// @Resource
	// private Configuration configuration;
	//
	// @Resource
	// private OrderRefRepository orderRefRepository;
	//
	// /**
	// * Sync payment with data streaming
	// */
	// @POST
	// @Path("sync2")
	// @PermitAll
	// @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	// public void syncPaymentsStreaming() {
	// Date today = Date.from(LocalDate.now().atStartOfDay(configuration.getZoneId()).toInstant());
	// // payment
	// try (Stream<OrderRef> paymentPendingOrders = orderRefRepository.findPaymentPendingOrders(today)) {
	// paymentPendingOrders.forEach(orderRef -> {
	// LOG.debug("Test sync: {}", orderRef);
	// });
	// }
	// }

	// @POST
	// @PermitAll
	// @Path("{paymentMethod}/notify")
	// public void notifyPaymentPlainText(@PathParam("paymentMethod") Payment.Method paymentMethod, String data,
	// @Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
	// Map<String, String> params = extractRequestParams(request);
	// Object notificationData = Payment.Method.WECHAT == paymentMethod ? data : params;
	// LOG.debug("Payment notification(RAW) params from server: {}", params);
	// LOG.debug("Payment notification(RAW) data from server: {}", data);
	// PaymentResponse paymentResponse = paymentService.processServerPaymentNotification(paymentMethod,
	// new PaymentNotification(notificationData));
	// LOG.debug("Payment response to server: {}", paymentResponse);
	// PrintWriter out = response.getWriter();
	// response.setStatus(paymentResponse.getStatus().getHttpStatusCode());
	// out.println(paymentResponse.getMessage());
	// out.flush();
	// out.close();
	// }

	// @POST
	// @PermitAll
	// @Path("{paymentMethod}/notify-plain")
	// public void notifyPaymentPlainTextBody(@PathParam("paymentMethod") Payment.Method paymentMethod, String data,
	// @Context HttpServletResponse response) throws Exception {
	// LOG.debug("Payment notification(RAW PLAIN) from server: {}", data);
	// PaymentResponse paymentResponse = paymentService.processServerPaymentNotification(paymentMethod,
	// new PaymentNotification(data));
	// LOG.debug("Payment response to server: {}", paymentResponse);
	// PrintWriter out = response.getWriter();
	// response.setStatus(paymentResponse.getStatus().getHttpStatusCode());
	// out.println(paymentResponse.getMessage());
	// out.flush();
	// out.close();
	// }

	/**
	 * On payment success, server notification from payment gateway (JSON) (NOT USED)
	 */
	// @POST
	// @PermitAll
	// @Path("{paymentMethod}/notify2")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.TEXT_PLAIN)
	// public String notifyPaymentJson(@PathParam("paymentMethod") Payment.Method paymentMethod,
	// Map<String, Object> result) throws Exception {
	// LOG.debug("Payment notification(JSON) from server: {}", result);
	// PaymentResponse paymentResponse = paymentService.processServerPaymentNotification(paymentMethod,
	// new PaymentNotification(result));
	// LOG.debug("Payment response to server: {}", paymentResponse);
	// return paymentResponse.getBody();
	// }

}
