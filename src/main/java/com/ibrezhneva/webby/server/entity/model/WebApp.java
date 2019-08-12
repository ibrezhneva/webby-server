package com.ibrezhneva.webby.server.entity.model;

import com.ibrezhneva.webby.server.entity.WebAppClassLoader;
import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import com.ibrezhneva.webby.server.entity.model.AppServletOutputStream;
import com.ibrezhneva.webby.server.entity.model.AppServletRequest;
import com.ibrezhneva.webby.server.entity.model.AppServletResponse;
import lombok.Data;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class WebApp {
    private String appFolder;
    private WebAppClassLoader classLoader;
    private Map<String, Class<?>> servletPathToClassMap;
    private Map<String, HttpServlet> servletPathToServletMap = new HashMap<>();

    public void process(AppServletRequest request, AppServletResponse response) {
        HttpServlet servlet;
        String requestURI = request.getRequestURI();
        Optional<HttpServlet> servletOptional = Optional.ofNullable(servletPathToServletMap.get(requestURI));
        try {
            if (!servletOptional.isPresent()) {
                Class<?> servletClass = servletPathToClassMap.get(request.getServletPath());
                servlet = (HttpServlet) servletClass.getDeclaredConstructor().newInstance();
            } else {
                servlet = servletOptional.get();
                servletPathToServletMap.put(requestURI, servlet);
                servlet.init();
            }
            servlet.service(request, response);
            setResponseStatusLine(response);
            setResponseHeaders(response);
            response.getWriter().flush();

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error during servlet instantiation", e);
        } catch (ServletException e) {
            throw new RuntimeException("Error during servlet initialization", e);
        } catch (Exception e) {
            throw new RuntimeException("Error during HTTP request handling", e);
        }
    }

    public void destroy() {
        servletPathToServletMap.values().forEach(GenericServlet::destroy);
    }

    private void setResponseStatusLine(AppServletResponse response) {
        StringBuilder responseStatusLine = new StringBuilder();
        responseStatusLine.append("HTTP/1.1 ");
        responseStatusLine.append(response.getHttpStatus());
        responseStatusLine.append("\n");
        ((AppServletOutputStream) response.getOutputStream()).setResponseStatusLine(responseStatusLine.toString());
    }

    private void setResponseHeaders(AppServletResponse response) {
        Optional<List<HttpHeader>> optionalHeaders = Optional.ofNullable(response.getHeaders());
        StringBuilder headersBuiltString = new StringBuilder();
        if (optionalHeaders.isPresent()) {
            List<HttpHeader> headers = optionalHeaders.get();
            headers.forEach(e -> headersBuiltString.append(e.toString()).append("\n"));
        }
        headersBuiltString.append("\r\n");
        ((AppServletOutputStream) response.getOutputStream()).setHeaders(headersBuiltString.toString());
    }
}
