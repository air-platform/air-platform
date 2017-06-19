package net.aircommunity.platform.rest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
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
import net.aircommunity.platform.model.domain.AirTransport;
import net.aircommunity.platform.model.domain.Course;
import net.aircommunity.platform.model.domain.FerryFlight;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.platform.service.AirTransportService;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.platform.service.FerryFlightService;

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
	private static final String PROP_TAXIS = "taxis";
	private static final String PROP_TRANSPORTS = "transports";
	private static final String PROP_FERRYFLIGHTS = "ferryflights";
	private static final String PROP_COURSES = "courses";
	private static final Map<String, Object> EMPTY_SEARCH_RESULT = ImmutableMap.<String, Object> builder()
			.put(PROP_TAXIS, Collections.emptyList()).put(PROP_TRANSPORTS, Collections.emptyList())
			.put(PROP_FERRYFLIGHTS, Collections.emptyList()).put(PROP_COURSES, Collections.emptyList()).build();

	@Resource
	private AirTaxiService airTaxiService;

	@Resource
	private AirTransportService airTransportService;

	@Resource
	private FerryFlightService ferryFlightService;

	@Resource
	private CourseService courseService;

	/**
	 * Search query (ONLY location for now)
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Public.class)
	public Map<String, Object> search(@QueryParam("q") String query) {
		String location = query;
		if (Strings.isBlank(location)) {
			return EMPTY_SEARCH_RESULT;
		}
		int page = 1;
		int pageSize = SEARCH_RESULT_TOP_N;
		ImmutableMap.Builder<String, Object> result = ImmutableMap.builder();
		// taxi
		List<AirTaxi> taxis = airTaxiService.searchAirTaxisByLocation(location, page, pageSize).getContent();
		result.put(PROP_TAXIS, taxis);

		// trans
		List<AirTransport> trans = airTransportService.searchAirTransportsByLocation(location, page, pageSize)
				.getContent();
		result.put(PROP_TRANSPORTS, trans);

		// ferryFlights
		List<FerryFlight> ferryFlights = ferryFlightService.searchFerryFlightsByLocation(location, page, pageSize)
				.getContent();
		result.put(PROP_FERRYFLIGHTS, ferryFlights);

		// courses
		List<Course> courses = courseService.listCoursesByLocation(location, page, pageSize).getContent();
		result.put(PROP_COURSES, courses);
		return result.build();
	}

}
