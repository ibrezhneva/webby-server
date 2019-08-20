package com.ibrezhneva.webby.server.entity.model;


import com.ibrezhneva.webby.server.entity.adapter.HttpServletRequestAdapter;
import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import com.ibrezhneva.webby.server.entity.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import java.util.*;

@Setter
public class AppServletRequest extends HttpServletRequestAdapter {
    private ServletInputStream inputStream;
    private HttpMethod httpMethod;
    private List<HttpHeader> headers;
    private String uri;
    private String queryString;
    private Cookie[] cookies;
    private String servletPath;
    @Getter
    private String webAppName;
    private Map<String, String[]> parameters;
    private String protocol;

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public String getHeader(String name) {
        Optional<HttpHeader> headerOptional = headers.stream()
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst();
        return headerOptional.map(HttpHeader::getValue).orElse(null);
    }

    @Override
    public int getIntHeader(String name) {
        Optional<HttpHeader> headerOptional = headers.stream()
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst();
        return Integer.parseInt(headerOptional.map(HttpHeader::getValue).orElse("-1"));
    }

    @Override
    public String getMethod() {
        return httpMethod.getName();
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getRequestURI() {
        return uri;
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    @Override
    public ServletInputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name)[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

}
