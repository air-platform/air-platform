package net.aircommunity.platform.model;

/**
 * The role of an {@code Account}.
 * 
 * @author Bin.Zhang
 */
public enum Role {

	/**
	 * Platform Admin.
	 */
	ADMIN(Roles.ROLE_ADMIN),

	/**
	 * Platform Air service provider.
	 */
	TENANT(Roles.ROLE_TENANT),

	/**
	 * Normal user.
	 */
	USER(Roles.ROLE_USER);

	private final String value;

	private Role(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Role of(String value) {
		for (Role e : values()) {
			if (e.name().equalsIgnoreCase(value)) {
				return e;
			}
		}
		return null;
	}

}
