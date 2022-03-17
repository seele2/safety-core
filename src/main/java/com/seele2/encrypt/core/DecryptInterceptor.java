package com.seele2.encrypt.core;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.manager.SafetyManager;
import com.seele2.encrypt.tool.FieldTool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}))
public class DecryptInterceptor implements Interceptor {

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        StopWatch watch = new StopWatch("解密统计");
        watch.start("获取数据");
        List<Object> rows = (List<Object>) invocation.proceed();
        watch.stop();
        watch.start("解密数据");
        decrypt(rows);
        watch.stop();
        System.out.println(watch.prettyPrint());
        return rows;
    }

    @SuppressWarnings("unchecked")
    private void decrypt(List<Object> rows) {
        if (CollectionUtils.isEmpty(rows)) return;
        Object o = rows.get(0);
        if (o instanceof Map) {
            rows.parallelStream().forEach(i -> doDecryptMap((Map<String, Object>) i));
        }
        else {
            final List<Field> fields = FieldTool.getFields(o.getClass()).stream()
                    .filter(field -> field.isAnnotationPresent(Safety.class))
                    .collect(Collectors.toList());
            rows.parallelStream().forEach(i -> doDecryptEntity(SystemMetaObject.forObject(i), fields));
        }
    }

    private void doDecryptEntity(MetaObject meta, List<Field> fields) {
        fields.forEach(field -> {
            String fieldName = field.getName();
            Object value     = meta.getValue(fieldName);
            if (Objects.isNull(value)) return;
            SafetyCipher cipher = SafetyManager.getDecryptCipher(fieldName);
            meta.setValue(fieldName, cipher.decrypt(value));
        });
    }

    private void doDecryptMap(Map<String, Object> map) {
        map.forEach((k, v) -> {
            if (Objects.isNull(v)) return;
            if (SafetyManager.isPresent(k)) {
                SafetyCipher cipher = SafetyManager.getDecryptCipher(k);
                map.put(k, cipher.decrypt(v));
            }
        });
    }
}
