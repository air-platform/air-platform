package net.aircommunity.platform.service.internal.security;

import org.springframework.stereotype.Service;

import net.aircommunity.platform.security.PrivilegedResource;
import net.aircommunity.platform.service.security.AccessControlService;

/**
 * Access control service implementation. TODO
 */
@Service
public class AccessControlServiceImpl implements AccessControlService {

	@Override
	public void checkResourceAccess(String accountId, PrivilegedResource resource) {
		// TODO
	}

}