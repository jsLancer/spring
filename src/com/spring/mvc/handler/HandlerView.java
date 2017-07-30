package com.spring.mvc.handler;

import com.spring.mvc.model.ModelAndView;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerView {

    private static final String VIEW_PREFIX = "/WEB-INF/";
    private static final String VIEW_SUFFIX = ".jsp";

    private String prefix = VIEW_PREFIX;
    private String suffix = VIEW_SUFFIX;


    public void handlerView(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView)
            throws Exception {
        String viewName = modelAndView.getView();
        if (viewName.startsWith("redirect:")) {
            String view = viewName.substring(9);
            response.sendRedirect(request.getContextPath() + view);
        } else {
            Map<String, Object> model = modelAndView.getModel();
            for (String key : model.keySet()) {
                request.setAttribute(key, model.get(key));
            }
            request.getRequestDispatcher(prefix + viewName + suffix).forward(request, response);
        }
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
