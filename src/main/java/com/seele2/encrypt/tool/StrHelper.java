package com.seele2.encrypt.tool;

import lombok.NonNull;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StrHelper {

	private StrHelper() {
	}



    /**
     * 字符串是否为空
     *
     * @param cs 字符串
     * @return 是否是空串
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串长度
     *
     * @param cs 字段串
     * @return 长度
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }



    /**
	 * 目标字符串出现得次数
	 *
	 * @param tar 目标字符串
	 * @param str 字符串
	 * @return 次数
	 */
	public static int contains(final String tar, @NonNull final String str) {
		int c = 0, p = 0, l = tar.length();
		while (str.indexOf(tar, p) > -1) {
			p = str.indexOf(tar, p) + l;
			c++;
		}
		return c;
	}

	/**
	 * 编码字符串
	 *
	 * @param str     字符串
	 * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 编码后的字节码
	 */
	public static byte[] bytes(final CharSequence str, Charset charset) {
		if (str == null) {
			return null;
		}
		if (null == charset) {
			return str.toString().getBytes();
		}
		return str.toString().getBytes(charset);
	}

	/**
	 * 解码字节码
	 *
	 * @param data    字符串
	 * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
	 * @return 解码后的字符串
	 */
	public static String str(final byte[] data, final Charset charset) {
		if (data == null) {
			return null;
		}
		if (null == charset) {
			return new String(data);
		}
		return new String(data, charset);
	}

	/**
	 * 字符串拆分
	 *
	 * @param str 字符串
	 * @return 拆分后的字符串
	 */
	public static List<String> breakup(@NonNull final String str) {
		return Stream.iterate(0, i -> ++i).limit(str.length())
				.map(i -> "" + str.charAt(i))
				.collect(Collectors.toList());
	}

}
