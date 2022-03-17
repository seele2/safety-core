package com.seele2.encrypt.factory;

import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.tool.SimpleCache;
import com.seele2.encrypt.enums.SafetyCipherEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class CipherFactory implements ApplicationListener<ContextRefreshedEvent> {

    private static final SimpleCache<SafetyCipherEnum, SafetyCipher> CIPHER_POOL = new SimpleCache<>();

    public static SafetyCipher getEncryptCipher(SafetyCipherEnum cipher) {
        SafetyCipher safetyCipher = CIPHER_POOL.getValue(cipher);
        // 理论上这里能获取到，但考虑到极端情况进行兼容处理
        if (null == safetyCipher) {
            throw new RuntimeException("safety init not complete！");
        }
        return safetyCipher;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        contextRefreshedEvent.getApplicationContext()
                .getBeansOfType(SafetyCipher.class)
                .forEach((__, v) -> CIPHER_POOL.put(v.getType(), v));
    }
}
