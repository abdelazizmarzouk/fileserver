package com.marshmelo.fileserver.models;

import java.util.Map;

public class HttpRequest {
    private String method;
    private String url;
    private Map<String, String> headers;
    private Map<String, String> params;

    public HttpRequest(String method, String url, Map<String, String> headers, Map<String, String> params) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }


    public Map<String, String> getParams() {
        return params;
    }


    public String getHeader(String key) {
        if (headers != null)
            return headers.get(key.toLowerCase());
        else return null;
    }

    public String getParam(String key) {
        return (String) params.get(key);
    }
}
