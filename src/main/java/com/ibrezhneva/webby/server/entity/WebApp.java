package com.ibrezhneva.webby.server.entity;

import com.ibrezhneva.webby.server.entity.model.AppServletOutputStream;
import com.ibrezhneva.webby.server.entity.model.AppServletRequest;
import com.ibrezhneva.webby.server.entity.model.AppServletResponse;
import lombok.Data;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
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
        responseStatusLine.append(response.getStatus());
        responseStatusLine.append(" ");
        responseStatusLine.append(response.getHttpStatus().getStatusMessage());
        responseStatusLine.append("\r\n\r\n");
        ((AppServletOutputStream) response.getOutputStream()).setResponseStatusLine(responseStatusLine.toString());
    }
}
