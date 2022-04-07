package com.seele2.encrypt.manager;

import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.tool.SimpleCache;
import com.seele2.encrypt.enums.SafetyCipherEnum;

import java.util.ServiceLoader;

public class CipherManager {

    private static final SimpleCache<SafetyCipherEnum, SafetyCipher> CIPHER_POOL;

    static {
        CIPHER_POOL = new SimpleCache<>();
        ServiceLoader<SafetyCipher> load = ServiceLoader.load(SafetyCipher.class);
        for (SafetyCipher next : load) {
            CIPHER_POOL.put(next.getType(), next);
        }
    }

    public static SafetyCipher getSafetyCipher(SafetyCipherEnum cipher) {
        SafetyCipher safetyCipher = CIPHER_POOL.getValue(cipher);
        if (null == safetyCipher) {
            throw new RuntimeException("not be supported!");
        }
        return safetyCipher;
    }

    public static void registerCipher(SafetyCipher cipher) {
        CIPHER_POOL.put(cipher.getType(), cipher);
    }


}
