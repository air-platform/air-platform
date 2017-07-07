package net.aircommunity.platform.rest;

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.micro.annotation.RESTful;
import io.swagger.annotations.Api;
import net.aircommunity.platform.model.AirqTopic;
import net.aircommunity.platform.service.AirqTopicService;

/**
 * AirQ Topics RESTful API.
 *
 * @author luocheng
 */
@Api
@RESTful
@PermitAll
@Path("topics") // XXX rename to airq/topics?
public class AirqTopicsResource {

	@Resource
	private AirqTopicService airqTopicService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<AirqTopic> list() {
		return airqTopicService.listRecentTopics();
	}
}
