package com.seele2.encrypt.base;

import com.seele2.encrypt.core.EncryptJude;
import com.seele2.encrypt.entity.EncryptTable;
import com.seele2.encrypt.tool.FieldTool;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DefaultEncryptJude implements EncryptJude {

	// 直接忽略的字段
	private static final String ignored = "serialVersionUID";

	private final List<EncryptTable> encryptTables;

	public DefaultEncryptJude(List<EncryptTable> encryptTables) {
		this.encryptTables = encryptTables;
	}

	/**
	 * TODO 数据库表与别名校验
	 */
	@Override
	public boolean encrypt(String k) {

		String[] split = k.split("\\.");
		String   name  = split[0].toLowerCase().replace("`", "");
		String   field = split[1];

		if (Objects.equals(field, ignored)) return false;

		for (EncryptTable table : encryptTables) {
			if (Objects.equals(table.getName().toLowerCase(), name)) {
				Set<String> fields = table.getFields();
				for (String f : fields) {
					if (Objects.equals(FieldTool.getSnakeName(field), FieldTool.getSnakeName(f))) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
