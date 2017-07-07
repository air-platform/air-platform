package net.aircommunity.platform.rest;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.micro.common.DateFormats;
import io.swagger.annotations.Api;

/**
 * Time service RESTful API used to allow client to sync time with server(act as NTP)
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("time")
public class TimeResource {
	private static final SimpleDateFormat SAFE_TIME_FORMATTER = DateFormats.simple("yyyy-MM-dd HH:mm:ss");

	/**
	 * Sync time
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject currentTime() {
		return Json.createObjectBuilder().add("time", SAFE_TIME_FORMATTER.format(new Date())).build();
	}

}
