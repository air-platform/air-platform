package net.aircommunity.platform.security;

import java.io.Serializable;

/**
 * Privileged Resource is used for access control.
 * 
 * @author Bin.Zhang
 */
public class PrivilegedResource implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Type resourceType;
	private final String resourceId; // should be always agentId ATM
	private final String subResourceId;

	public static PrivilegedResource of(Type resourceType, String resourceId, String subResourceId) {
		return new PrivilegedResource(resourceType, resourceId, subResourceId);
	}

	public static PrivilegedResource of(Type resourceType, String resourceId) {
		return of(resourceType, resourceId, null);
	}

	private PrivilegedResource(Type resourceType, String resourceId, String subResourceId) {
		this.resourceType = resourceType;
		this.resourceId = resourceId;
		this.subResourceId = subResourceId;
	}

	public Type getResourceType() {
		return resourceType;
	}

	public String getResourceId() {
		return resourceId;
	}

	public String getSubResourceId() {
		return subResourceId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrivilegedResource [resourceType=").append(resourceType).append(", resourceId=")
				.append(resourceId).append(", subResourceId=").append(subResourceId).append("]");
		return builder.toString();
	}

	// TODO
	public enum Type {
		ACCOUNT, ORDER;
	}
}
