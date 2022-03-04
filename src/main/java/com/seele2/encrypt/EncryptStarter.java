package com.seele2.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EncryptStarter implements ApplicationListener<ApplicationReadyEvent> {

	private final ApplicationContext container;

	public EncryptStarter(ApplicationContext container) {
		this.container = container;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationEvent) {
		if (container.containsBean("encryptInterceptor")) {
			log.info("已启用字段加密功能");
		} else {
			log.info("如需启用字段加密功能请开启 @EnableEncrypt ");
		}
	}
}
