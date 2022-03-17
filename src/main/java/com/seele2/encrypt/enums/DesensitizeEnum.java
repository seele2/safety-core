package com.seele2.encrypt.enums;

import java.util.function.Function;

/**
 * @ClassName DesensitizeEnum
 * @Author Zeng Guangfu
 * @Description 枚举类型，脱敏的执行方式
 * @Version 1.0
 */
public enum DesensitizeEnum {

    /**
     * 用户名
     * <p>
     * example:
     * <pre>
     *      王   ->  王
     *      王某  -> 王*
     *      王某某  -> 王*某
     *      王某某某 -> 王*某某
     * </pre>
     */
    USERNAME(source -> source.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),

    /**
     * 密码处理
     * <p>
     * example:
     * <pre>
     *      password1234   ->  **********
     * </pre>
     */
    PASSWORD(source -> "**********"),

    /**
     * 手机号
     * <p>
     * example:
     * <pre>
     *      13012341301   ->  130****1301
     * </pre>
     */
    PHONE(source -> source.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

    /**
     * 身份证
     * <p>
     * example:
     * <pre>
     *      110101199003070396   ->  1101**********0396
     * </pre>
     */
    ID_CARD(source -> source.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1**********$2")),

    /**
     * 地址
     * <p>
     * example:
     * <pre>
     * </pre>
     */
    ADDRESS(source -> source.replaceAll("(\\S{5})\\S{4}(\\S*)\\S{5}", "$1****$2****")),
    ;

    private final Function<String, String> func;

    DesensitizeEnum(Function<String, String> func) {
        this.func = func;
    }

    public Function<String, String> getFunc() {
        return func;
    }
}
