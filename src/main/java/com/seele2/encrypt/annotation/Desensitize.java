package com.seele2.encrypt.annotation;

import com.seele2.encrypt.entity.enums.DesensitizeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Desensitize {

    DesensitizeEnum type();
}
