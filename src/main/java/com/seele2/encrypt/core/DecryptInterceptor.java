package com.seele2.encrypt.core;

import com.seele2.encrypt.annotation.Safety;
import com.seele2.encrypt.base.SafetyCipher;
import com.seele2.encrypt.manager.SafetyManager;
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

@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}))
public class DecryptInterceptor implements Interceptor {

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        StopWatch stopWatch = new StopWatch("解密统计");
        stopWatch.start("获取数据");
        List<Object> rows = (List<Object>) invocation.proceed();
        stopWatch.stop();
        stopWatch.start("数据解密");
        decrypt(rows);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
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
            Set<Field> fields = getFields(o.getClass());
            rows.parallelStream().forEach(i -> doDecryptEntity(i, fields));
        }
    }

    private void doDecryptEntity(Object row, Set<Field> fields) {
        fields.stream().filter(field -> field.isAnnotationPresent(Safety.class))
                .forEach(field -> decrypt(SystemMetaObject.forObject(row), field.getName()));
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

    private void decrypt(MetaObject meta, String fieldName) {
        Object value = meta.getValue(fieldName);
        if (Objects.isNull(value)) return;
        SafetyCipher cipher = SafetyManager.getDecryptCipher(fieldName);
        meta.setValue(fieldName, cipher.decrypt(value));
    }

    /**
     * 获取目标对象声明的全部字段，包括继承的字段
     *
     * @param clazz 对象类型
     * @return 声明字段
     */
    private Set<Field> getFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

}
