package com.spring.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BeanPropertyRowMapper<T> implements RowMapper<T> {

    private Class<T> c;

    public BeanPropertyRowMapper(Class<T> c) {
        this.c = c;
    }

    @Override
    public T mapRow(ResultSet resultSet, int i) throws Exception {
        Field[] fields = c.getDeclaredFields();
        T data = c.newInstance();

        for (Field f : fields) {
            String name = f.getName();

            Class<?> type = f.getType();
            String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
            Method method = c.getMethod(methodName, type);

            name = humpToLine(name);
            Object object = resultSet.getObject(name);
            method.invoke(data, object);
        }
        return data;
    }

    private Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线
     * @param str
     * @return
     */
    private String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}