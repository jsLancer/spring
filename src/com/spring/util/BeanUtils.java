package com.spring.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtils {

    public static Method getSetMethod(Object object, String name) {

        Method method = null;
        try {
            // 1. 分析 Bean 对象 => BeanInfo
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            // 2. 根据 BeanInfo 获得所有属性的描述器
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            // 3. 遍历属性描述器
            for (PropertyDescriptor p : propertyDescriptors) {
                if (p.getName().equals(name)) {
                    // 找到 set 方法
                    method = p.getWriteMethod();

                    break;
                }
            }
            // 4. 找到 set 方法
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        return method;
    }

    /**
     * 当 source 某属性为空时，不 copy 到 target
     *
     * @param source
     * @param target
     * @param editable
     */
    public static void copyProperties(Object source, Object target, Class<?> editable) {

        Field[] fields = editable.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            String fieldName = field.getName();

            String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getMethod = null;
            Method setMethod = null;
            try {
                getMethod = editable.getMethod(getMethodName);
                setMethod = editable.getMethod(setMethodName, type);
                Object object = getMethod.invoke(source);
                if (object != null) {
                    setMethod.invoke(target, object);
                }

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

}