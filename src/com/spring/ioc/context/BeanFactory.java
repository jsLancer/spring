package com.spring.ioc.context;

import com.spring.ioc.config.Bean;

import java.util.Map;


public interface BeanFactory {
    
    Object getBean(String beanName);
    
    Map<String, Bean> getConfig();
}
