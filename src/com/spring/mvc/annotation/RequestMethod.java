package com.spring.mvc.annotation;

public enum RequestMethod {

    GET(200, "GET"), POST(200, "POST"), PUT(200, "PUT"), DELETE(200, "DELETE");

    private Integer status;
    private String method;

    private RequestMethod(Integer status, String method) {
        this.status = status;
        this.method = method;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}