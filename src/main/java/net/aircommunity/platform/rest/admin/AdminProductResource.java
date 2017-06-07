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

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.AirTaxi;
import net.aircommunity.platform.model.AirTour;
import net.aircommunity.platform.model.AirTransport;
import net.aircommunity.platform.model.Course;
import net.aircommunity.platform.model.FerryFlight;
import net.aircommunity.platform.model.Fleet;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.ProductFamily;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
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
	@GET
	@Path("product/families")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<ProductFamily> listProductFamilies(@QueryParam("status") ReviewStatus reviewStatus,
			@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
		return productFamilyService.listAllProductFamilies(reviewStatus, page, pageSize);
	}

	/**
	 * List all families
	 */
	@GET
	@Path("product/families/{productFamilyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public ProductFamily findProductFamily(@PathParam("productFamilyId") String productFamilyId) {
		return productFamilyService.findProductFamily(productFamilyId);
	}

	/**
	 * List product families count to be reviewed
	 */
	@GET
	@Path("product/families/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listProductFamiliesToBeApproved(@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(productFamilyService.countAllProductFamilies(reviewStatus));
	}

	// *********
	// TAXI
	// *********

	/**
	 * List all taxis
	 */
	@GET
	@Path("product/taxis")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<AirTaxi> listTaxis(@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		return airTaxiService.listAllAirTaxis(reviewStatus, page, pageSize);
	}

	/**
	 * List taxis count to be reviewed
	 */
	@GET
	@Path("product/taxis/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listTaxisToBeApproved(@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(airTaxiService.countAllAirTaxis(reviewStatus));
	}

	// *********
	// TRANS
	// *********

	/**
	 * List all Trans
	 */
	@GET
	@Path("product/transports")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<AirTransport> listTrans(@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		return airTransportService.listAllAirTransports(reviewStatus, page, pageSize);
	}

	/**
	 * List transports count to be reviewed
	 */
	@GET
	@Path("product/transports/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listTransportsToBeApproved(@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(airTransportService.countAllAirTransports(reviewStatus));
	}

	// *********
	// TOURS
	// *********
	/**
	 * List all Tours
	 */
	@GET
	@Path("product/tours")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<AirTour> listTours(@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		return airTourService.listAllAirTours(reviewStatus, page, pageSize);
	}

	/**
	 * List tours count to be reviewed
	 */
	@GET
	@Path("product/tours/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listToursToBeApproved(@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(airTourService.countAllAirTours(reviewStatus));
	}

	// *********
	// FERRYFLIGHTS
	// *********
	/**
	 * List all ferryflights
	 */
	@GET
	@Path("product/ferryflights")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<FerryFlight> listFerryFlights(@QueryParam("status") ReviewStatus reviewStatus,
			@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
		return ferryFlightService.listAllFerryFlights(reviewStatus, page, pageSize);
	}

	/**
	 * List ferryflights count to be reviewed
	 */
	@GET
	@Path("product/ferryflights/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listFerryFlightsToBeApproved(@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(ferryFlightService.countAllFerryFlights(reviewStatus));
	}

	// *********
	// FLEETS
	// *********
	/**
	 * List all fleets
	 */
	@GET
	@Path("product/fleets")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<Fleet> listFleets(@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		return fleetService.listAllFleets(reviewStatus, page, pageSize);
	}

	/**
	 * List fleets count to be reviewed
	 */
	@GET
	@Path("product/fleets/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listFleetsToBeApproved(@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(fleetService.countAllFleets(reviewStatus));
	}

	// *********
	// COURSES
	// *********
	/**
	 * List all courses
	 */
	@GET
	@Path("product/courses")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.Admin.class)
	public Page<Course> listCourses(@QueryParam("status") ReviewStatus reviewStatus, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize) {
		return courseService.listAllCourses(reviewStatus, page, pageSize);
	}

	/**
	 * List fleets count to be reviewed
	 */
	@GET
	@Path("product/courses/review/count")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonObject listCoursesToBeApproved(@QueryParam("status") ReviewStatus reviewStatus) {
		return buildCountResponse(courseService.countAllCourses(reviewStatus));
	}

	// *********
	// Products
	// *********

	/**
	 * Get
	 */
	@POST
	@Path("products/{productId}")
	public Product findProduct(@PathParam("productId") String productId) {
		return commonProductService.findProduct(productId);
	}

	// *********
	// Review
	// *********

	/**
	 * Approve a tenant product
	 */
	@POST
	@Path("products/{productId}/approve")
	public void approveProduct(@PathParam("productId") String productId) {
		commonProductService.reviewProduct(productId, ReviewStatus.APPROVED, null);
	}

	/**
	 * Disapprove a tenant product
	 */
	@POST
	@Path("products/{productId}/disapprove")
	@Consumes(MediaType.APPLICATION_JSON)
	public void disapproveProduct(@PathParam("productId") String productId, JsonObject rejectedReason) {
		String reason = null;
		if (rejectedReason != null) {
			reason = rejectedReason.getString("reason");
		}
		commonProductService.reviewProduct(productId, ReviewStatus.REJECTED, reason);
	}

	private JsonObject buildCountResponse(long count) {
		return Json.createObjectBuilder().add("count", count).build();
	}

}
