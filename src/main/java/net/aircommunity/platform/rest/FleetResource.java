package net.aircommunity.platform.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.FleetProvider;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Fleet;
import net.aircommunity.platform.service.FleetService;

/**
 * Fleet RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("fleets")
public class FleetResource extends ProductResourceSupport<Fleet> {
	private static final Logger LOG = LoggerFactory.getLogger(FleetResource.class);

	@Resource
	private FleetService fleetService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<Fleet> listAll(@QueryParam("type") String aircraftType, @QueryParam("provider") String provider,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all fleets aircraftType: {}, provider: {}", aircraftType, provider);
		if (!Strings.isBlank(aircraftType)) {
			if (!Strings.isBlank(provider)) {
				return fleetService.listFleets(aircraftType, provider, page, pageSize);
			}
			return fleetService.listFleetsByType(aircraftType, page, pageSize);
		}
		if (!Strings.isBlank(provider)) {
			return fleetService.listFleetsByProvider(provider, page, pageSize);
		}
		// both null
		return fleetService.listFleets(page, pageSize);
	}

	@GET
	@Path("providers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FleetProvider> listFleetProviders() {
		return fleetService.listFleetProviders();
	}

	/**
	 * Find
	 */
	@GET
	@Path("{fleetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Fleet find(@PathParam("fleetId") String fleetId) {
		return fleetService.findFleet(fleetId);
	}

	/**
	 * Find by flightNo
	 */
	@GET
	@Path("query/flightno/{flightNo}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Fleet findFlightNo(@PathParam("flightNo") String flightNo) {
		return fleetService.findFleetByFlightNo(flightNo);
	}

}
