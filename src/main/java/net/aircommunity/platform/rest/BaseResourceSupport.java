package net.aircommunity.platform.rest;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;

import io.micro.common.Strings;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.payment.PaymentService;
import net.aircommunity.platform.service.product.CommonProductService;

/**
 * Common RESTful API support
 * 
 * @author Bin.Zhang
 */
public abstract class BaseResourceSupport {

	private static final String JSON_PROP_POINTS = "points";
	private static final String JSON_PROP_COUNT = "count";
	private static final String JSON_PROP_REASON = "reason";
	private static final String JSON_PROP_RANK = "rank";
	private static final String JSON_PROP_STOCK = "stock";
	private static final String JSON_PROP_AMOUNT = "amount";
	private static final String JSON_PROP_STATUS = "status";
	private static final String JSON_PROP_ROLE = "role";
	private static final String JSON_PROP_FLEET_CANDIDATE = "candidate";

	/**
	 * Current used in APP, TODO REMOVE once APP is updated
	 * 
	 * @deprecated use JSON_PROP_REASON
	 */
	private static final String JSON_PROP_REFUNDREASON = "refundReason";

	@Resource
	protected CommonProductService commonProductService;

	@Resource(name = "commonOrderService")
	protected CommonOrderService commonOrderService;

	@Resource
	protected PaymentService paymentService;

	protected JsonObject buildCountResponse(long count) {
		return Json.createObjectBuilder().add(JSON_PROP_COUNT, count).build();
	}

	protected String getRole(JsonObject request) {
		return getJsonString(request, JSON_PROP_ROLE);
	}

	protected String getStatus(JsonObject request) {
		return getJsonString(request, JSON_PROP_STATUS);
	}

	protected String getOrderItemCandidate(JsonObject request) {
		return getJsonString(request, JSON_PROP_FLEET_CANDIDATE);
	}

	/**
	 * Reject reason, cancel, refund reason etc.
	 */
	protected String getReason(JsonObject request) { // TODO how to valid JsonObject using bval
		return getJsonString(request, JSON_PROP_REASON);
	}

	/**
	 * @deprecated use {@code #getReason(JsonObject)}
	 */
	protected String getRefundReason(JsonObject request) {
		String reason = getReason(request);
		if (Strings.isNotBlank(reason)) {
			return reason;
		}
		return getJsonString(request, JSON_PROP_REFUNDREASON);
	}

	protected long getPoints(JsonObject request) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(JSON_PROP_POINTS);
			return num == null ? 0 : num.longValue();
		}
		return 0;
	}

	protected BigDecimal getAmount(JsonObject request) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(JSON_PROP_AMOUNT);
			return num == null ? BigDecimal.ZERO : num.bigDecimalValue();
		}
		return BigDecimal.ZERO;
	}

	protected int getRank(JsonObject request) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(JSON_PROP_RANK);
			return num == null ? Product.DEFAULT_RANK : num.intValue();
			// NOTE: bad idea use this method (which uses Exception to control the logic)
			// request.getInt(JSON_PROP_RANK, Product.DEFAULT_RANK);
		}
		return Product.DEFAULT_RANK;
	}

	// product stock
	protected int getStock(JsonObject request) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(JSON_PROP_STOCK);
			return num == null ? Product.UNLIMITED_STOCK : num.intValue();
		}
		return Product.UNLIMITED_STOCK;
	}

	private String getJsonString(JsonObject request, String name) {
		if (request == null) {
			return null;
		}
		JsonString str = request.getJsonString(name);
		if (str == null) {
			return null;
		}
		return str.getString();
	}

	protected <T extends Order> T ensureOrderVisible(T order) {
		if (order.getStatus() == Order.Status.DELETED) {
			throw new AirException(Codes.ORDER_NOT_FOUND, M.msg(M.ORDER_NOT_FOUND));
		}
		return order;
	}

}
