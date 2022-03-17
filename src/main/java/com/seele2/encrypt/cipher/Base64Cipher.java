package com.seele2.encrypt.cipher;

import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.enums.SafetyCipherEnum;
import com.seele2.encrypt.tool.StringTool;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Cipher implements SafetyCipher {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public SafetyCipherEnum getType() {
        return SafetyCipherEnum.BASE64;
    }

    @Override
    public String encrypt(Object s) {
        byte[] bytes = StringTool.bytes(StringTool.transToStr(s), StandardCharsets.UTF_8);
        return encoder.encodeToString(bytes);
    }

    @Override
    public String decrypt(Object s) {
        byte[] decode = decoder.decode(StringTool.transToStr(s));
        return new String(decode, StandardCharsets.UTF_8);
    }
}
