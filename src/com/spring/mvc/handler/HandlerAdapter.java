package com.spring.mvc.handler;

import com.spring.mvc.model.Model;
import com.spring.mvc.model.ModelAndView;
import com.spring.mvc.model.ModelMap;
import com.spring.mvc.config.RequestConfig;
import com.spring.mvc.config.RequestParamConfig;
import com.spring.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HandlerAdapter {

    public ModelAndView handlerAdapter(HttpServletRequest request, HttpServletResponse response, RequestConfig config,
                                       Object controller) throws Exception {

        String className = config.getClassName();
        String methodName = config.getMethod();
        Model model = new ModelMap();
        Class<?>[] paramTypes = config.getParamTypes();

        Class<?> cls = Class.forName(className);
        Method method = cls.getMethod(methodName, paramTypes);
        // 数据绑定：HttpServletRequest, HttpServletResponse, HttpSession, Model
        List<RequestParamConfig> requestParamConfigs = config.getParamList();
        Object[] objects = new Object[requestParamConfigs.size()];
        int i = 0;
        for (RequestParamConfig requestParamConfig : requestParamConfigs) {
            String name = requestParamConfig.getName();
            Class<?> type = requestParamConfig.getType();
            System.out.println(name + ":" + type);

            String parameter = request.getParameter(name);
            if (type == HttpServletRequest.class) {
                objects[i++] = request;
            } else if (type == HttpServletResponse.class) {
                objects[i++] = response;
            } else if (type == HttpSession.class) {
                objects[i++] = request.getSession();
            } else if (type == Model.class) {
                objects[i++] = model;
            } else {
                if (isBasicType(type)) {
                    
                    // 基本数据类型转换
                    try {
                        if (StringUtils.isEmpty(parameter)) {
                            if (!StringUtils.isEmpty(requestParamConfig.getDefaultValue())) {
                                objects[i++] = parse(requestParamConfig.getDefaultValue(), type);
                            } else {                                
                                objects[i++] = null;
                            }
                        } else {
                            objects[i++] = parse(parameter, type);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        //转换失败，使用默认值
                        objects[i++] = null;
                    }
                    
                } else {
                    // 对象类型转换
                    Object object = type.newInstance();
                    
                    for (Field field : type.getDeclaredFields()) {
                        String fieldName = field.getName();

                        Class<?> fieldType = field.getType();
                        String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        Method setMethod = type.getMethod(setMethodName, fieldType);

                        String value = request.getParameter(fieldName);
                        if (!StringUtils.isEmpty(value)) {
                            setMethod.invoke(object, parse(value, fieldType));
                        }
                    }
                    objects[i++] = object;
                }

            }
        }

        Object result = method.invoke(controller, objects);
        ModelAndView modelAndView = null;
        if (result instanceof String) {
            modelAndView = new ModelAndView((String)result, (ModelMap)model);
        } else if (result instanceof ModelAndView) {
            modelAndView = (ModelAndView)result;
        }
        return modelAndView;
    }

    private Object parse(String value, Class<?> type) {
        Object object = value;

        // class 判断包装类型， TYPE 判断基本类型
        if (type == Integer.class || type == Integer.TYPE) {
            object = Integer.parseInt(value);
        } else if (type == Double.class || type == Double.TYPE) {
            object = Double.parseDouble(value);
        } else if (type == Float.class || type == Float.TYPE) {
            object = Float.parseFloat(value);
        } else if (type == Long.class || type == Long.TYPE) {
            object = Long.parseLong(value);
        } else if (type == Byte.class || type == Byte.TYPE) {
            object = Byte.parseByte(value);
        } else if (type == Short.class || type == Short.TYPE) {
            object = Short.parseShort(value);
        }

        return object;
    }

    private boolean isBasicType(Class<?> type) {
        if (type == Integer.class || type == Integer.TYPE
                || type == Double.class || type == Double.TYPE
                || type == Float.class || type == Float.TYPE
                || type == Long.class || type == Long.TYPE
                || type == Byte.class || type == Byte.TYPE
                || type == Short.class || type == Short.TYPE
                || type == String.class) {
            return true;
        }
        
        return false;
    }
}
