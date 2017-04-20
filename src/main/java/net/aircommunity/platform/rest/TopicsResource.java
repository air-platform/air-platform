package net.aircommunity.platform.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.Topic;
import net.aircommunity.platform.service.TopicService;

/**
 * AirBB Topics RESTful API.
 *
 * @author luocheng
 */
@Api
@RESTful
@PermitAll
@Path("topics")
public class TopicsResource {

	@Resource
	private TopicService topicService;

	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response list() {
		List<Topic> result = topicService.listRecentTopics();
		return Response.ok(result).build();
	}
}