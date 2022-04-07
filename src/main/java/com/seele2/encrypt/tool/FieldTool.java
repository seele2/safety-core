package com.seele2.encrypt.tool;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldTool {

    @Getter
    public enum BasenameType {
        /**
         * 驼峰
         */
        Camel(name -> {
            Matcher      matcher = Pattern.compile("_(\\w)").matcher(name.toLowerCase());
            StringBuffer sb      = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);
            return sb.toString();
        }),
        /**
         * 蛇形
         */
        Snake(name -> {
            Matcher      matcher = Pattern.compile("[A-Z]").matcher(name);
            StringBuffer sb      = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            }
            matcher.appendTail(sb);
            return sb.toString();
        });

        private final Function<String, String> fun;

        BasenameType(Function<String, String> fun) {
            this.fun = fun;
        }
    }

    /**
     * 获取目标对象声明的全部字段，包括继承的字段
     *
     * @param clazz 对象类型
     * @return 声明字段
     */
    public static Set<Field> getFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }


    /**
     * 获取字段名
     *
     * @param field 字段
     * @return 字段的本名、蛇形名、驼峰名
     */
    public static String[] getBasename(Field field) {
        String[] basename = new String[3];
        basename[0] = field.getName();
        basename[1] = getFieldName(field, BasenameType.Snake);
        basename[2] = getFieldName(field, BasenameType.Camel);
        return basename;
    }

    /**
     * 获取指定类型的字段名
     *
     * @param field 字段
     * @param type  名称类型
     * @return 字段名称
     */
    public static String getFieldName(Field field, BasenameType type) {
        return type.fun.apply(field.getName());
    }


}
