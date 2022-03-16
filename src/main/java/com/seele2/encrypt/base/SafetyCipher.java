package com.seele2.encrypt.base;

import com.seele2.encrypt.enums.SafetyCipherEnum;

public interface SafetyCipher {

    SafetyCipherEnum getType();

    String encrypt(Object s);

    String decrypt(Object s);

}
