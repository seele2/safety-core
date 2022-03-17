package com.seele2.encrypt.conf;

import com.seele2.encrypt.cipher.Base64Cipher;
import com.seele2.encrypt.core.DecryptInterceptor;
import com.seele2.encrypt.core.EncryptInterceptor;
import com.seele2.encrypt.cipher.DefaultCipher;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EncryptAutoConfiguration {

    @Bean
    public Base64Cipher base64Cipher() { return new Base64Cipher(); }

    @Bean
    public DefaultCipher defaultCipher() {
        return new DefaultCipher();
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
