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
	private final String resourceId;
	private final Object context;

	public static PrivilegedResource of(Type resourceType, String resourceId, Object context) {
		return new PrivilegedResource(resourceType, resourceId, context);
	}

	public static PrivilegedResource of(Type resourceType, String resourceId) {
		return of(resourceType, resourceId, null);
	}

	private PrivilegedResource(Type resourceType, String resourceId, Object context) {
		this.resourceType = resourceType;
		this.resourceId = resourceId;
		this.context = context;
	}

	public Type getResourceType() {
		return resourceType;
	}

	public String getResourceId() {
		return resourceId;
	}

	public Object getContext() {
		return context;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrivilegedResource [resourceType=").append(resourceType).append(", resourceId=")
				.append(resourceId).append(", context=").append(context).append("]");
		return builder.toString();
	}

	public enum Type {
		ORDER, PRODUCT, AIRCRAFT, SCHOOL, PRODUCT_FAMILY;
	}
}
