package net.aircommunity.model;

/**
 * Roles used by platform.
 * 
 * @author Bin.Zhang
 */
public final class Roles {

	public static final String ROLE_ADMIN = "admin";
	public static final String ROLE_TENANT = "tenant";
	public static final String ROLE_USER = "user";

	private Roles() {
		throw new AssertionError();
	}

}
