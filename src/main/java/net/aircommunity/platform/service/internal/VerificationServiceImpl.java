package net.aircommunity.platform.service.internal;

import org.springframework.stereotype.Service;

import net.aircommunity.platform.service.VerificationService;

/**
 * Verification service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class VerificationServiceImpl implements VerificationService {

	// TODO: impl based on redis

	@Override
	public String generateCode(String key, long expires) {
		// TODO
		return null;
	}

	@Override
	public boolean verifyCode(String key, String code) {
		// TODO
		return true;
	}

}
