package android.util;

// Simplified version of Base64 for testing SharedGameData
public class Base64 {
	public static final int NO_PADDING = 0x1;
	public static final int NO_WRAP = 0x2;
	public static final int URL_SAFE = 0x8;

	public static String encodeToString(byte[] input, int flags) {
		String ret;
		ret = java.util.Base64.getEncoder().encodeToString(input);
		if ((flags & NO_PADDING) != 0) {
			return ret.replace("=","");
		}
		return ret;
	}
	public static byte[] decode(String str, int flags) {
		return java.util.Base64.getDecoder().decode(str);
	}
}
