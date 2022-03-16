package com.seele2.encrypt.factory;

import com.seele2.encrypt.base.SafetyCipher;
import com.seele2.encrypt.enums.SafetyCipherEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CipherFactory implements ApplicationListener<ContextRefreshedEvent> {

    private static final Map<SafetyCipherEnum, SafetyCipher> maps = new HashMap<>();

    public static SafetyCipher getEncryptCipher(SafetyCipherEnum cipher) {
        SafetyCipher encryptCipher = maps.get(cipher);
        int          loop          = 0;
        while (null == encryptCipher) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                if (loop >= 3) {
                    throw new RuntimeException("plug init failed !");
                }
                loop++;
            }
            encryptCipher = maps.get(cipher);
        }
        return encryptCipher;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, SafetyCipher> beansOfType = contextRefreshedEvent.getApplicationContext().getBeansOfType(SafetyCipher.class);
        beansOfType.forEach((__, v) -> maps.put(v.getType(), v));
    }
}
