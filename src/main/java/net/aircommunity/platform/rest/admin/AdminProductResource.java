package net.aircommunity.platform.rest.admin;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.model.Reviewable.ReviewStatus;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.rest.ProductResourceSupport;
import net.aircommunity.platform.service.CommonProductService;

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

	// *********
	// AIRCRAFT
	// *********
	@Resource
	private AdminAircraftResource adminAircraftResource;

	@Path("aircrafts")
	public AdminAircraftResource aircrafts() {
		return adminAircraftResource;
	}

	// *********
	// FAMILY
	// *********
	@Resource
	private AdminProductFamilyResource adminProductFamilyResource;

	@Path("product/families")
	public AdminProductFamilyResource families() {
		return adminProductFamilyResource;
	}

	// *********
	// TAXI
	// *********
	@Resource
	private AdminAirTaxiResource adminAirTaxiResource;

	@Path("product/taxis")
	public AdminAirTaxiResource taxis() {
		return adminAirTaxiResource;
	}

	// *********
	// TOUR
	// *********
	@Resource
	private AdminAirTourResourse adminAirTourResourse;

	@Path("product/tours")
	public AdminAirTourResourse tours() {
		return adminAirTourResourse;
	}

	// *********
	// TRANPORT
	// *********
	@Resource
	private AdminAirTransportResource adminAirTransportResource;

	@Path("product/tranports")
	public AdminAirTransportResource tranports() {
		return adminAirTransportResource;
	}

	// *********
	// FERRYFLIGHT
	// *********
	@Resource
	private AdminFerryFlightResource adminFerryFlightResource;

	@Path("product/ferryflights")
	public AdminFerryFlightResource ferryflights() {
		return adminFerryFlightResource;
	}

	// *********
	// FLEET
	// *********
	@Resource
	private AdminFleetResource adminFleetResource;

	@Path("product/fleets")
	public AdminFleetResource fleets() {
		return adminFleetResource;
	}

	// *********
	// COURSE
	// *********
	@Resource
	private AdminCourseResource adminCourseResource;

	@Path("product/courses")
	public AdminCourseResource courses() {
		return adminCourseResource;
	}

	// **************
	// Review Product
	// **************

	@POST
	@Path("products/{productId}")
	public Product findProduct(@PathParam("productId") String productId) {
		return commonProductService.findProduct(productId);
	}

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

}
