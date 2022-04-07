package com.seele2.encrypt.cipher;

import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.enums.SafetyCipherEnum;
import com.seele2.encrypt.tool.StringTool;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * 变种Base64，以牺牲空间和性能来换取模糊查询的能力
 */
public class DefaultCipher implements SafetyCipher {

    private static final String targetStr = "==";

    private static final String replaceStr = "$";

    private static final int step = 4;

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public SafetyCipherEnum getType() {
        return SafetyCipherEnum.DEFAULT;
    }

    @Override
    public String encrypt(Object s) {
        List<String>  str = StringTool.breakup(StringTool.transToStr(s));
        StringBuilder sb  = new StringBuilder();
        str.forEach(i -> sb.append(encoder.encodeToString(StringTool.bytes(i, StandardCharsets.UTF_8))));
        return sb.toString().replace(targetStr, replaceStr);
    }

    @Override
    public String decrypt(Object s) {
        String        str = StringTool.transToStr(s).replace(replaceStr, targetStr);
        int           len = str.length();
        int           c   = (int) Math.ceil((double) len / step);
        StringBuilder sb  = new StringBuilder();
        for (int i = 0; i < c; i++) {
            sb.append(new String(decoder.decode(str.substring(i * step, Math.min(i * step + step, len)))));
        }
        return sb.toString();
    }


}
