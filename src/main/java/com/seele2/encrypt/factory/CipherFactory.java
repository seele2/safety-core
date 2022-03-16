package com.seele2.encrypt.factory;

import com.seele2.encrypt.base.SafetyCipher;
import com.seele2.encrypt.base.SimpleCache;
import com.seele2.encrypt.enums.SafetyCipherEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class CipherFactory implements ApplicationListener<ContextRefreshedEvent> {

    private static final SimpleCache<SafetyCipherEnum, SafetyCipher> CIPHER_POOL = new SimpleCache<>();

    public static SafetyCipher getEncryptCipher(SafetyCipherEnum cipher) {
        SafetyCipher encryptCipher = CIPHER_POOL.getValue(cipher);

        // TODO 应该使用自旋锁, 理论上这里能获取到，但考虑到极端情况进行兼容处理
        int loop = 0;
        while (null == encryptCipher) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {
                if (loop >= 3) {
                    throw new RuntimeException("plug init failed !");
                }
                loop++;
            }
            encryptCipher = CIPHER_POOL.getValue(cipher);
        }

        return encryptCipher;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        contextRefreshedEvent.getApplicationContext()
                .getBeansOfType(SafetyCipher.class)
                .forEach((__, v) -> CIPHER_POOL.put(v.getType(), v));
    }
}
