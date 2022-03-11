package com.seele2.encrypt.core;

import com.seele2.encrypt.annotation.Decrypt;
import com.seele2.encrypt.base.EncryptCipher;
import com.seele2.encrypt.tool.StrHelper;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * TODO 仅对实体生效
 */
@Intercepts(
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
)
public class DecryptInterceptor implements Interceptor {

    private final EncryptCipher encryptCipher;

    public DecryptInterceptor(EncryptCipher encryptCipher) {
        this.encryptCipher = encryptCipher;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        List<Object> rows = (List<Object>) invocation.proceed();
        rows.forEach(this::decrypt);
        return rows;
    }


    private void decrypt(Object res) {
        MetaObject  meta   = SystemMetaObject.forObject(res);
        Class<?>    clazz  = res.getClass();
        List<Field> fields = getFields(clazz);
        fields.parallelStream()
                .filter(field -> field.isAnnotationPresent(Decrypt.class))
                .forEach(field -> decrypt(meta, field))
        ;
    }

    private void decrypt(MetaObject meta, Field field) {
        String fieldName = field.getName();
        Object value     = meta.getValue(fieldName);
        if (Objects.isNull(value)) return;
        if (Objects.equals(String.class, meta.getGetterType(fieldName)) && !StrHelper.isBlank((String) value)) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            meta.setValue(fieldName, encryptCipher.decrypt(value));
        }
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
