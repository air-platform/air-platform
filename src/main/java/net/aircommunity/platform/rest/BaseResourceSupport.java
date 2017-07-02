package net.aircommunity.platform.rest;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;

import net.aircommunity.platform.model.domain.Product;
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
	private static final String JSON_PROP_REJECT_REASON = "reason";
	private static final String JSON_PROP_CANCEL_REASON = "reason";
	private static final String JSON_PROP_RANK = "rank";
	private static final String JSON_PROP_AMOUNT = "amount";
	private static final String JSON_PROP_STATUS = "status";
	private static final String JSON_PROP_FLEET_CANDIDATE = "candidate";

	@Resource
	protected CommonProductService commonProductService;

	protected JsonObject buildCountResponse(long count) {
		return Json.createObjectBuilder().add(JSON_PROP_COUNT, count).build();
	}

	protected String getStatus(JsonObject request) {
		return getJsonString(request, JSON_PROP_STATUS);
	}

	protected String getFleetCandidate(JsonObject request) {
		return getJsonString(request, JSON_PROP_FLEET_CANDIDATE);
	}

	protected String getReason(JsonObject request) {
		return getJsonString(request, JSON_PROP_REASON);
	}

	protected String getRejectedReason(JsonObject request) {
		return getJsonString(request, JSON_PROP_REJECT_REASON);
	}

	protected String getCancelReason(JsonObject request) {
		return getJsonString(request, JSON_PROP_CANCEL_REASON);
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

}
