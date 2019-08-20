package com.ibrezhneva.webby.server.entity.model;

import com.ibrezhneva.webby.server.entity.WebAppClassLoader;
import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import com.ibrezhneva.webby.server.entity.http.HttpHeaderName;
import com.ibrezhneva.webby.server.entity.http.HttpResponseConstants;
import lombok.Data;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import java.util.*;

@Data
public class WebApp {
    private String appFolder;
    private WebAppClassLoader classLoader;
    private Map<String, Class<?>> servletPathToClassMap;
    private Map<String, HttpServlet> servletPathToServletMap = new HashMap<>();

    public WebApp(String appFolder) {
        this.appFolder = appFolder;
    }

    public void process(AppServletRequest request, AppServletResponse response) {
        HttpServlet servlet;
        String requestURI = request.getRequestURI();
        Optional<HttpServlet> servletOptional = Optional.ofNullable(servletPathToServletMap.get(requestURI));
        try {
            if (!servletOptional.isPresent()) {
                Class<?> servletClass = servletPathToClassMap.get(request.getServletPath());
                servlet = (HttpServlet) servletClass.getDeclaredConstructor().newInstance();
                servletPathToServletMap.put(requestURI, servlet);
                servlet.init();
            } else {
                servlet = servletOptional.get();
            }
            servlet.service(request, response);
            setResponseStatusLine(response);
            setResponseHeaders(response);
            response.getWriter().flush();

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error during servlet instantiation", e);
        } catch (ServletException e) {
            throw new RuntimeException("Error during servlet init method", e);
        } catch (Exception e) {
            throw new RuntimeException("Error during HTTP request handling", e);
        }
    }

    public void destroy() {
        servletPathToServletMap.values().forEach(GenericServlet::destroy);
    }

    private void setResponseStatusLine(AppServletResponse response) {
        StringBuilder responseStatusLine = new StringBuilder();
        responseStatusLine.append(HttpResponseConstants.HTTP_VERSION).append(" ");
        responseStatusLine.append(response.getHttpStatus());
        responseStatusLine.append(HttpResponseConstants.CRLF);
        ((AppServletOutputStream) response.getOutputStream()).setResponseStatusLine(responseStatusLine.toString());
    }

    private void setResponseHeaders(AppServletResponse response) {
        StringBuilder headersBuiltString = new StringBuilder();
        List<HttpHeader> headers = response.getHeaders();
        headers.forEach(e -> headersBuiltString.append(e.toString()).append(HttpResponseConstants.CRLF));
        if (response.getCookies().size() > 0) {
            HttpHeader cookiesHeader = getCookiesHeader(response);
            headersBuiltString.append(cookiesHeader.toString()).append(HttpResponseConstants.CRLF);
        }
        headersBuiltString.append(HttpResponseConstants.CRLF);
        ((AppServletOutputStream) response.getOutputStream()).setHeaders(headersBuiltString.toString());
    }

    HttpHeader getCookiesHeader(AppServletResponse response) {
        List<Cookie> cookies = response.getCookies();
        StringJoiner joiner = new StringJoiner("; ");
        for (Cookie cookie : cookies) {
            joiner.add(cookieToString(cookie));
        }
        String headerName = HttpHeaderName.COOKIE.getName();
        String headerValue = joiner.toString();

        return new HttpHeader(headerName, headerValue);
    }

    private String cookieToString(Cookie cookie) {
        return cookie.getName() + "=" + cookie.getValue();
    }
}
