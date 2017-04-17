package net.aircommunity.platform.rest;


import net.aircommunity.platform.model.Topic;
import net.aircommunity.rest.annotation.RESTful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.aircommunity.platform.service.TopicService;

import java.util.ArrayList;

/**
 * Account RESTful API.
 *
 * @author luocheng
 */
@RESTful
@Path("topics")
public class TopicsResource {

    private static final Logger LOG = LoggerFactory.getLogger(JetCardResource.class);

    @Resource
    private TopicService topicService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecentTopics() {

        ArrayList<Topic> result = topicService.getRecentTopics();
        return Response.ok(result).build();
    }
}
