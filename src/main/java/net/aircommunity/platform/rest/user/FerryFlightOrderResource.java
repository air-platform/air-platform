package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.FerryFlightOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.FerryFlightOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * FerryFlightOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class FerryFlightOrderResource extends UserOrderResourceSupport<FerryFlightOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(FerryFlightOrderResource.class);

	@Resource
	private FerryFlightOrderService ferryFlightOrderService;

	@Override
	protected StandardOrderService<FerryFlightOrder> getStandardOrderService() {
		return ferryFlightOrderService;
	}

	// /**
	// * Find order
	// */
	// @GET
	// @Path("/a/{orderId}")
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public FerryFlightOrder find2(@PathParam("orderId") String orderId) {
	// return ferryFlightOrderService.findFerryFlightOrder(orderId);
	// }
	//
	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
	// @NotNull @Valid FerryFlightOrder order, @Context UriInfo uriInfo) {
	// LOG.debug("[{}] Creating order {}", userAgent, order);
	// FerryFlightOrder created = ferryFlightOrderService.createFerryFlightOrder(userId,
	// detectOrderChannel(userAgent, order));
	// URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
	// LOG.debug("Created {}", uri);
	// return Response.created(uri).build();
	// }
	//
	// /**
	// * List
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Page<FerryFlightOrder> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return ferryFlightOrderService.listUserFerryFlightOrders(userId, status, page, pageSize);
	// }
	//
	// /**
	// * Update
	// */
	// @PUT
	// @Path("{orderId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public FerryFlightOrder update(@PathParam("orderId") String orderId, @NotNull @Valid FerryFlightOrder newOrder) {
	// return ferryFlightOrderService.updateFerryFlightOrder(orderId, newOrder);
	// }

}
