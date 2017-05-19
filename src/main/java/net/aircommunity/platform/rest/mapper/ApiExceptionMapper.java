package net.aircommunity.platform.rest.mapper;

import static javax.ws.rs.core.Response.status;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.model.ErrorResponse;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Code;
import net.aircommunity.platform.Codes;

/**
 * Exception mapper for mapping a Exception that was thrown from an application into a human readable message and error
 * code.
 * 
 * @author Bin.Zhang
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<AirException> {
	private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionMapper.class);

	// The request that triggered the exception (so we can extract locale)
	@Context
	private HttpServletRequest currentRequest;

	public Response toResponse(AirException e) {
		LOG.error(e.getLocalizedMessage(), e);
		// build error response
		Code code = e.getCode();
		if (code == null) {
			code = Codes.INTERNAL_ERROR;
		}
		return status(code.getStatus()).entity(new ErrorResponse(code.getValue(), e.getLocalizedMessage()))
				.type(MediaType.APPLICATION_JSON).build();

	}
}
