package net.aircommunity.platform.service.internal;

import javax.annotation.Resource;

import org.springframework.cache.CacheManager;

/**
 * Abstract service support.
 * 
 * @author Bin.Zhang
 */
abstract class AbstractServiceSupport {

	@Resource
	protected CacheManager cacheManager;

}
