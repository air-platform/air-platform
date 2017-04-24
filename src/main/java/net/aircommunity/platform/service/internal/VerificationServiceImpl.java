package net.aircommunity.platform.service.internal;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import io.micro.common.Randoms;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.service.VerificationService;

/**
 * Verification service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
public class VerificationServiceImpl implements VerificationService {
	private static final int REQUEST_PER_IP = 10;
	private static final int VERIFICATION_CODE_LENGTH = 6;
	// account verification code
	private static final String VERIFICATION_CODD_KEY_FORMAT = "account:vc:%s";
	private static final String IP_KEY_FORMAT = "account:ip:%s";

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	@PostConstruct
	private void init() {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
	}

	@Override
	public String generateCode(String key, String ip, long expires) {
		// per IP address 120 seconds
		String ipRequested = redisTemplate.opsForValue().get(String.format(IP_KEY_FORMAT, ip));
		if (ipRequested != null) {
			int requests = Integer.valueOf(ipRequested);
			if (requests <= 0) {
				throw new AirException(Codes.TOO_MANY_VERIFICATION_REQUEST, "Too many request");
			}
			// dec by 1 (AKA. incr -1)
			redisTemplate.opsForValue().increment(String.format(IP_KEY_FORMAT, ip), -1l);
		}
		else {
			redisTemplate.opsForValue().set(String.format(IP_KEY_FORMAT, ip), REQUEST_PER_IP + "", expires,
					TimeUnit.SECONDS);
		}
		String code = findCode(key);
		if (code != null) {
			return code;
		}
		code = Randoms.randomNumeric(VERIFICATION_CODE_LENGTH);
		redisTemplate.opsForValue().set(String.format(VERIFICATION_CODD_KEY_FORMAT, key), code, expires,
				TimeUnit.SECONDS);
		return code;
	}

	@Override
	public String generateCode(String key, long expires) {
		String code = findCode(key);
		if (code != null) {
			return code;
		}
		code = Randoms.randomNumeric(VERIFICATION_CODE_LENGTH);
		redisTemplate.opsForValue().set(String.format(VERIFICATION_CODD_KEY_FORMAT, key), code, expires,
				TimeUnit.SECONDS);
		return code;
	}

	@Override
	public boolean verifyCode(String key, String code) {
		String codeFound = findCode(key);
		if (codeFound == null) {
			return false;
		}
		return codeFound.equals(code);
	}

	private String findCode(String key) {
		return (String) redisTemplate.opsForValue().get(String.format(VERIFICATION_CODD_KEY_FORMAT, key));
	}

}
