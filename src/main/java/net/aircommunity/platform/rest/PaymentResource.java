package net.aircommunity.platform.rest;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Payment;
import net.aircommunity.platform.service.CommonOrderService;
import net.aircommunity.platform.service.PaymentService;

/**
 * Payment RESTful API.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("payment")
public class PaymentResource {
	private static final Logger LOG = LoggerFactory.getLogger(PaymentResource.class);

	// TODO refine payment logging
	private static final String PAYMENT_CONFIRMATION_STATUS = "status";

	@Resource
	private PaymentService paymentService;

	@Resource
	private CommonOrderService commonOrderService;

	/**
	 * Create payment request for a given gateway.
	 */
	@POST
	@Authenticated
	@Path("{paymentMethod}/sign/{orderNo}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaymentRequest createPaymentRequest(@PathParam("paymentMethod") Payment.Method paymentMethod,
			@PathParam("orderNo") String orderNo) {
		Order order = commonOrderService.findByOrderNo(orderNo);
		return paymentService.createPaymentRequest(paymentMethod, order);
	}

	/**
	 * On payment success, payment notification from client APP.
	 * 
	 * (e.g. Alipay, https://doc.open.alipay.com/doc2/detail.htm?treeId=204&articleId=105302&docType=1)
	 */

	/**
	 * Client notification JSON (APP)
	 */
	@POST
	@Path("{paymentMethod}/client/notify")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject notifyPaymentFromClient(@PathParam("paymentMethod") Payment.Method paymentMethod,
			@NotNull Map<String, Object> result) throws Exception {
		LOG.debug("[{}] payment notification(JSON) from client: {}", paymentMethod, result);
		PaymentVerification verification = paymentService.verifyClientPaymentNotification(paymentMethod,
				new PaymentNotification(result));
		LOG.debug("[{}] payment notification result verification result: {}", paymentMethod, verification);
		return Json.createObjectBuilder()
				.add(PAYMENT_CONFIRMATION_STATUS, verification.name().toLowerCase(Locale.ENGLISH)).build();
	}

	/**
	 * Client notification (return from gateway) RAW
	 */
	@POST
	@PermitAll
	@Path("{paymentMethod}/client/return")
	public void notifyPaymentFromClient(@PathParam("paymentMethod") Payment.Method paymentMethod,
			@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		Map<String, String> params = extractRequestParams(request);
		LOG.debug("[{}] payment notification(RAW) from client: {}", paymentMethod, params);
		PaymentVerification verification = paymentService.verifyClientPaymentNotification(paymentMethod,
				new PaymentNotification(params));
		LOG.debug("[{}] payment notification result verification result: {}", paymentMethod, verification);
		response.getWriter().write(verification.name());
		response.getWriter().flush();
	}

	/**
	 * On payment success, server notification from payment gateway (JSON)
	 */
	@POST
	@PermitAll
	@Path("{paymentMethod}/notify2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String notifyPaymentJson(@PathParam("paymentMethod") Payment.Method paymentMethod,
			Map<String, Object> result) throws Exception {
		LOG.debug("[{}] payment notification(JSON) from server: {}", paymentMethod, result);
		PaymentResponse paymentResponse = paymentService.processServerPaymentNotification(paymentMethod,
				new PaymentNotification(result));
		LOG.debug("[{}] payment response to server: {}", paymentMethod, paymentResponse);
		return paymentResponse.getBody();
	}

	/**
	 * On payment success, server notification from payment gateway (RAW)
	 */
	@POST
	@PermitAll
	@Path("{paymentMethod}/notify")
	public void notifyPaymentPlainText(@PathParam("paymentMethod") Payment.Method paymentMethod,
			@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		Map<String, String> params = extractRequestParams(request);
		LOG.debug("[{}] payment notification(RAW) from server: {}", paymentMethod, params);
		PaymentResponse paymentResponse = paymentService.processServerPaymentNotification(paymentMethod,
				new PaymentNotification(params));
		LOG.debug("[{}] payment response to server: {}", paymentMethod, paymentResponse);
		response.setStatus(paymentResponse.getCode());
		PrintWriter out = response.getWriter();
		String body = paymentResponse.getBody();
		if (Strings.isBlank(body)) {
			out.write(body);
		}
		out.flush();
		out.close();
	}

	private Map<String, String> extractRequestParams(HttpServletRequest request) {
		Map<String, String> params = new HashMap<>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = Joiner.on(",").join(values);
			// String valueStr = "";
			// for (int i = 0; i < values.length; i++) {
			// valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			// }
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		return params;
	}

}
