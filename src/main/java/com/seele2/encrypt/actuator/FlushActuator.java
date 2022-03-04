package com.seele2.encrypt.actuator;

import com.seele2.encrypt.EncryptFlushType;
import com.seele2.encrypt.base.EncryptFlusher;
import com.seele2.encrypt.conf.EncryptConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FlushActuator implements CommandLineRunner {

	private final EncryptConf encryptConf;

	@Autowired(required = false)
	private EncryptFlusher flusher;

	public FlushActuator(EncryptConf encryptConf) {
		this.encryptConf = encryptConf;
	}

	@Override
	public void run(String... args) {
		EncryptFlushType type = encryptConf.getFlushType();
		switch (type) {
			case EMPTY:
				break;
			case DECRYPT:
				selfCheck();
				flusher.decrypt();
				break;
			case ENCRYPT:
				selfCheck();
				flusher.encrypt();
				break;
		}
	}

	private void selfCheck() {
		if (Objects.isNull(flusher)) {
			throw new RuntimeException("未找到加解密清洗器：com.jiuzhou.encrypt.base.EncryptFlusher  ");
		}
	}
}
