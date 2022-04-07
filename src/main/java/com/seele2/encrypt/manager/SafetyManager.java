package com.seele2.encrypt.manager;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.core.SafetyCipher;
import com.seele2.encrypt.tool.FieldTool;
import com.seele2.encrypt.tool.SimpleCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Component
public class SafetyManager {

    private static final SimpleCache<String, Field> FIELD_POOL = new SimpleCache<>();

    private static final SimpleCache<String, SafetyCipher> CIPHER_POOL = new SimpleCache<>();

    /**
     * 名称 是否有对应字段存在校验
     *
     * @param key 字段名称
     * @return true / false
     */
    public static boolean isPresent(String key) {
        return FIELD_POOL.isPresent(key);
    }

    /**
     * 获取解密器
     *
     * @param key 字段名
     * @return 解密器
     */
    public static SafetyCipher getSafetyCipher(String key) {
        SafetyCipher safetyCipher = CIPHER_POOL.getValue(key);
        if (null == safetyCipher) {
            safetyCipher = CipherManager.getSafetyCipher(FIELD_POOL.getValue(key).getAnnotation(Safety.class).cipher());
            CIPHER_POOL.put(key, safetyCipher);
        }
        return safetyCipher;
    }

    public static void addField(Field... fields) {
        Arrays.stream(fields).parallel().filter(field -> field.isAnnotationPresent(Safety.class)).forEach(field -> {
            Safety   annotation = field.getAnnotation(Safety.class);
            String[] alias      = annotation.alias();
            String[] basename   = FieldTool.getBasename(field);
            Stream.concat(Arrays.stream(alias), Stream.of(basename)).filter(StringUtils::isNotBlank)
                    .forEach(name -> FIELD_POOL.put(name, field));
        });
    }
}
