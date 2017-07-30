package com.spring.mvc.model;

public interface Model {

    Model addAttribute(String key, Object value);

//    Model addAttribute(Object value);

//    Model addAllAttributes(Collection<?> attributeValues);

//    Model addAllAttributes(Map<String, ?> attributes);

//    Model mergeAttributes(Map<String, ?> attributes);

    boolean containsAttribute(String attributeName);


}