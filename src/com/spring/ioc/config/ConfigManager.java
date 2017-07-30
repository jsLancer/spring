package com.spring.ioc.config;

import com.spring.ioc.annotation.*;
import com.spring.mvc.annotation.Controller;
import com.spring.util.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigManager {

    private static Map<String, Bean> map = new HashMap<>();

    private static String basePackage = null;

    // 加载配置文件
    public static Map<String, Bean> getConfig(String path) {

        // 1. 创建解析器
        SAXReader reader = new SAXReader();

        // 2. 加载配置文件 -> document对象
        InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream(path);
        Document doc = null;
        try {
            doc = reader.read(inputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException("XML 配置错误");
        }

        // 3. 定义 xpath 表达式，取出 Bean 元素
        String xpath = "//bean";

        // 4.1 将 bean 元素封装到 Bean 对象
        List<Element> list = doc.selectNodes(xpath);
        if (list != null && !list.isEmpty()) {
            for (Element element : list) {
                String id = element.attributeValue("id");
                String className = element.attributeValue("class");
                String scope = element.attributeValue("scope");

                Bean bean = new Bean();
                bean.setId(id);
                bean.setClassName(className);
                if (scope != null) {
                    bean.setScope(scope);
                }

                // 4.2 获得 property 子元素
                List<Element> children = element.elements();

                List<Property> properties = new ArrayList<>();

                for (Element propEle : children) {
                    String propName = propEle.attributeValue("name");
                    String propValue = propEle.attributeValue("value");
                    String propRef = propEle.attributeValue("ref");

                    // 4.3 将 property 封装到 Property 对象
                    Property property = new Property();
                    property.setName(propName);
                    property.setValue(propValue);
                    property.setRef(propRef);

                    properties.add(property);
                }
                // 4.4 将 Property 对象封装到 Bean 对象
                bean.setProperties(properties);

                // 4.5 将 Bean 对象封装到 Map
                map.put(id, bean);
            }
        }
        

        // 注解优先于配置
        String scan = "//component-scan";
        List<Element> nodes = doc.selectNodes(scan);
        if (!nodes.isEmpty()) {
            basePackage = nodes.get(0).attributeValue("base-package");
            String basePackageFile = basePackage.replace(".", "/");
            
            componentScan(basePackageFile);
        }

        return map;
    }

    public static void componentScan(String baseFile) {
        String s = ConfigManager.class.getResource("/" + baseFile).getFile().toString();

        File packageFile = new File(s);

//        if (packageFile.isDirectory()) {
//            File[] files = packageFile.listFiles();
//
//            List<String> classNames = new ArrayList<>();
//
//            for (File file : files) {
//                String fileName = file.getAbsolutePath();
//
//                System.out.println(fileName);
//                String[] split = fileName.split("\\\\");
//                String className = basePackage + "." + split[split.length - 1].replaceAll(".class", "");
//
//                loadClass(className);
//            }
//        }
        findFile(packageFile);
    }
    
    /**
     * 递归的方式查找所有file
     * @param file
     */
    public static void findFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                findFile(f);
            }
        } else if (file.isFile()) {
            String fileName = file.getAbsolutePath();
            //将路径转换为包
            String temp = fileName.replaceAll("\\\\", ".");
            //获取正确包路径
            temp = temp.substring(temp.indexOf(basePackage));
            //得到全限定class名
            String className = temp.replaceAll(".class", "");

            
            loadClass(className);
        }
    }

    public static void loadClass(String className) {
        try {
            Class<?> aClass = Class.forName(className);
            if (aClass.isAnnotationPresent(Component.class)
                    || aClass.isAnnotationPresent(Controller.class)
                    || aClass.isAnnotationPresent(Service.class)
                    || aClass.isAnnotationPresent(Repository.class)) {
                String beanName = null;
                String scope = null;
                if (aClass.isAnnotationPresent(Component.class)) {                    
                    Component component = aClass.getAnnotation(Component.class);
                    if (!StringUtils.isEmpty(component.value())) {
                        beanName = component.value();
                    } else {
                        String simpleName = aClass.getSimpleName();
                        beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                    }

                    scope = component.scope();
                } else if (aClass.isAnnotationPresent(Controller.class)){
                    Controller controller = aClass.getAnnotation(Controller.class);
                    if (!StringUtils.isEmpty(controller.value())) {
                        beanName = controller.value();
                    } else {
                        String simpleName = aClass.getSimpleName();
                        beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                    }
                    scope = controller.scope();
                } else if (aClass.isAnnotationPresent(Service.class)) {
                    Service service = aClass.getAnnotation(Service.class);
                    if (!StringUtils.isEmpty(service.value())) {
                        beanName = service.value();
                    } else {
                        String simpleName = aClass.getSimpleName();
                        beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                    }
                    scope = service.scope();
                } else if (aClass.isAnnotationPresent(Repository.class)) {
                    Repository repository = aClass.getAnnotation(Repository.class);
                    if (!StringUtils.isEmpty(repository.value())) {
                        beanName = repository.value();
                    } else {
                        String simpleName = aClass.getSimpleName();
                        beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                    }
                    scope = repository.scope();
                }
                Bean bean = new Bean();
                bean.setId(beanName);
                bean.setClassName(className);
                bean.setScope(scope);

                List<Property> properties = new ArrayList<>();

                Field[] fields = aClass.getDeclaredFields();
                for (Field field : fields) {
                    
                    Class<?> type = field.getType();
                    String name = field.getName();

                    if (field.isAnnotationPresent(Autowired.class)) {
                        Autowired autowired = field.getAnnotation(Autowired.class);

                        String ref = null;
                        if (!StringUtils.isEmpty(autowired.value())) {
                            ref = autowired.value();
                        } else {
                            String simpleName = type.getSimpleName();
                            ref = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                        }
                        Property property = new Property();
                        property.setRef(ref);
                        property.setName(name);
                        properties.add(property);

                    } else if (field.isAnnotationPresent(Value.class)) {
                        Value value = field.getAnnotation(Value.class);
                        Property property = new Property();
                        property.setValue(value.value());
                        property.setName(name);
                        properties.add(property);
                    }
                }

                bean.setProperties(properties);


                map.put(beanName, bean);

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}