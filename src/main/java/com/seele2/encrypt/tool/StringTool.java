package com.seele2.encrypt.tool;

import lombok.NonNull;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StringTool {

    private StringTool() {
    }

    /**
     * 加对象转为String
     *
     * @param s 对象
     * @return
     */
    public static String transToStr(Object s) {
        if (s instanceof String) {
            return (String) s;
        }
        return String.valueOf(s);
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
    public static String toStr(final byte[] data, final Charset charset) {
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
