package com.seele2.encrypt.core;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * 列权限过滤，将没有权限的字段直接置空
 */
@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}))
public class ColumnInterceptor implements Interceptor {

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List<Object> rows = (List<Object>) invocation.proceed();
        for (Object row : rows) {
            if (row instanceof Map) {
                handleMap((Map<String, Object>) row);
            }
            else {
                handleEntity(rows);
            }
        }
        return rows;
    }

    private void handleEntity(List<Object> rows) {

    }

    private void handleMap(Map<String, Object> row) {

    }

}
