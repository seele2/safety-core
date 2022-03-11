package com.seele2.encrypt.entity.enums;

import java.util.function.Function;

/**
 * @ClassName DesensitizeEnum
 * @Author Zeng Guangfu
 * @Description 枚举类型，脱敏的执行方式
 * @Version 1.0
 */
public enum DesensitizeEnum {

    USERNAME(source -> source.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),

    PHONE(source -> source.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

    ID_CARD(source -> source.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1**********$2")),

    ADDRESS(source -> source.replaceAll("(\\S{8})\\S{4}(\\S*)\\S{4}", "$1****$2****")),
    ;

    private final Function<String, String> func;

    DesensitizeEnum(Function<String, String> func) {
        this.func = func;
    }

    public Function<String, String> getFunc() {
        return func;
    }
}
