package com.spring.mvc.config;

public class RequestParamConfig {
    private String name;
    private Class<?> type;
    private boolean required;
    private String defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "RequestParamConfig{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", required=" + required +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
