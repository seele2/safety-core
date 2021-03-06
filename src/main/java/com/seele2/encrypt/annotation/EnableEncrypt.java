package com.seele2.encrypt.annotation;

import com.seele2.encrypt.EncryptStarter;
import com.seele2.encrypt.actuator.FlushActuator;
import com.seele2.encrypt.conf.RegisterInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({EncryptStarter.class, FlushActuator.class, RegisterInterceptor.class})
public @interface EnableEncrypt {
}
