package com.seele2.encrypt.core;

import com.seele2.encrypt.SqlType;
import com.seele2.encrypt.base.SafetyCipher;
import com.seele2.encrypt.base.EncryptJude;
import com.seele2.encrypt.entity.EncryptTable;
import com.seele2.encrypt.tool.StrHelper;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

/**
 * TODO 暂时不支持递归处理
 */
@Intercepts({
		@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@SuppressWarnings({"unchecked", "rawtypes"})
public class EncryptInterceptor implements Interceptor {

	private final Log log = LogFactory.getLog(getClass());

	private final SafetyCipher cipher;

	private final EncryptJude jude;

	private final boolean active;

	public EncryptInterceptor(SafetyCipher cipher, EncryptJude jude, boolean active) {
		this.cipher = cipher;
		this.jude   = jude;
		this.active = active;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (!active) return invocation.proceed();
		Object[]        args      = invocation.getArgs();
		MappedStatement statement = ((MappedStatement) args[0]);
		SqlCommandType  type      = statement.getSqlCommandType();
		Object          param     = args[1];
		String          sql       = getSql(statement, param);

		if (isComposite(sql)) {
			log.debug("not support composite sql, encrypt isn`t work!");
			return invocation.proceed();
		}

		try {
			EncryptTable table = extractTable(getSql(statement, param), type);
			if (pass(type)) {
				Object result = invocation.proceed();
//				decrypt(result, table);
				return result;
			} else {
				if (!Objects.isNull(param)) {
					args[1]    = encrypt(table, param);
					invocation = new Invocation(invocation.getTarget(), invocation.getMethod(), args);
				}
				Object result = invocation.proceed();
//				decrypt(result, table);
				return result;
			}
		} catch (Exception e) {
			return invocation.proceed();
		}

	}

	private boolean isComposite(String sql) {
		return Stream.of(
				StrHelper.contains(SqlType.INSERT.name(), sql),
				StrHelper.contains(SqlType.UPDATE.name(), sql),
				StrHelper.contains(SqlType.DELETE.name(), sql),
				StrHelper.contains(SqlType.SELECT.name(), sql)
		).mapToInt(i -> i).sum() > 1;
	}

	@Override
	public Object plugin(Object o) {
		return Plugin.wrap(o, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	private boolean pass(SqlCommandType type) {
		switch (type) {
			case INSERT:
			case UPDATE:
			case SELECT:
				return false;
			default:
				return true;
		}
	}


	private Object encrypt(EncryptTable table, Object param) {
		if (param instanceof Map) {
			doEncrypt((Map<String, Object>) param, table);
		} else if (param instanceof List) {
			doEncrypt((List) param, table);
		} else {
			return doEncrypt(param, table);
		}
		return param;
	}

	private boolean encryptCheck(String field, EncryptTable table) {
		return jude.encrypt(table.getName() + "." + field);
	}


	private EncryptTable extractTable(String sql, SqlCommandType type) {
		EncryptTable table = new EncryptTable();
		switch (type) {
			case INSERT:
				setTableInsert(table, sql);
				break;
			case UPDATE:
				setTableUpdate(table, sql);
				break;
			default:
				setTableSelect(table, sql);
		}
		return table;
	}

	private void setTableInsert(EncryptTable table, String sql) {
		String lowerCase = sql.toLowerCase();
		extractTableNameInsert(table, sql, lowerCase);
		extractTableFieldInsert(table, sql, lowerCase);
	}


	private void extractTableNameUpdate(EncryptTable table, String sql, String tmp) {
		table.setName(sql.substring(tmp.indexOf("update") + 6, tmp.indexOf("set")).trim());
	}


	// TODO 暂时只考虑一个条件请求参数
	private void extractTableFieldSelect(EncryptTable table, String sql, String tmp) {
		if (tmp.contains("where") && tmp.contains("?")) {
			String sub = sql.substring(tmp.indexOf("where") + 5, tmp.lastIndexOf('?')).trim();
			table.setFields(new HashSet<>(Arrays.asList(sub.split(" "))));
		}
	}

	private void extractTableFieldInsert(EncryptTable table, String sql, String tmp) {
		String   fields = sql.substring(tmp.indexOf('(') + 1, tmp.indexOf(')'));
		String[] split  = fields.split(",");
		for (String s : split) {
			table.getFields().add(s.trim());
		}
	}

	private void extractTableNameInsert(EncryptTable table, String sql, String tmp) {
		String name = sql.substring(tmp.indexOf("into") + 4, tmp.indexOf("value")).trim();
		if (name.contains(" ")) {
			name = name.split(" ")[0];
		}

		if (name.contains("(")) {
			name = name.substring(0, name.indexOf('('));
		}

		table.setName(name);
	}

	private void extractTableNameSelect(EncryptTable table, String sql, String tmp) {
		String name;
		if (tmp.contains("where")) {
			name = sql.substring(tmp.indexOf("from") + 4, tmp.indexOf("where")).trim();
		} else {
			name = sql.substring(tmp.indexOf("from")).trim();
		}
		if (name.contains(" ")) {
			String[] s = name.split(" ");
			table.setName(s[0]);
			table.setAlias(s[s.length - 1]);
		} else {
			table.setName(name);
		}
	}

	private void setTableUpdate(EncryptTable table, String sql) {
		String lowerCase = sql.toLowerCase();
		extractTableNameUpdate(table, sql, lowerCase);
		extractTableFieldUpdate(table, sql, lowerCase);
	}

	private Set<String> extractTableFieldUpdateSet(String set) {
		Set<String> list  = new HashSet<>();
		String[]    split = set.split(",");
		for (String s : split) {
			if (s.contains("?")) {
				String field = s.substring(0, s.indexOf('='));
				list.add(field.trim());
			}
		}
		return list;
	}

	private Set<String> extractTableFieldUpdateWhere(String where) {
		Set<String> list = new HashSet<>();
		// TODO 目前仅简单条件处理
		String[] split = where.split("(?i)and");
		for (String s : split) {
			if (s.contains("?")) {
				String field = s.substring(0, s.indexOf('='));
				list.add(field.trim());
			}
		}
		return list;
	}

	private void extractTableFieldUpdate(EncryptTable table, String sql, String tmp) {
		if (tmp.contains("where")) {
			String sub   = sql.substring(tmp.indexOf("set") + 3, tmp.indexOf("where")).trim();
			String where = sql.substring(tmp.indexOf("where") + 5);
			table.getFields().addAll(extractTableFieldUpdateSet(sub));
			table.getFields().addAll(extractTableFieldUpdateWhere(where));
		}
	}


	// TODO 暂时不考虑连表查询
	private void setTableSelect(EncryptTable table, String sql) {
		String lowerCase = sql.toLowerCase();
		extractTableNameSelect(table, sql, lowerCase);
		extractTableFieldSelect(table, sql, lowerCase);
	}

	private String getSql(MappedStatement statement, Object params) {
		return statement.getBoundSql(params).getSql().trim();
	}

	private void decrypt(Object o, EncryptTable table) {
		if (o instanceof Map) {
			doDecrypt((Map<String, Object>) o, table);
		} else if (o instanceof List) {
			doDecrypt((List) o, table);
		} else {
			doDecrypt(o, table);
		}
	}

	private void doEncrypt(List o, EncryptTable table) {
		for (Object i : o) {
			if (i instanceof Map) {
				doEncrypt((Map<String, Object>) o, table);
			} else {
				doEncrypt(i, table);
			}
		}
	}

	private void doEncrypt(Map<String, Object> o, EncryptTable table) {

		if (o.containsKey("First_PageHelper")) {
			return;
		}

		Set<Map.Entry<String, Object>> entries = o.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			String key   = entry.getKey();
			Object value = entry.getValue();

			if (Objects.equals("collection", key)) {
				doEncrypt((List) value, table);
			} else {
				if (isBaseValue(value)) {
					if (encryptCheck(key, table)) {
						o.put(key, cipher.encrypt(value));
					}
				} else {
					if (key.contains("param") && !Objects.isNull(value)) {
						doEncryptBean(value, table);
					}
				}
			}
		}
	}

	private Object doEncrypt(Object o, EncryptTable table) {
		if (isBaseValue(o)) {
			Set<String> fields = table.getFields();
			for (String field : fields) {
				if (encryptCheck(field, table)) {
					return cipher.encrypt(o);
				}
			}
			return o;
		} else {
			return doEncryptBean(o, table);
		}
	}

	private void doDecrypt(List o, EncryptTable table) {
		for (Object i : o) {
			if (i instanceof Map) {
				doDecrypt((Map<String, Object>) i, table);
			} else {
				doDecrypt(i, table);
			}
		}
	}

	private void doDecrypt(Map<String, Object> o, EncryptTable table) {
		o.forEach((k, v) -> {
			if (encryptCheck(k, table)) {
				o.put(k, cipher.decrypt(o.get(k)));
			}
		});
	}

	@SneakyThrows
	private void doDecrypt(Object o, EncryptTable table) {
		Field[] fs = o.getClass().getDeclaredFields();
		for (Field f : fs) {
			if (encryptCheck(f.getName(), table)) {
				f.setAccessible(true);
				f.set(o, cipher.decrypt(f.get(o)));
			}
		}
	}

	@SneakyThrows
	private Object doEncryptBean(Object o, EncryptTable table) {
		Field[] fs = o.getClass().getDeclaredFields();
		for (Field f : fs) {
			if (encryptCheck(f.getName(), table)) {
				f.setAccessible(true);
				f.set(o, cipher.encrypt(f.get(o)));
			}
		}
		return o;
	}

	private boolean isBaseValue(Object o) {
		return o instanceof String || o instanceof Number;
	}

}
