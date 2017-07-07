package net.aircommunity.platform.service.internal.payment.newpay.client;

import java.io.ObjectInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA Signature
 * 
 * @author Bin.Zhang
 */
final class RsaSignature {
	private static final String ALGO_NAME = "SHA1withRSA";
	private static final String ALGO_ALIAS = "RSA";

	public static String genSignature(byte[] src, byte[] usekey) throws Exception {
		return genSignature(src, getPrivateKey(usekey));
	}

	public static String genSignature(byte[] src, PrivateKey usekey, String provider) throws Exception {
		Signature sig = Signature.getInstance(ALGO_NAME, provider);
		sig.initSign(usekey);
		sig.update(src);
		return Bytes.toHexString(sig.sign());
	}

	public static boolean verifySignature(byte[] src, String dest, PublicKey usekey, String provider) throws Exception {
		Signature sigcheck = Signature.getInstance(ALGO_NAME, provider);
		sigcheck.initVerify(usekey);
		sigcheck.update(src);
		return sigcheck.verify(Bytes.toByteArray(dest));
	}

	public static String genSignature(byte[] src, Key usekey) throws Exception {
		return genSignature(src, (PrivateKey) usekey);
	}

	public static boolean verifySignature(byte[] src, String dest, byte[] encodedKey) throws Exception {
		return verifySignature(src, dest, getPublicKey(encodedKey));
	}

	public static String genSignature(byte[] src, ObjectInputStream usekey) throws Exception {
		PrivateKey mykey = (PrivateKey) usekey.readObject();
		usekey.close();
		Signature sig = Signature.getInstance(ALGO_NAME);
		sig.initSign(mykey);
		sig.update(src);
		return Bytes.toHexString(sig.sign());
	}

	public static String genSignature(byte[] src, PrivateKey usekey) throws Exception {
		Signature sig = Signature.getInstance(ALGO_NAME);
		sig.initSign(usekey);
		sig.update(src);
		return Bytes.toHexString(sig.sign());
	}

	public static boolean verifySignature(byte[] src, String dest, ObjectInputStream usekey) throws Exception {
		PublicKey mypublickey = (PublicKey) usekey.readObject();
		usekey.close();
		Signature sigcheck = Signature.getInstance(ALGO_NAME);
		sigcheck.initVerify(mypublickey);
		sigcheck.update(src);
		return sigcheck.verify(Bytes.toByteArray(dest));
	}

	public static boolean verifySignature(byte[] src, String dest, PublicKey publicKey) throws Exception {
		Signature sigcheck = Signature.getInstance(ALGO_NAME);
		sigcheck.initVerify(publicKey);
		sigcheck.update(src);
		return sigcheck.verify(Bytes.toByteArray(dest));
	}

	private static PublicKey getPublicKey(byte[] encodedKey) throws Exception {
		KeyFactory kFactory = KeyFactory.getInstance(ALGO_ALIAS);
		X509EncodedKeySpec myPubKeySpec = new X509EncodedKeySpec(encodedKey);
		PublicKey mPubKey = kFactory.generatePublic(myPubKeySpec);
		return mPubKey;
	}

	private static PrivateKey getPrivateKey(byte[] key) throws Exception {
		KeyFactory kFactory = KeyFactory.getInstance(ALGO_ALIAS);
		PKCS8EncodedKeySpec myPubKeySpec = new PKCS8EncodedKeySpec(key);
		PrivateKey mPrivateKey = kFactory.generatePrivate(myPubKeySpec);
		return mPrivateKey;
	}

	private RsaSignature() {
		throw new AssertionError();
	}
}