package com.spring.mvc.model;

import java.util.Map;

public class ModelAndView {
    private String view;
    private ModelMap model;

    public ModelAndView(String view) {
        this.view = view;
    }

    public ModelAndView(String view, Map<String, ?> model) {
        this.view = view;
        if (model != null) {
            getModel().putAll(model);
        }
    }

    public Map<String, Object> getModel() {
        if (this.model == null) {
            this.model = new ModelMap();
        }
        return this.model;
    }
    
    public ModelAndView addObject(String key, Object value) {
        getModel().put(key, value);
        return this;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
    
    public boolean isEmpty() {
        return this.getModel().isEmpty();
    }

    @Override
    public String toString() {
        return "ModelAndView [view=" + view + ", model=" + model + "]";
    }


}
