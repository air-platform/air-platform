package net.aircommunity.platform.rest;

import io.micro.annotation.RESTful;
import io.micro.core.security.AccessTokenService;
import io.swagger.annotations.Api;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.aircommunity.platform.model.ApplicationParams;
import net.aircommunity.platform.model.Roles;
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

	@PUT
	@Path("quickflight")
	@RolesAllowed(Roles.ROLE_ADMIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject updateBasicPointsConfig(@NotNull JsonObject appConfig) {
		int speed = getInt(appConfig, JSON_PROP_QUICKFLIGHT_SPEED, ApplicationParams.DEFAULT_QUICKFLIGHT_SPEED);
		int price = getInt(appConfig, JSON_PROP_QUICKFLIGHT_UNIT_TIME_PRICE, ApplicationParams.DEFAULT_QUICKFLIGHT_UNIT_TIME_PRICE);
		int time = getInt(appConfig, JSON_PROP_QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE, ApplicationParams.DEFAULT_QUICKFLIGHT_DEPARTURE_TIME_IN_ADVANCE);

		applicationParamService.setQuickflightSpeed(speed);
		applicationParamService.setQuickflightUnitPrice(price);
		applicationParamService.setQuickflightDepartureTime(time);


		return appConfig;
	}

	private int getInt(JsonObject request, String name, int defaultValue) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(name);
			return num == null ? defaultValue : num.intValue();
		}
		return defaultValue;
	}
}

