package com.spring.mvc.handler;

import com.spring.util.StringUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HandlerMapping {
    public String handlerMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        
        String requestUri = uri.substring(request.getContextPath().length());
        if (StringUtils.isEmpty(requestUri)) {
            requestUri = "/login";
        }
        return requestUri + request.getMethod().toUpperCase();
    }
}
