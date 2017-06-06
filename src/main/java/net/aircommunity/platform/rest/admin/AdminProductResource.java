package net.aircommunity.platform.rest.admin;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.ProductResourceSupport;
import net.aircommunity.platform.service.AirTaxiService;
import net.aircommunity.platform.service.AirTourService;
import net.aircommunity.platform.service.AirTransportService;
import net.aircommunity.platform.service.CommonProductService;
import net.aircommunity.platform.service.CourseService;
import net.aircommunity.platform.service.FerryFlightService;
import net.aircommunity.platform.service.FleetService;
import net.aircommunity.platform.service.ProductFamilyService;

/**
 * Product administrative RESTful API. NOTE: <b>all permission</b> for ADMIN
 * 
 * @author Bin.Zhang
 */
@RESTful
@RolesAllowed(Roles.ROLE_ADMIN)
public class AdminProductResource extends ProductResourceSupport<Product> {

	@Resource
	private CommonProductService commonProductService;

	@Resource
	private ProductFamilyService productFamilyService;

	@Resource
	private AirTaxiService airTaxiService;

	@Resource
	private AirTourService airTourService;

	@Resource
	private AirTransportService airTransportService;

	@Resource
	private FerryFlightService ferryFlightService;

	@Resource
	private FleetService fleetService;

	@Resource
	private CourseService courseService;

	/**
	 * List all families
	 */
	@POST
	@Path("families")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<ProductFamily> listProductFamilies(@QueryParam("approved") Boolean approved,
			@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
		if (approved == null) {
			return productFamilyService.listProductFamilies(page, pageSize);
		}
		return productFamilyService.listProductFamilies(approved, page, pageSize);
	}

	/**
	 * List product families count to be reviewed
	 */
	@GET
	@Path("families/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listProductFamiliesToBeApproved() {
		return buildCountResponse(productFamilyService.countProductFamilies(false));
	}

	/**
	 * List all taxis
	 */
	@POST
	@Path("taxis")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<AirTaxi> listTaxis(@QueryParam("approved") Boolean approved, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		if (approved == null) {
			return airTaxiService.listAirTaxis(page, pageSize);
		}
		return airTaxiService.listAirTaxis(approved, page, pageSize);
	}

	/**
	 * List taxis count to be reviewed
	 */
	@GET
	@Path("taxis/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listTaxisToBeApproved() {
		return buildCountResponse(airTaxiService.countAirTaxis(false));
	}

	/**
	 * List all Trans
	 */
	@POST
	@Path("transports")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<AirTransport> listTrans(@QueryParam("approved") Boolean approved, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		if (approved == null) {
			return airTransportService.listAirTransports(page, pageSize);
		}
		return airTransportService.listAirTransports(approved, page, pageSize);
	}

	/**
	 * List transports count to be reviewed
	 */
	@GET
	@Path("transports/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listTransportsToBeApproved() {
		return buildCountResponse(airTransportService.countAirTransports(false));
	}

	/**
	 * List all Tours
	 */
	@POST
	@Path("tours")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<AirTour> listTours(@QueryParam("approved") Boolean approved, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		if (approved == null) {
			return airTourService.listAirTours(page, pageSize);
		}
		return airTourService.listAirTours(approved, page, pageSize);
	}

	/**
	 * List tours count to be reviewed
	 */
	@GET
	@Path("tours/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listToursToBeApproved() {
		return buildCountResponse(airTourService.countAirTours(false));
	}

	/**
	 * List all ferryflights
	 */
	@POST
	@Path("ferryflights")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<FerryFlight> listFerryFlights(@QueryParam("approved") Boolean approved, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		if (approved == null) {
			return ferryFlightService.listFerryFlights(page, pageSize);
		}
		return ferryFlightService.listFerryFlights(approved, page, pageSize);
	}

	/**
	 * List ferryflights count to be reviewed
	 */
	@GET
	@Path("ferryflights/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listFerryFlightsToBeApproved() {
		return buildCountResponse(ferryFlightService.countFerryFlights(false));
	}

	/**
	 * List all fleets
	 */
	@POST
	@Path("fleets")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Fleet> listFleets(@QueryParam("approved") Boolean approved, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		if (approved == null) {
			return fleetService.listFleets(page, pageSize);
		}
		return fleetService.listFleets(approved, page, pageSize);
	}

	/**
	 * List fleets count to be reviewed
	 */
	@GET
	@Path("fleets/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listFleetsToBeApproved() {
		return buildCountResponse(fleetService.countFleets(false));
	}

	/**
	 * List all courses
	 */
	@POST
	@Path("courses")
	@Produces(MediaType.APPLICATION_JSON)
	public Page<Course> listCourses(@QueryParam("approved") Boolean approved, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		if (approved == null) {
			return courseService.listCourses(page, pageSize);
		}
		return courseService.listCourses(approved, page, pageSize);
	}

	/**
	 * List fleets count to be reviewed
	 */
	@GET
	@Path("courses/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listCoursesToBeApproved() {
		return buildCountResponse(courseService.countCourses(false));
	}

	/**
	 * Approve a tenant product
	 */
	@POST
	@Path("{productId}/approve")
	public void approveProduct(@PathParam("productId") String productId) {
		commonProductService.reviewProduct(productId, true, null);
	}

	/**
	 * Disapprove a tenant product
	 */
	@POST
	@Path("{productId}/disapprove")
	@Consumes(MediaType.APPLICATION_JSON)
	public void disapproveProduct(@PathParam("productId") String productId, JsonObject rejectedReason) {
		String reason = null;
		if (rejectedReason != null) {
			reason = rejectedReason.getString("reason");
		}
		commonProductService.reviewProduct(productId, false, reason);
	}

	private JsonObject buildCountResponse(long count) {
		return Json.createObjectBuilder().add("count", count).build();
	}

}
