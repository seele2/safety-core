package com.seele2.encrypt.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CamelSnakeHelper {

	private CamelSnakeHelper(){}

	private static final Pattern SNAKE_PATTERN = Pattern.compile("_(\\w)");

	private static final Pattern CAMEL_PATTERN = Pattern.compile("[A-Z]");

	public static String toCamelCase(String str) {
		str = str.toLowerCase();
		Matcher      matcher = SNAKE_PATTERN.matcher(str);
		StringBuffer sb      = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}


	public static String toSnakeCase(String str) {
		Matcher      matcher = CAMEL_PATTERN.matcher(str);
		StringBuffer sb      = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

}
