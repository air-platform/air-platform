package net.aircommunity.platform.service;

import java.util.Set;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.domain.Contact;

/**
 * Generic platform service for platform administrators (ADMIN ONLY).
 * 
 * @author Bin.Zhang
 */
public interface PlatformService {

	/**
	 * Clear specific cache.
	 * 
	 * @param cacheName the cache name
	 */
	void clearCache(String cacheName);

	/**
	 * Clear all caches.
	 */
	void clearAllCaches();

	/**
	 * Set platform client managers who will receive all the notifications of all tenants when an order is created or
	 * cancelled.
	 * 
	 * @param clientManagers platform clientManagers
	 */
	void setPlatformClientManagers(@Nonnull Set<Contact> clientManagers);

	/**
	 * Returns the platform client managers who will receive all the notifications of all tenants when an order is
	 * created or cancelled.
	 * 
	 * @return a set of contacts or empty if none
	 */
	@Nonnull
	Set<Contact> getPlatformClientManagers();

}
