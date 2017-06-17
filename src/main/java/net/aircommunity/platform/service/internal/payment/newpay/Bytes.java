package net.aircommunity.platform.service.internal.payment.newpay;

/**
 * Bytes utility
 *
 * @author Bin.Zhang
 */
final class Bytes {

	private static final String HEX_CHARS = "0123456789abcdef";

	static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_CHARS.charAt(b[i] >>> 4 & 0xF));
			sb.append(HEX_CHARS.charAt(b[i] & 0xF));
		}
		return sb.toString();
	}

	static byte[] toByteArray(String s) {
		if (s.length() % 2 != 0)
			return new byte[s.length() / 2];
		byte[] buf = new byte[s.length() / 2];
		int j = 0;
		for (int i = 0; i < buf.length; i++) {
			buf[i] = ((byte) (Character.digit(s.charAt(j++), 16) << 4 | Character.digit(s.charAt(j++), 16)));
		}
		return buf;
	}
}
