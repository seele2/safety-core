package com.seele2.encrypt.conf;

import com.seele2.encrypt.core.DecryptInterceptor;
import com.seele2.encrypt.core.EncryptInterceptor;
import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.base.DefaultSafetyCipher;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EncryptAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SafetyCipher.class)
    public SafetyCipher cipher() {
        return new DefaultSafetyCipher();
    }

    @Bean
    @ConditionalOnMissingBean(DecryptInterceptor.class)
    public DecryptInterceptor decryptInterceptor() {
        return new DecryptInterceptor();
    }


    @Bean
    @ConditionalOnMissingBean(EncryptInterceptor.class)
    public EncryptInterceptor encryptInterceptor() {
        return new EncryptInterceptor();
    }


}
