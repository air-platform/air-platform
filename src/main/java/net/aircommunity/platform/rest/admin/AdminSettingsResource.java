package net.aircommunity.platform.rest.admin;

import java.util.Base64;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.micro.annotation.constraint.NotEmpty;
import io.micro.core.security.AccessTokenService;
import net.aircommunity.platform.model.AccessToken;
import net.aircommunity.platform.model.PointRule;
import net.aircommunity.platform.model.PointRules;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.Contact;
import net.aircommunity.platform.service.MemberPointsService;
import net.aircommunity.platform.service.PlatformService;

/**
 * Settings RESTful API for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminSettingsResource {

	@Resource
	private PlatformService platformService;

	@Resource
	private MemberPointsService memberPointsService;

	@Resource
	private AccessTokenService accessTokenService;

	// **************************************
	// Platform generic settings
	// **************************************

	/**
	 * Get access token Secret
	 */
	@GET
	@Path("accesstoken/secret")
	@Consumes(MediaType.APPLICATION_JSON)
	public AccessToken getAccessTokenPublicKey() {
		String key = Base64.getEncoder().encodeToString(accessTokenService.getPublicKey().getEncoded());
		return new AccessToken(key);
	}

	/**
	 * set platform client managers
	 */
	@PUT
	@Path("client-managers")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPlatformClientManagers(@NotNull @Valid Set<Contact> clientManagers) {
		platformService.setPlatformClientManagers(clientManagers);
	}

	/**
	 * set platform client managers
	 */
	@GET
	@Path("client-managers")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Contact> getPlatformClientManagers() {
		return platformService.getPlatformClientManagers();
	}

	// **************************************
	// Member Points settings
	// **************************************
	private static final String JSON_PROP_EXCHANGE_RATE = "exchangeRate";
	private static final String JSON_PROP_EXCHANGE_PERCENT = "exchangePercent";

	@GET
	@Path("point/basics")
	@Consumes(MediaType.APPLICATION_JSON)
	public JsonObject getBasicPointsConfig() {
		return Json.createObjectBuilder().add(JSON_PROP_EXCHANGE_RATE, memberPointsService.getPointsExchangeRate())
				.add(JSON_PROP_EXCHANGE_PERCENT, memberPointsService.getPointsExchangePercent()).build();
	}

	@PUT
	@Path("point/basics")
	@Consumes(MediaType.APPLICATION_JSON)
	public JsonObject updateBasicPointsConfig(@NotNull JsonObject pointsConfig) {
		int exchangeRate = getInt(pointsConfig, JSON_PROP_EXCHANGE_RATE, PointRules.DEFAULT_EXCHANGE_RATE);
		int exchangePercent = getInt(pointsConfig, JSON_PROP_EXCHANGE_PERCENT, PointRules.DEFAULT_EXCHANGE_PERCENT);
		memberPointsService.setPointsExchangeRate(exchangeRate);
		memberPointsService.setPointsExchangePercent(exchangePercent);
		return pointsConfig;
	}

	@PUT
	@Path("point/rules")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Set<PointRule> updatePointsRules(@NotNull @NotEmpty Set<PointRule> rules) {
		return memberPointsService.setEarnPointRules(rules);
	}

	@GET
	@Path("point/rules")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<PointRule> getPointsRules() {
		return memberPointsService.getEarnPointRules();
	}

	private int getInt(JsonObject request, String name, int defaultValue) {
		if (request != null) {
			JsonNumber num = request.getJsonNumber(name);
			return num == null ? defaultValue : num.intValue();
		}
		return defaultValue;
	}

}
