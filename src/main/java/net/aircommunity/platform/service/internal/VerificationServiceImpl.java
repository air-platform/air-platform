package net.aircommunity.platform.service.internal;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import net.aircommunity.platform.common.base.Randoms;
import net.aircommunity.platform.service.VerificationService;

/**
 * Verification service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class VerificationServiceImpl implements VerificationService {
	private static final int VERIFICATION_CODE_LENGTH = 6;

	// account verification code
	private static final String VERIFICATION_CODD_KEY_FORMAT = "account:vc:%s";

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public String generateCode(String key, long expires) {
		String code = Randoms.randomNumeric(VERIFICATION_CODE_LENGTH);
		redisTemplate.opsForValue().set(String.format(VERIFICATION_CODD_KEY_FORMAT, key), code, expires,
				TimeUnit.SECONDS);
		return code;
	}

	@Override
	public boolean verifyCode(String key, String code) {
		String codeFound = redisTemplate.opsForValue().get(String.format(VERIFICATION_CODD_KEY_FORMAT, key));
		if (codeFound == null) {
			return false;
		}
		return codeFound.equals(code);
	}

}
