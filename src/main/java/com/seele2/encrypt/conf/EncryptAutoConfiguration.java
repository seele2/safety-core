package com.seele2.encrypt.conf;

import com.seele2.encrypt.EncryptFlushType;
import com.seele2.encrypt.base.EncryptCipher;
import com.seele2.encrypt.base.EncryptJude;
import com.seele2.encrypt.core.*;
import com.seele2.encrypt.lazy.DefaultEncryptCipher;
import com.seele2.encrypt.lazy.DefaultEncryptJude;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Data
@Configuration
public class EncryptAutoConfiguration {

    @Autowired
    private EncryptConf encryptConf;

    @Bean
    @ConditionalOnMissingBean(EncryptCipher.class)
    public EncryptCipher cipher() {
        return new DefaultEncryptCipher();
    }

    @Bean
    @ConditionalOnMissingBean(EncryptJude.class)
    public EncryptJude jude() {
        return new DefaultEncryptJude(encryptConf.getEncryptTables());
    }

//    @Bean
//    @ConditionalOnMissingBean(EncryptInterceptor.class)
//    public EncryptInterceptor encryptInterceptor() {
//        return new EncryptInterceptor(cipher(), jude(), !Objects.equals(encryptConf.getFlushType(), EncryptFlushType.DECRYPT));
//    }

    /**
     * TODO
     *   DecryptInterceptor 一定要在 DesensitizeInterceptor 之前初始化
     *   后续考虑 AutoConfigureAfter 将 DesensitizeInterceptor 滞后
     *   目前依靠代码执行顺序即可
     */
    @Bean
    public DecryptInterceptor decryptInterceptor() {
        return new DecryptInterceptor(cipher());
    }

    @Bean
    @ConditionalOnMissingBean(DesensitizeInterceptor.class)
    public DesensitizeInterceptor desensitizeInterceptor() {
        return new DesensitizeInterceptor();
    }


}
