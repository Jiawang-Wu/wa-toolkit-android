package com.windowsazure.samples.android.storageclient;

final class Base64 {

	private static final String BASE_64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	private static final byte m_Decode64[] = { -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1,
			-1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
			13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1,
			-1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };

	public static byte[] decode(String s) throws IllegalArgumentException {
		int i = (3 * s.length()) / 4;
		if (s.endsWith("=="))
			i -= 2;
		else if (s.endsWith("="))
			i--;
		byte abyte0[] = new byte[i];
		int j = 0;
		for (int k = 0; k < s.length(); k += 4) {
			byte byte0 = m_Decode64[(byte) s.charAt(k)];
			byte byte1 = m_Decode64[(byte) s.charAt(k + 1)];
			byte byte2 = m_Decode64[(byte) s.charAt(k + 2)];
			byte byte3 = m_Decode64[(byte) s.charAt(k + 3)];
			if (byte0 < 0 || byte1 < 0 || byte2 == -1 || byte3 == -1)
				throw new IllegalArgumentException(
						"The String is not a valid Base64-encoded string.");
			int l = byte0 << 18;
			l += byte1 << 12;
			l += (byte2 & 0xff) << 6;
			l += byte3 & 0xff;
			if (byte2 == -2) {
				l &= 0xfff000;
				abyte0[j++] = (byte) (l >> 16 & 0xff);
				continue;
			}
			if (byte3 == -2) {
				l &= 0xffffc0;
				abyte0[j++] = (byte) (l >> 16 & 0xff);
				abyte0[j++] = (byte) (l >> 8 & 0xff);
			} else {
				abyte0[j++] = (byte) (l >> 16 & 0xff);
				abyte0[j++] = (byte) (l >> 8 & 0xff);
				abyte0[j++] = (byte) (l & 0xff);
			}
		}

		return abyte0;
	}

	public static String encode(byte abyte0[]) {
		StringBuilder stringbuilder = new StringBuilder();
		int i = abyte0.length % 3;
		int j = 0;
		int k = 0;
		for (; j < abyte0.length; j += 3) {
			if (j < abyte0.length - i)
				k = ((abyte0[j] & 0xff) << 16) + ((abyte0[j + 1] & 0xff) << 8)
						+ (abyte0[j + 2] & 0xff);
			else if (i == 1)
				k = (abyte0[j] & 0xff) << 16;
			else if (i == 2)
				k = ((abyte0[j] & 0xff) << 16) + ((abyte0[j + 1] & 0xff) << 8);
			stringbuilder
					.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
							.charAt((byte) (k >>> 18 & 0x3f)));
			stringbuilder
					.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
							.charAt((byte) (k >>> 12 & 0x3f)));
			stringbuilder
					.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
							.charAt((byte) (k >>> 6 & 0x3f)));
			stringbuilder
					.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
							.charAt((byte) (k & 0x3f)));
		}

		int l = stringbuilder.length();
		if (abyte0.length % 3 == 1)
			stringbuilder.replace(l - 2, l, "==");
		else if (abyte0.length % 3 == 2)
			stringbuilder.replace(l - 1, l, "=");
		return stringbuilder.toString();
	}

	public static Boolean validateIsBase64String(String s) {
		if (s == null || s.length() % 4 != 0)
			return Boolean.valueOf(false);
		for (int i = 0; i < s.length(); i++) {
			byte byte0 = (byte) s.charAt(i);
			if (m_Decode64[byte0] == -2) {
				if (i < s.length() - 2)
					return Boolean.valueOf(false);
				if (i == s.length() - 2
						&& m_Decode64[(byte) s.charAt(i + 1)] != -2)
					return Boolean.valueOf(false);
			}
			if (byte0 < 0 || m_Decode64[byte0] == -1)
				return Boolean.valueOf(false);
		}

		return Boolean.valueOf(true);
	}
	Base64() {
	}

}
