package net.aircommunity.service.internal;

import org.springframework.stereotype.Service;

import net.aircommunity.security.PrivilegedResource;
import net.aircommunity.service.AccessControlService;

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
