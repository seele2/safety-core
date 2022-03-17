package com.seele2.encrypt.conf;

import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.core.EncryptJude;
import com.seele2.encrypt.core.*;
import com.seele2.encrypt.base.DefaultSafetyCipher;
import com.seele2.encrypt.base.DefaultEncryptJude;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EncryptAutoConfiguration {

    @Autowired
    private EncryptConf encryptConf;

    @Bean
    @ConditionalOnMissingBean(SafetyCipher.class)
    public SafetyCipher cipher() {
        return new DefaultSafetyCipher();
    }

    @Bean
    @ConditionalOnMissingBean(EncryptJude.class)
    public EncryptJude jude() {
        return new DefaultEncryptJude(encryptConf.getEncryptTables());
    }


    @Bean
    @ConditionalOnMissingBean(DecryptInterceptor.class)
    public DecryptInterceptor decryptInterceptor() {
        return new DecryptInterceptor();
    }


    @Bean
    @ConditionalOnMissingBean(EncryptInterceptor.class)
    public EncryptInterceptor encryptInterceptor() {
        return new EncryptInterceptor( true);
    }


}
