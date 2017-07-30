package com.spring;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

    public static final String CONNECTION = "CONNECTION";
    
    private static ThreadLocal<AppContext> appContextMap = new ThreadLocal<AppContext>();
    // private static Map<Thread, AppContext> appContextMap = new
    // HashMap<Thread, AppContext>();

    private Map<String, Object> objects = new HashMap<String, Object>();

    private AppContext() {

    }

    public Map<String, Object> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, Object> objects) {
        if (objects == null) {
            objects = new HashMap<String, Object>();
        }
        this.objects = objects;
    }

    public void setObject(String key, Object value) {
        this.objects.put(key, value);
    }

    public Object getObject(String key) {
        return this.objects.get(key);
    }
    
    public Object removeObject(String key) {
        return this.objects.remove(key);
    }

    public void clear() {
        this.objects.clear();
    }

    public static AppContext getContext() {
        AppContext appContext = appContextMap.get();
        if (appContext == null) {
            appContext = new AppContext();
            appContextMap.set(appContext);
        }
        return appContext;
    }
}
