package net.aircommunity.platform.rest.filter;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.CounterService;

import com.google.common.base.Joiner;

import io.micro.common.Strings;
import io.micro.core.security.SimplePrincipal;
import net.aircommunity.platform.Constants;

/**
 * Log all REST API request.
 * 
 * @author Bin.Zhang
 */
// http://stackoverflow.com/questions/33666406/logging-request-and-response-in-one-place-with-jax-rs
@Provider
@Priority(Priorities.USER)
public class AccessLoggingFilter implements ContainerRequestFilter {
	private static final Logger LOG = LoggerFactory.getLogger(AccessLoggingFilter.class);

	private static final String ANONYMOUS = "ANONYMOUS";

	@Resource
	private CounterService counterService;

	@Context
	private HttpServletRequest request;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Use the ContainerRequestContext to extract information from the HTTP request
		// Information such as the URI, headers and HTTP entity are available
		String forwardIp = "";
		try {
			// when using Heroku, the remote host is the AWS application tier, therefore the EC2 ip address.
			forwardIp = request.getHeader(Constants.HEADER_X_FORWARDED_FOR).split(",")[0];
		}
		catch (Exception ignored) {
		}
		String username = ANONYMOUS;
		String role = ANONYMOUS;
		Principal principal = requestContext.getSecurityContext().getUserPrincipal();
		if (principal != null) {
			username = principal.getName();
			role = Joiner.on(",").join(SimplePrincipal.class.cast(principal).getClaims().getRoles());
		}
		// TODO check if request.getRemoteAddr() and local server IP address is the same subnet
		// And just use forwardIp for client IP in this case
		// e.g. 192.168.100.195 106.37.175.130
		String ip = request.getRemoteAddr();
		if (Constants.LOOPBACK_ADDRESSES.contains(ip)) {
			ip = forwardIp;
		}
		if (Strings.isBlank(ip)) {
			ip = Constants.LOOPBACK_LOCALHOST;
		}
		counterService.increment(Constants.COUNTER_API_REQUESTS);
		LOG.info("[{}] [{}] [{}] {} {} {}", ip, username, role, requestContext.getMethod(),
				requestContext.getUriInfo().getRequestUri().getPath(), requestContext.getMediaType());
	}

}
