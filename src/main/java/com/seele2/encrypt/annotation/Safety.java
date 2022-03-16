package com.seele2.encrypt.annotation;


import com.seele2.encrypt.enums.SafetyCipherEnum;
import com.seele2.encrypt.enums.SafetyScopeEnum;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Safety {

    /**
     * 执行范围  加密 / 解密 / 全部
     *
     * @see SafetyScopeEnum
     */
    SafetyScopeEnum scope() default SafetyScopeEnum.ALL;

    /**
     * 数据字段别名
     * <p>
     * 当返回非实体时会根据别名进行加解密处理
     */
    String[] alias() default "";


    /**
     * 加解密处理器
     */
    SafetyCipherEnum cipher() default SafetyCipherEnum.DEFAULT;


}
