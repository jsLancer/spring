package com.spring.ioc.annotation;

import com.spring.ioc.config.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    String value() default "";
    String scope() default Bean.SINGLETON;

}