package net.aircommunity.platform.rest;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Common RESTful API support
 * 
 * @author Bin.Zhang
 */
public abstract class BaseResourceSupport {

	protected JsonObject buildCountResponse(long count) {
		return Json.createObjectBuilder().add("count", count).build();
	}

}
