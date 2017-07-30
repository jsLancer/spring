package com.spring.ioc.context;

import com.spring.ioc.TransactionProxy;
import com.spring.ioc.annotation.Service;
import com.spring.ioc.annotation.Transactional;
import com.spring.ioc.config.Bean;
import com.spring.ioc.config.ConfigManager;
import com.spring.ioc.config.Property;
import com.spring.util.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApplicationContext implements BeanFactory {

    private Map<String, Bean> config = new HashMap<String, Bean>();

    // 存储对象容器
    private Map<String, Object> context = new HashMap<>();

    private String path = null;

    public ApplicationContext(String path) {
        this.path = path;
        config.putAll(ConfigManager.getConfig(path));

        Bean jdbcBean = new Bean();
        jdbcBean.setId("jdbcTemplate");
        jdbcBean.setClassName("com.spring.jdbc.JdbcTemplate");
        jdbcBean.setProperties(new ArrayList<Property>());
        jdbcBean.setScope(Bean.SINGLETON);
        config.put("jdbcTemplate", jdbcBean);

        for (String s : config.keySet()) {
            System.out.println(s + "-->" + config.get(s));
        }

        for (Map.Entry<String, Bean> entry : config.entrySet()) {
            String beanName = entry.getKey();
            Bean bean = entry.getValue();
            // 如果容器中不存在 bean 对应 Object，则创建
            if (context.get(beanName) == null) {
                Object object = createBean(bean);
                // SINGLETON的话，由容器管理对象
                if (bean.getScope().equals(Bean.SINGLETON)) {
                    context.put(beanName, object);
                }
            }
        }
    }

    private Object createBean(Bean bean) {
        Object object = null;
        String className = bean.getClassName();
        List<Property> properties = bean.getProperties();
        try {
            Class<?> aClass = Class.forName(className);
            object = aClass.newInstance();

            for (Property property : properties) {
                String name = property.getName();
                String value = property.getValue();
                String ref = property.getRef();
                Method setMethod = BeanUtils.getSetMethod(object, name);
                Class<?> parameterType = setMethod.getParameterTypes()[0];

                if (value != null && ref == null) {

                    setMethod.invoke(object, parse(value, parameterType));

                } else if (value == null && ref != null) {
                    // 其他 Bean 的注入
                    Object refObject = context.get(ref);
                    if (refObject == null) {
                        // 容器中不存在要注入的 Bean
                        refObject = createBean(config.get(ref));
                        if (bean.getScope().equals(Bean.SINGLETON)) {
                            context.put(ref, refObject);
                        }
                    }
                    setMethod.invoke(object, refObject);
                }
            }

            //实现 Transactional 代理
            if (aClass.isAnnotationPresent(Service.class) || aClass.isAnnotationPresent(Transactional.class)) {
                TransactionProxy transactionProxy = new TransactionProxy();
                transactionProxy.setTarget(object);
                Object proxyObject = Proxy.newProxyInstance(object.getClass().getClassLoader(), 
                        object.getClass().getInterfaces(), transactionProxy);
                return proxyObject;
                
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("注意参数匹配");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }

    private Object parse(String value, Class parameterType) {
        Object object = value;

        // class 判断包装类型， TYPE 判断基本类型
        if (parameterType == Integer.class || parameterType == Integer.TYPE) {
            object = Integer.parseInt(value);
        } else if (parameterType == Double.class || parameterType == Double.TYPE) {
            object = Double.parseDouble(value);
        } else if (parameterType == Float.class || parameterType == Float.TYPE) {
            object = Float.parseFloat(value);
        } else if (parameterType == Long.class || parameterType == Long.TYPE) {
            object = Long.parseLong(value);
        } else if (parameterType == Byte.class || parameterType == Byte.TYPE) {
            object = Byte.parseByte(value);
        } else if (parameterType == Short.class || parameterType == Short.TYPE) {
            object = Short.parseShort(value);
        }

        return object;
    }

    @Override
    public Object getBean(String beanName) {
        Object object = context.get(beanName);
        if (object == null) {
            object = createBean(config.get(beanName));
        }
        return object;
    }
    
    public Map<String, Bean> getConfig() {
        return this.config;
    }

    public void registerBean(String beanName, Bean bean) {
        this.config.put(beanName, bean);
    }
}