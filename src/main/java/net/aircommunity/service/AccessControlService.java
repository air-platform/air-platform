package net.aircommunity.service;

import javax.annotation.Nonnull;

import net.aircommunity.AirException;
import net.aircommunity.security.PrivilegedResource;

/**
 * The resource access control.
 * 
 * @author Bin.Zhang
 */
public interface AccessControlService {

	/**
	 * Check if the resource is accessible by an account.
	 * 
	 * @param accountId the accountId
	 * @param resource the resource to be checked
	 * @throws AirException if access is denied
	 */
	void checkResourceAccess(@Nonnull String accountId, @Nonnull PrivilegedResource resource);
}
