package com.seele2.encrypt.conf;

import com.seele2.encrypt.EncryptFlushType;
import com.seele2.encrypt.base.EncryptCipher;
import com.seele2.encrypt.base.EncryptJude;
import com.seele2.encrypt.core.*;
import com.seele2.encrypt.lazy.DefaultEncryptCipher;
import com.seele2.encrypt.lazy.DefaultEncryptJude;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Bean
	@ConditionalOnMissingBean(EncryptInterceptor.class)
	public EncryptInterceptor encryptInterceptor() {
		return new EncryptInterceptor(cipher(), jude(), !Objects.equals(encryptConf.getFlushType(), EncryptFlushType.DECRYPT));
	}

}
