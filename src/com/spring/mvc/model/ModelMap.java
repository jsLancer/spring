package com.spring.mvc.model;

import java.util.HashMap;

public class ModelMap extends HashMap<String, Object> implements Model {

    @Override
    public Model addAttribute(String key, Object value) {
        this.put(key, value);
        return this;
    }

    @Override
    public boolean containsAttribute(String attributeName) {
        return this.containsKey(attributeName);
    }
   

}
