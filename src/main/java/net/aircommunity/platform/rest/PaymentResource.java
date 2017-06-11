package net.aircommunity.platform.rest;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.Authenticated;
import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Payment;
import net.aircommunity.platform.model.PaymentNotification;
import net.aircommunity.platform.model.PaymentRequest;
import net.aircommunity.platform.model.PaymentResponse;
import net.aircommunity.platform.model.PaymentVerification;
import net.aircommunity.platform.nls.M;
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
	public PaymentRequest signPaymentRequest(@PathParam("paymentMethod") Payment.Method paymentMethod,
			@PathParam("orderNo") String orderNo) {
		Order order = commonOrderService.findByOrderNo(orderNo);
		if (!order.isPayable()) {
			LOG.error("Order {} is NOT ready to pay", orderNo);
			throw new AirException(Codes.ORDER_NOT_PAYABLE, M.msg(M.ORDER_NOT_PAYABLE, orderNo));
		}
		return paymentService.createPaymentRequest(paymentMethod, orderNo);
	}

	/**
	 * On payment success, payment notification from client APP.
	 * 
	 * (e.g. Alipay, https://doc.open.alipay.com/doc2/detail.htm?treeId=204&articleId=105302&docType=1)
	 */
	@POST
	@Path("{paymentMethod}/client/notify")
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject notifyPaymentFromClient(@PathParam("paymentMethod") Payment.Method paymentMethod,
			@NotNull Map<String, Object> result) throws Exception {
		LOG.debug("{} payment notification result from client: {}", paymentMethod, result);
		PaymentVerification verification = paymentService.verifyClientPaymentNotification(paymentMethod,
				new PaymentNotification(result));
		return Json.createObjectBuilder()
				.add(PAYMENT_CONFIRMATION_STATUS, verification.name().toLowerCase(Locale.ENGLISH)).build();
	}

	/**
	 * On payment success, server notification from payment gateway (JSON)
	 */
	@POST
	@PermitAll
	@Path("{paymentMethod}/notify")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String notifyPaymentJson(@PathParam("paymentMethod") Payment.Method paymentMethod,
			Map<String, Object> result) throws Exception {
		PaymentResponse paymentResponse = paymentService.processServerPaymentNotification(paymentMethod,
				new PaymentNotification(result));
		LOG.debug("Payment response to server: {}", paymentResponse);
		return paymentResponse.getBody();
	}

	/**
	 * On payment success, server notification from payment gateway (TEXT)
	 */
	@POST
	@PermitAll
	@Path("{paymentMethod}/notify")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String notifyPaymentPlainText(@PathParam("paymentMethod") Payment.Method paymentMethod,
			@Context UriInfo uriInfo) throws Exception {
		// NOTE: no URL decoded here, will decode later
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(false/* decode */);
		Map<String, String> params = queryParams.keySet().stream()
				.map(name -> new SimpleEntry<String, String>(name,
						queryParams.get(name).stream().collect(Collectors.joining(","))))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		LOG.debug("Payment notification from server: {}", params);
		PaymentResponse paymentResponse = paymentService.processServerPaymentNotification(paymentMethod,
				new PaymentNotification(params));
		LOG.debug("Payment response to server: {}", paymentResponse);
		return paymentResponse.getBody();
	}

}
