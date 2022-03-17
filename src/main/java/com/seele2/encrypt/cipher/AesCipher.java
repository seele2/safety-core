package com.seele2.encrypt.cipher;

import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.enums.SafetyCipherEnum;

public class AesCipher implements SafetyCipher {
    @Override
    public SafetyCipherEnum getType() {
        return SafetyCipherEnum.AES;
    }

    @Override
    public String encrypt(Object s) {
        // TODO
        return null;
    }

    @Override
    public String decrypt(Object s) {
        // TODO
        return null;
    }
}
