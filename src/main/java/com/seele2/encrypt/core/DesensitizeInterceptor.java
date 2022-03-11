package com.seele2.encrypt.core;

import com.seele2.encrypt.annotation.Desensitize;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName DesensitizeInterceptor
 * @Author Zeng Guangfu
 * @Description 脱敏过滤器
 * @Version 1.0
 */
@Intercepts(
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
)
public class DesensitizeInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List<Object> rows = (List<Object>)invocation.proceed();
        rows.forEach(this::desensitize);
        return rows;
    }

    /**
     * 每一个对象遍历其属性，有脱敏注解才脱敏
     * @param o     mybatis返回的每一个结果
     */
    private void desensitize(Object o) {
        MetaObject metaObject = SystemMetaObject.forObject(o);
        Class<?> clazz = o.getClass();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Desensitize.class))
                .forEach(field -> handleDesensitize(metaObject, field));
    }

    /**
     * 处理脱敏
     * @param metaObject    带value的包装对象
     * @param field         字段
     */
    private void handleDesensitize(MetaObject metaObject, Field field) {
        String fieldName = field.getName();
        Object value = metaObject.getValue(fieldName);
        if (String.class == metaObject.getGetterType(fieldName) && !StringUtils.isEmpty(value)) {
            Desensitize desensitize = field.getAnnotation(Desensitize.class);
            Object desensitizeResult = desensitize.type().getFunc().apply((String) value);
            field.setAccessible(true);
            metaObject.setValue(fieldName, desensitizeResult);
        }
    }


}
