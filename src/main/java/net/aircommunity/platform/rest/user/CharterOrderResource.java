package net.aircommunity.platform.rest.user;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import io.micro.annotation.RESTful;
import net.aircommunity.platform.model.Roles;
import net.aircommunity.platform.model.domain.CharterOrder;
import net.aircommunity.platform.rest.annotation.AllowResourceOwner;
import net.aircommunity.platform.service.order.CharterOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * CharterOrder RESTful API for ADMIN and USER.
 * 
 * @author Bin.Zhang
 */
@RESTful
@AllowResourceOwner
@RolesAllowed({ Roles.ROLE_ADMIN, Roles.ROLE_USER })
public class CharterOrderResource extends UserOrderResourceSupport<CharterOrder> {
	// private static final Logger LOG = LoggerFactory.getLogger(CharterOrderResource.class);

	@Resource
	private CharterOrderService charterOrderService;

	@Override
	protected StandardOrderService<CharterOrder> getStandardOrderService() {
		return charterOrderService;
	}

	// /**
	// * Create
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @JsonView(JsonViews.User.class)
	// public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
	// @NotNull @Valid CharterOrder order, @Context UriInfo uriInfo) {
	// LOG.debug("[{}] Creating order {}", userAgent, order);
	// CharterOrder created = charterOrderService.createCharterOrder(userId, detectOrderChannel(userAgent, order));
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
	// public Page<CharterOrder> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
	// @QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
	// return charterOrderService.listUserCharterOrders(userId, status, page, pageSize);
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
	// public CharterOrder update(@PathParam("orderId") String orderId, @NotNull @Valid CharterOrder newOrder) {
	// return charterOrderService.updateCharterOrder(orderId, newOrder);
	// }

	/**
	 * Select a fleet (Only allow one vendor to be selected)
	 */
	// @POST
	// @Path("{orderId}/select")
	// public void selectFleet(@PathParam("orderId") String orderId,
	// @NotNull @QueryParam("candidate") String fleetCandidateId) {
	// charterOrderService.selectFleetCandidate(orderId, fleetCandidateId);
	// }

}
