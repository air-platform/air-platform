package net.aircommunity.platform.rest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;

import io.micro.annotation.RESTful;
import io.micro.common.Strings;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.domain.AirTaxi;
import net.aircommunity.platform.model.domain.AirTour;
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.model.domain.JetTravel;
import net.aircommunity.platform.service.product.AirTaxiService;
import net.aircommunity.platform.service.product.AirTourService;
import net.aircommunity.platform.service.product.AirTransportService;
import net.aircommunity.platform.service.product.CourseService;
import net.aircommunity.platform.service.product.FerryFlightService;
import net.aircommunity.platform.service.product.JetTravelService;

/**
 * Generic product search RESTful API allows list/find/query for ANYONE.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@PermitAll
@Path("search")
public class ProductSearchResource {
	private static final int SEARCH_RESULT_TOP_N = 10;
	private static final String PROP_TAXIS = "airtaxis";
	private static final String PROP_TOURS = "airtours";
	private static final String PROP_TRANSPORTS = "airtransports";
	private static final String PROP_FERRYFLIGHTS = "ferryflights";
	private static final String PROP_JETTRAVELS = "jettravels";
	private static final String PROP_COURSES = "courses";
	private static final Map<String, Object> EMPTY_SEARCH_RESULT = ImmutableMap.<String, Object> builder()
			.put(PROP_TAXIS, Collections.emptyList()).put(PROP_TRANSPORTS, Collections.emptyList())
			.put(PROP_FERRYFLIGHTS, Collections.emptyList()).put(PROP_COURSES, Collections.emptyList()).build();

	@Resource
	private AirTaxiService airTaxiService;

	@Resource
	private AirTourService airTourService;

	@Resource
	private AirTransportService airTransportService;

	@Resource
	private FerryFlightService ferryFlightService;

	@Resource
	private JetTravelService jetTravelService;

	@Resource
	private CourseService courseService;

	/**
	 * Search query (ONLY location for now)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Map<String, Object> search(@QueryParam("q") String query, @QueryParam("topN") @DefaultValue("0") int topN) {
		String location = query;
		if (Strings.isBlank(location)) {
			return EMPTY_SEARCH_RESULT;
		}
		int page = 1;
		int pageSize = topN <= 0 ? SEARCH_RESULT_TOP_N : topN;
		ImmutableMap.Builder<String, Object> result = ImmutableMap.builder();
		// taxi
		List<AirTaxi> taxis = airTaxiService.listAirTaxisByFuzzyLocation(location, page, pageSize).getContent();
		result.put(PROP_TAXIS, taxis);

		// tour
		List<AirTour> tours = airTourService.listAirToursByCity(location, page, pageSize).getContent();
		result.put(PROP_TOURS, tours);

		// trans
		List<AirTransport> trans = airTransportService.listAirTransportsByFuzzyLocation(location, page, pageSize)
				.getContent();
		result.put(PROP_TRANSPORTS, trans);

		// ferryFlights
		List<FerryFlight> ferryFlights = ferryFlightService.listFerryFlightsByFuzzyLocation(location, page, pageSize)
				.getContent();
		result.put(PROP_FERRYFLIGHTS, ferryFlights);

		// jettravels
		List<JetTravel> jettravels = jetTravelService.listJetTravelsByFuzzyName(location, page, pageSize).getContent();
		result.put(PROP_JETTRAVELS, jettravels);

		// courses
		List<Course> courses = courseService.listCoursesByLocation(location, page, pageSize).getContent();
		result.put(PROP_COURSES, courses);
		return result.build();
	}

}
