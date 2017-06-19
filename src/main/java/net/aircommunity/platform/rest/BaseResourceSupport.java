package net.aircommunity.platform.rest;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;

import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.service.CommonProductService;

/**
 * Common RESTful API support
 * 
 * @author Bin.Zhang
 */
public abstract class BaseResourceSupport {

	private static final String JSON_PROP_COUNT = "count";
	private static final String JSON_PROP_REJECT_REASON = "reason";
	private static final String JSON_PROP_RANK = "rank";
	private static final String JSON_PROP_AMOUNT = "amount";
	private static final String JSON_PROP_TOTAL_AMOUNT = "totalAmount";

	@Resource
	protected CommonProductService commonProductService;

	protected JsonObject buildCountResponse(long count) {
		return Json.createObjectBuilder().add(JSON_PROP_COUNT, count).build();
	}

	protected BigDecimal getTotalAmount(JsonObject request) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(JSON_PROP_TOTAL_AMOUNT);
			return num == null ? BigDecimal.ZERO : num.bigDecimalValue();
		}
		return BigDecimal.ZERO;
	}

	protected BigDecimal getAmount(JsonObject request) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(JSON_PROP_AMOUNT);
			return num == null ? BigDecimal.ZERO : num.bigDecimalValue();
		}
		return BigDecimal.ZERO;
	}

	protected int getProductRank(JsonObject request) {
		if (request != null) {
			return request.getInt(JSON_PROP_RANK, Product.DEFAULT_RANK);
		}
		return Product.DEFAULT_RANK;
	}

	protected String getRejectedReason(JsonObject rejectedReason) {
		if (rejectedReason != null) {
			return rejectedReason.getString(JSON_PROP_REJECT_REASON);
		}
		return null;
	}

}
