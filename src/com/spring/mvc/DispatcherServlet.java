package com.spring.mvc;

import com.spring.ioc.config.Bean;
import com.spring.ioc.context.ApplicationContext;
import com.spring.ioc.context.BeanFactory;
import com.spring.mvc.annotation.*;
import com.spring.mvc.config.RequestConfig;
import com.spring.mvc.config.RequestParamConfig;
import com.spring.mvc.handler.HandlerAdapter;
import com.spring.mvc.handler.HandlerMapping;
import com.spring.mvc.handler.HandlerView;
import com.spring.mvc.model.ModelAndView;
import com.spring.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DispatcherServlet extends HttpServlet {

    private Map<String, RequestConfig> requestConfigMap = new HashMap<>();

    private HandlerAdapter handlerAdapter = new HandlerAdapter();
    private HandlerMapping handlerMapping = new HandlerMapping();
    private HandlerView handlerView;

    private BeanFactory beanFactory;

    public DispatcherServlet() {
        super();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String requestKey = handlerMapping.handlerMapping(request, response);
        System.out.println("requestUrl:" + requestKey);

        RequestConfig config = requestConfigMap.get(requestKey);

        if (config != null) {
            try {
                Object controller = beanFactory.getBean(config.getBeanName());
                ModelAndView modelAndView = handlerAdapter.handlerAdapter(request, response, config, controller);
                System.out.println(modelAndView);
                handlerView.handlerView(request, response, modelAndView);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            response.sendError(404);
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String contextConfig = config.getInitParameter("contextConfigLocation");
        beanFactory = new ApplicationContext(contextConfig);

        Map<String, Bean> beanConfig = beanFactory.getConfig();

        handlerView = (HandlerView) beanFactory.getBean("handlerView");
        try {
            initController(beanConfig);
            for (String key : requestConfigMap.keySet()) {
                System.err.println(key + "---->" + requestConfigMap.get(key));    
            }
            
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    private void initController(Map<String, Bean> beanConfig) throws ClassNotFoundException {
        for (Bean bean : beanConfig.values()) {
            String className = bean.getClassName();
            String beanName;
            Class<?> c = Class.forName(className);
            if (c.isAnnotationPresent(Controller.class)) {
                Controller controller = c.getAnnotation(Controller.class);
                if (!StringUtils.isEmpty(controller.value())) {
                    beanName = controller.value();
                } else {
                    String simpleName = c.getSimpleName();
                    beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                }

                // 获取所有公有方法
                Method[] methods = c.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)
                            || method.isAnnotationPresent(GetMapping.class)
                            || method.isAnnotationPresent(PostMapping.class)
                            || method.isAnnotationPresent(DeleteMapping.class)) {

                        RequestConfig requestConfig = new RequestConfig();
                        requestConfig.setClassName(className);
                        requestConfig.setBeanName(beanName);
                        if (c.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping requestMapping = c.getAnnotation(RequestMapping.class);
                            String[] classMapping = requestMapping.value();
                            requestConfig.setClassMapping(classMapping);
                            RequestMethod[] requestMethod = requestMapping.method();
                            requestConfig.setRequestMethod(requestMethod);
                        }

                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                            String[] methodMapping = requestMapping.value();
                            requestConfig.setMethodMapping(methodMapping);
                            RequestMethod[] requestMethod = requestMapping.method();
                            requestConfig.setRequestMethod(requestMethod);
                        }
                        if (method.isAnnotationPresent(GetMapping.class)) {
                            GetMapping getMapping = method.getAnnotation(GetMapping.class);
                            String[] methodMapping = getMapping.value();
                            requestConfig.setMethodMapping(methodMapping);
                            RequestMethod[] requestMethod = getMapping.method();
                            requestConfig.setRequestMethod(requestMethod);
                        }
                        if (method.isAnnotationPresent(PostMapping.class)) {
                            PostMapping postMapping = method.getAnnotation(PostMapping.class);
                            String[] methodMapping = postMapping.value();
                            requestConfig.setMethodMapping(methodMapping);
                            RequestMethod[] requestMethod = postMapping.method();
                            requestConfig.setRequestMethod(requestMethod);
                        }
                        if (method.isAnnotationPresent(PutMapping.class)) {
                            PutMapping putMapping = method.getAnnotation(PutMapping.class);
                            String[] methodMapping = putMapping.value();
                            requestConfig.setMethodMapping(methodMapping);
                            RequestMethod[] requestMethod = putMapping.method();
                            requestConfig.setRequestMethod(requestMethod);
                        }
                        if (method.isAnnotationPresent(DeleteMapping.class)) {
                            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                            String[] methodMapping = deleteMapping.value();
                            requestConfig.setMethodMapping(methodMapping);
                            RequestMethod[] requestMethod = deleteMapping.method();
                            requestConfig.setRequestMethod(requestMethod);
                        }

                        // 获取所有参数类型
                        Class<?>[] paramTypes = method.getParameterTypes();
                        requestConfig.setParamTypes(paramTypes);
                        requestConfig.setMethod(method.getName());

                        // 获取所有参数名
                        Parameter[] parameters = method.getParameters();

                        List<RequestParamConfig> paramList = new ArrayList<RequestParamConfig>();

                        for (Parameter parameter : parameters) {
                            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                            RequestParamConfig paramConfig = new RequestParamConfig();
                            if (requestParam != null) {
                                System.out.println(requestParam.value());
                                paramConfig.setName(requestParam.value());
                                paramConfig.setRequired(requestParam.required());
                                paramConfig.setDefaultValue(requestParam.defaultValue());
                            } else {
                                paramConfig.setName(parameter.getName());
                                paramConfig.setRequired(false);
                                paramConfig.setDefaultValue(null);
                            }
                            paramConfig.setType(parameter.getType());
                            paramList.add(paramConfig);
                        }

                        requestConfig.setParamList(paramList);

                        if (requestConfig.getClassMapping().length == 0) {
                            for (String methodMapping : requestConfig.getMethodMapping()) {
                                for (RequestMethod requestMethod : requestConfig.getRequestMethod()) {
                                    String urlMapping = methodMapping + ".action" + requestMethod.getMethod();
                                    requestConfigMap.put(urlMapping, requestConfig);
                                }
                            }
                        } else {
                            for (String classMapping : requestConfig.getClassMapping()) {
                                for (String methodMapping : requestConfig.getMethodMapping()) {
                                    for (RequestMethod requestMethod : requestConfig.getRequestMethod()) {
                                        String urlMapping = classMapping + methodMapping + ".action" + requestMethod.getMethod();
                                        requestConfigMap.put(urlMapping, requestConfig);
                                    }
                                }
                            }
                        }
                        
                    }

                }

            }
        }
    }
}
