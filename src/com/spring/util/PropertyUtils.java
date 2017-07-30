package com.spring.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

    private static Properties properties = new Properties();

    static {
        try (InputStream inStream = PropertyUtils.class.getClassLoader().getResourceAsStream("db.properties");) {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return (String) properties.get(key);
    }

}
