package net.aircommunity.platform.rest;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.service.product.AirTransportService;
import net.aircommunity.platform.service.product.StandardProductService;

/**
 * AirTransport RESTful API allows list/find/query for ANYONE
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("transports")
public class AirTransportResource extends ProductResourceSupport<AirTransport> {
	private static final Logger LOG = LoggerFactory.getLogger(AirTransportResource.class);

	@Resource
	private AirTransportService airTransportService;

	// ***********************
	// ANYONE
	// ***********************

	/**
	 * List all families
	 */
	@GET
	@Path("flight/families")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public List<ProductFamily> listAllFamilies() {
		LOG.debug("List all air transport families");
		return airTransportService.listAirTransportFamilies();
	}

	/**
	 * List all flight arrivals from a departure
	 */
	@GET
	@Path("flight/arrivals")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> listArrivalsFromDeparture(@NotNull @QueryParam("family") String familyId,
			@NotNull @QueryParam("departure") String departure) {
		LOG.debug("List all air transport arrivals of family: {} for departure: {}", familyId, departure);
		return airTransportService.listArrivalsFromDeparture(familyId, departure);
	}

	/**
	 * List all flight departures to a arrival
	 */
	@GET
	@Path("flight/departures")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> listDeparturesToArrival(@NotNull @QueryParam("family") String familyId,
			@NotNull @QueryParam("arrival") String arrival) {
		LOG.debug("List all air transport arrivals of family: {} for departure: {}", familyId, arrival);
		return airTransportService.listDeparturesToArrival(familyId, arrival);
	}

	/**
	 * List all (query: family=xxx&departure=xxx&arrival=xxx&provider=xxx)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Page<AirTransport> listAll(@QueryParam("family") String familyId, @QueryParam("departure") String departure,
			@QueryParam("arrival") String arrival, @QueryParam("provider") String tenantId,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("List all air transports for familyId: {}, departure: {}, arrival: {}, tenant: {}", familyId,
				departure, arrival, tenantId);
		return airTransportService.listAirTransportsWithConditions(familyId, departure, arrival, tenantId, page,
				pageSize);
	}

	@Override
	protected StandardProductService<AirTransport> getProductService() {
		return airTransportService;
	}

	/**
	 * Find
	 */
	// TODO REMOVE
	// @GET
	// @Path("{transportId}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.Public.class)
	// public AirTransport find(@PathParam("transportId") String transportId) {
	// return airTransportService.findAirTransport(transportId);
	// }

}
