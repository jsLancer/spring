package com.spring.mvc.config;

import com.spring.mvc.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;


public class RequestConfig {

    private String beanName;
    private String className;
    private String method;
    private RequestMethod[] requestMethod;
    private String[] classMapping;
    private String[] methodMapping;
    private Class<?>[] paramTypes;
    private List<RequestParamConfig> paramList;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RequestMethod[] getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod[] requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String[] getClassMapping() {
        return classMapping;
    }

    public void setClassMapping(String[] classMapping) {
        this.classMapping = classMapping;
    }

    public String[] getMethodMapping() {
        return methodMapping;
    }

    public void setMethodMapping(String[] methodMapping) {
        this.methodMapping = methodMapping;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public List<RequestParamConfig> getParamList() {
        return paramList;
    }

    public void setParamList(List<RequestParamConfig> paramList) {
        this.paramList = paramList;
    }

    @Override
    public String toString() {
        return "RequestConfig{" +
                "beanName='" + beanName + '\'' +
                ", className='" + className + '\'' +
                ", method='" + method + '\'' +
                ", requestMethod=" + Arrays.toString(requestMethod) +
                ", classMapping=" + Arrays.toString(classMapping) +
                ", methodMapping=" + Arrays.toString(methodMapping) +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", paramList=" + paramList +
                '}';
    }
}
