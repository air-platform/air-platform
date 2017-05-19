package net.aircommunity.platform;

import java.io.Serializable;

/**
 * The set of canonical status codes. If new codes are added over time they must choose a numerical value that does not
 * collide with any previously used value.
 */
public final class Code implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int SUCCESS = 200;

	/**
	 * Client specified an invalid argument. It indicates arguments that are problematic regardless of the state of the
	 * system (e.g., a malformed file name).
	 */
	private static final int INVALID_ARGUMENT = 400;
	private static final int NOT_AUTHORIZED = 401;
	private static final int NOT_PERIMITTED = 403;
	private static final int NOT_FOUND = 404;
	private static final int ALREADY_EXISTS = 409;
	private static final int ILLEGAL_ACCESS = 409;

	/**
	 * This resource is gone. Used to indicate that an API endpoint has been turned off.
	 */
	private static final int GONE = 410;

	/**
	 * Returned in when a request cannot be served due to the application’s rate limit having been exhausted for the
	 * resource. See Rate Limiting .
	 */
	private static final int TOO_MANY_REQUESTS = 429;

	/**
	 * Something is broken. Please post to the developer forums with additional details of your request, so the team can
	 * investigate.
	 */
	private static final int INTERNAL_ERROR = 500;

	/**
	 * Server is down or being upgraded.
	 */
	private static final int BAD_GATEWAY = 502;

	/**
	 * The server is up, but overloaded with requests. Try again later.
	 */
	private static final int SERVICE_UNAVAILABLE = 503;

	/**
	 * The server is up, but the request couldn’t be serviced due to some failure within our stack. Try again later.
	 */
	private static final int GATEWAY_TIMEOUT = 504;

	private final int status;
	private final int value;

	public static Code success(int value) {
		return new Code(SUCCESS, value);
	}

	public static Code invalidArgument(int value) {
		return new Code(INVALID_ARGUMENT, value);
	}

	public static Code notAuthorized(int value) {
		return new Code(NOT_AUTHORIZED, value);
	}

	public static Code notPerimitted(int value) {
		return new Code(NOT_PERIMITTED, value);
	}

	public static Code notFound(int value) {
		return new Code(NOT_FOUND, value);
	}

	public static Code alreadyExists(int value) {
		return new Code(ALREADY_EXISTS, value);
	}

	public static Code illegalAccess(int value) {
		return new Code(ILLEGAL_ACCESS, value);
	}

	public static Code apiTurnedoff(int value) {
		return new Code(GONE, value);
	}

	public static Code rateLimiting(int value) {
		return new Code(TOO_MANY_REQUESTS, value);
	}

	public static Code internalError(int value) {
		return new Code(INTERNAL_ERROR, value);
	}

	public static Code badGateway(int value) {
		return new Code(BAD_GATEWAY, value);
	}

	public static Code serviceUnavailable(int value) {
		return new Code(SERVICE_UNAVAILABLE, value);
	}

	public static Code gatewayTimeout(int value) {
		return new Code(GATEWAY_TIMEOUT, value);
	}

	private Code(int status, int value) {
		this.status = status;
		this.value = value;
	}

	public int getStatus() {
		return status;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Code [status=").append(status).append(", value=").append(value).append("]");
		return builder.toString();
	}
}
