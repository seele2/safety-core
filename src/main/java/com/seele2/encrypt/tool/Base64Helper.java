package com.seele2.encrypt.tool;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Base64Helper {

	private Base64Helper(){}

	private static final Charset DEFAULT_CHARSET       = StandardCharsets.UTF_8;
	/**
	 * 标准编码表
	 */
	private static final byte[]  STANDARD_ENCODE_TABLE = { //
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', //
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', //
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', //
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', //
			'w', 'x', 'y', 'z', '0', '1', '2', '3', //
			'4', '5', '6', '7', '8', '9', '+', '/' //
	};
	/**
	 * URL安全的编码表，将 + 和 / 替换为 - 和 _
	 */
	private static final byte[]  URL_SAFE_ENCODE_TABLE = { //
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', //
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', //
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', //
			'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', //
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', //
			'w', 'x', 'y', 'z', '0', '1', '2', '3', //
			'4', '5', '6', '7', '8', '9', '-', '_' //
	};


	private static final byte PADDING = -2;

	/**
	 * Base64解码表，共128位，-1表示非base64字符，-2表示padding
	 */
	// private static final byte[] DECODE_TABLE2 = {
	// -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	// -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	// -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
	// 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1,
	// -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
	// 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
	// -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
	// 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
	private static final byte[] DECODE_TABLE = {
			// 0 1 2 3 4 5 6 7 8 9 A B C D E F
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, // 20-2f + - /
			52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, // 30-3f 0-9
			-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // 40-4f A-O
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, // 50-5f P-Z _
			-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, // 60-6f a-o
			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 // 70-7a p-z
	};

	/**
	 * 编码为Base64，非URL安全的
	 *
	 * @param arr     被编码的数组
	 * @param lineSep 在76个char之后是CRLF还是EOF
	 * @return 编码后的bytes
	 */
	public static byte[] encode(byte[] arr, boolean lineSep) {
		return encode(arr, lineSep, false);
	}

	/**
	 * 编码为Base64，URL安全的
	 *
	 * @param arr     被编码的数组
	 * @param lineSep 在76个char之后是CRLF还是EOF
	 * @return 编码后的bytes
	 * @since 3.0.6
	 */
	public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
		return encode(arr, lineSep, true);
	}

	/**
	 * base64编码
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(CharSequence source) {
		return encode(source, DEFAULT_CHARSET);
	}

	/**
	 * base64编码，URL安全
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 * @since 3.0.6
	 */
	public static String encodeUrlSafe(CharSequence source) {
		return encodeUrlSafe(source, DEFAULT_CHARSET);
	}

	/**
	 * base64编码
	 *
	 * @param source  被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String encode(CharSequence source, Charset charset) {
		return encode(StrHelper.bytes(source, charset));
	}

	/**
	 * base64编码，URL安全的
	 *
	 * @param source  被编码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 * @since 3.0.6
	 */
	public static String encodeUrlSafe(CharSequence source, Charset charset) {
		return encodeUrlSafe(StrHelper.bytes(source, charset));
	}

	/**
	 * base64编码
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String encode(byte[] source) {
		return StrHelper.str(encode(source, false), DEFAULT_CHARSET);
	}

	/**
	 * base64编码,URL安全的
	 *
	 * @param source 被编码的base64字符串
	 * @return 被加密后的字符串
	 * @since 3.0.6
	 */
	public static String encodeUrlSafe(byte[] source) {
		return StrHelper.str(encodeUrlSafe(source, false), DEFAULT_CHARSET);
	}

	/**
	 * 编码为Base64<br>
	 * 如果isMultiLine为<code>true</code>，则每76个字符一个换行符，否则在一行显示
	 *
	 * @param arr         被编码的数组
	 * @param isMultiLine 在76个char之后是CRLF还是EOF
	 * @param isUrlSafe   是否使用URL安全字符，一般为<code>false</code>
	 * @return 编码后的bytes
	 */
	public static byte[] encode(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
		if (null == arr) {
			return null;
		}

		int len = arr.length;
		if (len == 0) {
			return new byte[0];
		}

		int    evenlen = (len / 3) * 3;
		int    cnt     = ((len - 1) / 3 + 1) << 2;
		int    destlen = cnt + (isMultiLine ? (cnt - 1) / 76 << 1 : 0);
		byte[] dest    = new byte[destlen];

		byte[] encodeTable = isUrlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;

		for (int s = 0, d = 0, cc = 0; s < evenlen; ) {
			int i = (arr[s++] & 0xff) << 16 | (arr[s++] & 0xff) << 8 | (arr[s++] & 0xff);

			dest[d++] = encodeTable[(i >>> 18) & 0x3f];
			dest[d++] = encodeTable[(i >>> 12) & 0x3f];
			dest[d++] = encodeTable[(i >>> 6) & 0x3f];
			dest[d++] = encodeTable[i & 0x3f];

			if (isMultiLine && ++cc == 19 && d < destlen - 2) {
				dest[d++] = '\r';
				dest[d++] = '\n';
				cc        = 0;
			}
		}

		int left = len - evenlen;// 剩余位数
		if (left > 0) {
			int i = ((arr[evenlen] & 0xff) << 10) | (left == 2 ? ((arr[len - 1] & 0xff) << 2) : 0);

			dest[destlen - 4] = encodeTable[i >> 12];
			dest[destlen - 3] = encodeTable[(i >>> 6) & 0x3f];

			if (isUrlSafe) {
				// 在URL Safe模式下，=为URL中的关键字符，不需要补充。空余的byte位要去掉。
				int urlSafeLen = destlen - 2;
				if (2 == left) {
					dest[destlen - 2] = encodeTable[i & 0x3f];
					urlSafeLen += 1;
				}
				byte[] urlSafeDest = new byte[urlSafeLen];
				System.arraycopy(dest, 0, urlSafeDest, 0, urlSafeLen);
				return urlSafeDest;
			} else {
				dest[destlen - 2] = (left == 2) ? encodeTable[i & 0x3f] : (byte) '=';
				dest[destlen - 1] = '=';
			}
		}
		return dest;
	}


	/**
	 * base64解码
	 *
	 * @param source 被解码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static String decodeStr(CharSequence source) {
		return decodeStr(source, DEFAULT_CHARSET);
	}

	/**
	 * base64解码
	 *
	 * @param source  被解码的base64字符串
	 * @param charset 字符集
	 * @return 被加密后的字符串
	 */
	public static String decodeStr(CharSequence source, Charset charset) {
		return StrHelper.str(decode(source), charset);
	}

	/**
	 * base64解码
	 *
	 * @param source 被解码的base64字符串
	 * @return 被加密后的字符串
	 */
	public static byte[] decode(CharSequence source) {
		return decode(StrHelper.bytes(source, DEFAULT_CHARSET));
	}

	/**
	 * 解码Base64
	 *
	 * @param in 输入
	 * @return 解码后的bytes
	 */
	public static byte[] decode(byte[] in) {
		if (ArrayHelper.isEmpty(in)) {
			return in;
		}
		return decode(in, 0, in.length);
	}

	/**
	 * 解码Base64
	 *
	 * @param in     输入
	 * @param pos    开始位置
	 * @param length 长度
	 * @return 解码后的bytes
	 */
	public static byte[] decode(byte[] in, int pos, int length) {
		if (ArrayHelper.isEmpty(in)) {
			return in;
		}

		final IntWrapper offset = new IntWrapper(pos);

		byte   sestet0;
		byte   sestet1;
		byte   sestet2;
		byte   sestet3;
		int    maxPos  = pos + length - 1;
		int    octetId = 0;
		byte[] octet   = new byte[length * 3 / 4];// over-estimated if non-base64 characters present
		while (offset.value <= maxPos) {
			sestet0 = getNextValidDecodeByte(in, offset, maxPos);
			sestet1 = getNextValidDecodeByte(in, offset, maxPos);
			sestet2 = getNextValidDecodeByte(in, offset, maxPos);
			sestet3 = getNextValidDecodeByte(in, offset, maxPos);

			if (PADDING != sestet1) {
				octet[octetId++] = (byte) ((sestet0 << 2) | (sestet1 >>> 4));
			}
			if (PADDING != sestet2) {
				octet[octetId++] = (byte) (((sestet1 & 0xf) << 4) | (sestet2 >>> 2));
			}
			if (PADDING != sestet3) {
				octet[octetId++] = (byte) (((sestet2 & 3) << 6) | sestet3);
			}
		}

		if (octetId == octet.length) {
			return octet;
		} else {
			// 如果有非Base64字符混入，则实际结果比解析的要短，截取之
			return (byte[]) ArrayHelper.copy(octet, new byte[octetId], octetId);
		}
	}

	/**
	 * 获取下一个有效的byte字符
	 *
	 * @param in     输入
	 * @param pos    当前位置，调用此方法后此位置保持在有效字符的下一个位置
	 * @param maxPos 最大位置
	 * @return 有效字符，如果达到末尾返回
	 */
	private static byte getNextValidDecodeByte(byte[] in, IntWrapper pos, int maxPos) {
		byte base64Byte;
		byte decodeByte;
		while (pos.value <= maxPos) {
			base64Byte = in[pos.value++];
			if (base64Byte > -1) {
				decodeByte = DECODE_TABLE[base64Byte];
				if (decodeByte > -1) {
					return decodeByte;
				}
			}
		}
		// padding if reached max position
		return PADDING;
	}

	/**
	 * int包装，使之可变
	 *
	 * @author looly
	 */
	private static class IntWrapper {
		int value;

		IntWrapper(int value) {
			this.value = value;
		}
	}

}
