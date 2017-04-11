package net.aircommunity.platform.model;

/**
 * Roles used by platform.
 * 
 * @author Bin.Zhang
 */
public final class Roles {

	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_TENANT = "TENANT";
	public static final String ROLE_USER = "USER";

	private Roles() {
		throw new AssertionError();
	}

}
