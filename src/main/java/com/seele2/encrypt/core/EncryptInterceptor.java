package com.seele2.encrypt.core;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.enums.SafetyScopeEnum;
import com.seele2.encrypt.manager.SafetyManager;
import com.seele2.encrypt.tool.FieldTool;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 加密拦截器，依托于 mybatis 的 Param 注解的参数名进行判定处理
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@SuppressWarnings({"unchecked", "rawtypes"})
public class EncryptInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        encrypt(invocation);
        return invocation.proceed();
    }

    private void encrypt(Invocation invocation) {
        Object param = invocation.getArgs()[1];
        if (param instanceof Map) {
            encryptMap((Map<String, Object>) param);
        }
        else {
            encryptEntity(param);
        }
    }

    private void encryptEntity(Object param) {
        Class<?>   clazz  = param.getClass();
        MetaObject meta   = SystemMetaObject.forObject(param);
        Set<Field> fields = FieldTool.getFields(clazz);
        fields.stream()
                .filter(field -> field.isAnnotationPresent(Safety.class) && !field.getAnnotation(Safety.class).scope().equals(SafetyScopeEnum.DECRYPT))
                .forEach(field -> {
                    String fieldName = field.getName();
                    Object value     = meta.getValue(fieldName);
                    if (Objects.isNull(value)) return;
                    SafetyCipher cipher = SafetyManager.getSafetyCipher(fieldName);
                    meta.setValue(fieldName, cipher.encrypt(value));
                });
    }

    private void encryptMap(Map<String, Object> map) {
        map.forEach((k, v) -> {
            if (SafetyManager.isPresent(k)) {
                SafetyCipher cipher = SafetyManager.getSafetyCipher(k);
                map.put(k, cipher.encrypt(v));
            }
        });
    }
}
