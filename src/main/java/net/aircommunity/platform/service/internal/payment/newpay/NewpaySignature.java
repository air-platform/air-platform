package net.aircommunity.platform.service.internal.payment.newpay;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Objects;

/**
 * Newpay RSA Signature Signer.
 * 
 * @author Bin.Zhang
 */
final class NewpaySignature {
	private static final String KEYSTORE_TYPE = "JKS";
	private final NewpayConfig config;
	private final PrivateKey privateKey;

	NewpaySignature(NewpayConfig configuration) {
		config = configuration;
		try {
			InputStream in = NewpaySignature.class.getClassLoader().getResourceAsStream(config.getKeystorePath());
			KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
			ks.load(in, config.getKeystorePassword().toCharArray());
			// Certificate certificate = ks.getCertificate(config.getCertificateEntryAlias());
			// PublicKey publicKey = certificate.getPublicKey();
			privateKey = (PrivateKey) ks.getKey(config.getCertAlias(), config.getCertPassword().toCharArray());
			Objects.requireNonNull(privateKey, "private key is null, keystore path is not properly configured");
		}
		catch (Exception e) {
			throw new NewpayException("Failed init RSA signature signer:" + e.getMessage(), e);
		}

	}

	public String signWithRSA(String src, NewpayCharset charset) {
		assertNotBlank(src, "src is empty, cannot sign with RSA signature");
		try {
			String hash = String.valueOf(pjwHash(src));
			return RsaSignature.genSignature(hash.getBytes(charset.getName()), privateKey);
		}
		catch (Exception e) {
			throw new NewpayException("Failed to generate RSA signature:" + e.getMessage(), e);
		}
	}

	public boolean verifyRsaSignature(String src, String dest, NewpayCharset charset) {
		assertNotBlank(src, "src is empty, cannot verify RSA signature");
		assertNotBlank(dest, "dest is empty, cannot verify RSA signature");
		try {
			String hash = String.valueOf(pjwHash(src));
			return RsaSignature.verifySignature(hash.getBytes(charset.getName()), dest,
					Bytes.toByteArray(config.getGatewayPublicKey()));
		}
		catch (Exception e) {
			throw new NewpayException("Invalid RSA Signature:" + e.getMessage(), e);
		}
	}

	// public static boolean verifyRsaSignature(String src, String dit, TradeCharset charset) {
	// try {
	// String hashSrc = String.valueOf(pjwHash(src));
	// return RsaSignature.verifySignature(hashSrc.getBytes(charset.getName()), dit,
	// Bytes.toByteArray((String) paramsMap.get("hnapay.gateway.pubkey")));
	// }
	// catch (Exception e) {
	// throw new Exception(e);
	// }
	// }

	private static void assertNotBlank(String str, String errorMessage) {
		if (isBlank(str)) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	private static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reference https://en.wikipedia.org/wiki/PJW_hash_function
	 */
	private static int pjwHash(String str) {
		int bitsInUnsignedInt = 32;
		int threeQuarters = bitsInUnsignedInt * 3 / 4;
		int oneEighth = bitsInUnsignedInt / 8;
		int highBits = -1 << bitsInUnsignedInt - oneEighth;
		int hash = 0;
		int test = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash << oneEighth) + str.charAt(i);
			if ((test = hash & highBits) != 0) {
				hash = (hash ^ test >> threeQuarters) & (highBits ^ 0xFFFFFFFF);
			}
		}
		return hash & 0x7FFFFFFF;
	}
}