package net.aircommunity.platform.rest;

import io.micro.annotation.RESTful;
import io.micro.core.security.AccessTokenService;
import io.swagger.annotations.Api;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.aircommunity.platform.service.ApplicationParamService;
import net.aircommunity.platform.service.PlatformService;

/**
 * Application params RESTful API for ADMIN
 *
 * @author Xiangwen.kong
 */
@Api
@RESTful
@PermitAll
@Path("application-params")
public class ApplicationParamResource {

	@Resource
	private PlatformService platformService;

	@Resource
	private ApplicationParamService applicationParamService;

	@Resource
	private AccessTokenService accessTokenService;

	// **************************************
	// Platform quick flight settings
	// **************************************

	private static final String JSON_PROP_QUICKFLIGHT_SPEED = "speed";
	private static final String JSON_PROP_QUICKFLIGHT_UNIT_TIME_PRICE = "unitTimePrice";
	private static final String JSON_PROP_QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE = "departureTimeInAdvance";

	@GET
	@Path("quickflight")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject getQuickflight() {
		return Json.createObjectBuilder().add(JSON_PROP_QUICKFLIGHT_SPEED, applicationParamService.getQuickflightSpeed())
				.add(JSON_PROP_QUICKFLIGHT_UNIT_TIME_PRICE, applicationParamService.getQuickflightUnitPrice())
				.add(JSON_PROP_QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE, applicationParamService.getQuickflightDepartureTime()).build();
	}
}
