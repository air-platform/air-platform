package net.aircommunity.platform.rest;

import java.math.BigDecimal;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;

/**
 * Common RESTful API support
 * 
 * @author Bin.Zhang
 */
public abstract class BaseResourceSupport {

	private static final String JSON_PROP_COUNT = "count";
	private static final String JSON_PROP_REJECT_REASON = "reason";
	private static final String JSON_PROP_AMOUNT = "amount";

	protected JsonObject buildCountResponse(long count) {
		return Json.createObjectBuilder().add(JSON_PROP_COUNT, count).build();
	}

	protected BigDecimal getAmount(JsonObject request) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(JSON_PROP_AMOUNT);
			return num == null ? BigDecimal.ZERO : num.bigDecimalValue();
		}
		return BigDecimal.ZERO;
	}

	protected String getRejectedReason(JsonObject rejectedReason) {
		if (rejectedReason != null) {
			return rejectedReason.getString(JSON_PROP_REJECT_REASON);
		}
		return null;
	}

}
