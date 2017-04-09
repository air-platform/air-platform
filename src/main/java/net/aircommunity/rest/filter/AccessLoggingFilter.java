package net.aircommunity.rest.filter;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final String ANONYMOUS = "anonymous";
	private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

	@Context
	private HttpServletRequest request;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Use the ContainerRequestContext to extract information from the HTTP request
		// Information such as the URI, headers and HTTP entity are available
		String forwardIp = "";
		try {
			// when using Heroku, the remote host is the AWS application tier, therefore the EC2 ip address.
			forwardIp = request.getHeader(HEADER_X_FORWARDED_FOR).split(",")[0];
		}
		catch (Exception ignored) {
		}
		String username = ANONYMOUS;
		Principal principal = requestContext.getSecurityContext().getUserPrincipal();
		if (principal != null) {
			username = principal.getName();
		}
		// TODO check if request.getRemoteAddr() and local server IP address is the same subnet
		// And just use forwardIp for client IP in this case
		// e.g. 192.168.100.195 106.37.175.130
		LOG.info("[{} {}] [{}] {} {}", request.getRemoteAddr(), forwardIp, username, requestContext.getMethod(),
				requestContext.getUriInfo().getRequestUri().getPath());
	}

}
