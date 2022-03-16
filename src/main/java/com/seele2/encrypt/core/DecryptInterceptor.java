package com.seele2.encrypt.core;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.base.SafetyCipher;
import com.seele2.encrypt.manager.SafetyManager;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;

@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}))
public class DecryptInterceptor implements Interceptor {


    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        List<Object> rows = (List<Object>) invocation.proceed();
        rows.forEach(this::handle);
        return rows;
    }

    @SuppressWarnings("unchecked")
    private void handle(Object res) {
        if (res instanceof Map) {
            decrypt((Map<String, Object>) res);
        }
        else {
            MetaObject  meta   = SystemMetaObject.forObject(res);
            Class<?>    clazz  = res.getClass();
            List<Field> fields = getFields(clazz);
            fields.parallelStream().filter(field -> field.isAnnotationPresent(Safety.class)).forEach(field -> decrypt(meta, field));
        }
    }

    private void decrypt(Map<String, Object> map) {
        map.forEach((k, v) -> {
            if (Objects.isNull(v)) return;
            if (SafetyManager.isPresent(k)) {
                SafetyCipher cipher = SafetyManager.getDecryptCipher(k);
                map.put(k, cipher.decrypt(v));
            }
        });
    }

    private void decrypt(MetaObject meta, Field field) {
        String name  = field.getName();
        Object value = meta.getValue(name);
        if (Objects.isNull(value)) return;
        SafetyCipher cipher = SafetyManager.getDecryptCipher(field);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        meta.setValue(name, cipher.decrypt(value));
    }

    /**
     * 获取目标对象声明的全部字段，包括继承的字段
     *
     * @param clazz 对象类型
     * @return 声明字段
     */
    private List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

}
