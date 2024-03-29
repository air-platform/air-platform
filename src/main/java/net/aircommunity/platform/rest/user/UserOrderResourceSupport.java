package net.aircommunity.platform.rest.user;

import java.net.URI;

import javax.annotation.Resource;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

import io.micro.common.Strings;
import net.aircommunity.platform.common.ua.Client;
import net.aircommunity.platform.common.ua.Device;
import net.aircommunity.platform.common.ua.Parser;
import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.rest.BaseResourceSupport;
import net.aircommunity.platform.service.order.CharterOrderService;
import net.aircommunity.platform.service.order.QuickFlightOrderService;
import net.aircommunity.platform.service.order.StandardOrderService;

/**
 * Base User order RESTful API for ADMIN/USER
 * 
 * @author Bin.Zhang
 */
public abstract class UserOrderResourceSupport<T extends Order> extends BaseResourceSupport {
	private static final Logger LOG = LoggerFactory.getLogger(UserOrderResourceSupport.class);

	private static final Parser UA_PARSER = Parser.newParser();

	/**
	 * Create
	 */
	@POST
	@JsonView(JsonViews.User.class)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@HeaderParam("user-agent") String userAgent, @PathParam("userId") String userId,
			@NotNull @Valid T order, @Context UriInfo uriInfo) {
		LOG.debug("[{}] Creating order {}", userAgent, order);
		T created = getStandardOrderService().createOrder(userId, detectOrderChannel(userAgent, order));
		URI uri = uriInfo.getAbsolutePathBuilder().segment(created.getId()).build();
		LOG.debug("Created {}", uri);
		return Response.created(uri).build();
	}

	/**
	 * Detect order channel from UA.
	 * 
	 * @param userAgent UA
	 * @return
	 * @return the order channel
	 */
	protected <ORDER extends Order> ORDER detectOrderChannel(String userAgent, ORDER order) {
		// parse userAgent for channel
		LOG.debug("Order userAgent: {}", userAgent);
		String channel = parseForChannel(userAgent);
		LOG.debug("Order channel: {}", channel);
		order.setChannel(channel);
		return order;
	}

	private String parseForChannel(String userAgent) {
		Client c = UA_PARSER.parse(userAgent);
		StringBuilder builder = new StringBuilder(c.os.family);
		if (Strings.isNotBlank(c.os.major)) {
			builder.append(" ").append(c.os.major);
		}
		if (Strings.isNotBlank(c.os.minor)) {
			builder.append(".").append(c.os.minor);
		}
		if (Strings.isNotBlank(c.os.patch)) {
			builder.append(".").append(c.os.patch);
		}
		builder.append("(");
		// TODO check
		if (Device.OTHER.family.equalsIgnoreCase(c.device.family)) {
			builder.append(c.userAgent.family);
		}
		else {
			builder.append(c.device.family);
		}
		String channel = builder.append(")").toString();
		// 50 is max channel column size (just in case this happens, normally it will not exceed this size)
		if (channel.length() > 50) {
			channel = channel.substring(0, 50);
		}
		return channel;
	}

	/**
	 * Find
	 */
	@GET
	@Path("{orderId}")
	@JsonView(JsonViews.User.class)
	@Produces(MediaType.APPLICATION_JSON)
	public T find(@PathParam("orderId") String orderId) {
		T order = getStandardOrderService().findOrder(orderId);
		// NOTE: it can be still returns order in DELETED status, because of ADMIN find order may cached
		return ensureOrderVisible(order);
	}

	/**
	 * List
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public Page<T> list(@PathParam("userId") String userId, @QueryParam("status") Order.Status status,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		return getStandardOrderService().listUserOrders(userId, status, page, pageSize);
	}

	/**
	 * Update
	 */
	@PUT
	@Path("{orderId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(JsonViews.User.class)
	public T update(@PathParam("orderId") String orderId, @NotNull @Valid T order) {
		return getStandardOrderService().updateOrder(orderId, order);
	}

	/**
	 * Delete (mark order as DELETED)
	 */
	@DELETE
	@Path("{orderId}")
	public void delete(@PathParam("orderId") String orderId) {
		getStandardOrderService().softDeleteOrder(orderId);
	}

	/**
	 * Cancel order
	 */
	@POST
	@Path("{orderId}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	public void cancel(@PathParam("orderId") String orderId, JsonObject request) {
		String reason = getReason(request);
		commonOrderService.cancelOrder(orderId, reason);
	}

	protected abstract StandardOrderService<T> getStandardOrderService();

	// ********************************************
	// QuickFlightOrder ONLY
	// ********************************************
	@Resource
	private QuickFlightOrderService quickFlightOrderService;

	/**
	 * Select a aircraft (Only allow one vendor to be selected)
	 */
	@POST
	@Path("{orderId}/aircraft/select")
	public void selectAircraft(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("candidate") String candidateId) {
		quickFlightOrderService.selectOrderCandidate(orderId, candidateId);
	}

	// ********************************************
	// CharterOrder ONLY
	// ********************************************
	@Resource
	private CharterOrderService charterOrderService;

	/**
	 * Select a fleet (Only allow one vendor to be selected)
	 */
	@POST
	@Path("{orderId}/fleet/select")
	public void selectFleet(@PathParam("orderId") String orderId,
			@NotNull @QueryParam("candidate") String candidateId) {
		charterOrderService.selectOrderCandidate(orderId, candidateId);
	}

}
