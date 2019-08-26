package com.ibrezhneva.webby.server.entity.model;

import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import com.ibrezhneva.webby.server.exception.ServerException;
import lombok.Data;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class WebApp {
    private String appFolder;
    private URLClassLoader classLoader;
    private Map<String, Class<?>> servletPathToClassMap;
    private Map<String, HttpServlet> servletPathToServletMap = new HashMap<>();

    public WebApp(String appFolder) {
        this.appFolder = appFolder;
    }

    public void process(AppServletRequest request, AppServletResponse response) {
        HttpServlet servlet;
        String servletPath = request.getServletPath();
        String urlPattern = getUrlPattern(servletPath);
        Optional<HttpServlet> servletOptional = Optional.ofNullable(servletPathToServletMap.get(urlPattern));
        try {
            if (!servletOptional.isPresent()) {
                Class<?> servletClass = servletPathToClassMap.get(urlPattern);
                servlet = (HttpServlet) servletClass.getDeclaredConstructor().newInstance();
                servletPathToServletMap.put(urlPattern, servlet);
                servlet.init();
            } else {
                servlet = servletOptional.get();
            }
            servlet.service(request, response);
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

    private String getUrlPattern(String uri) {
        for (String urlPattern : servletPathToClassMap.keySet()) {
            if(urlPattern.contains("*")) {
                if(uri.matches(urlPattern.replace("*", ".*"))) {
                    return urlPattern;
                }
            } else {
                if(uri.equals(urlPattern)) {
                    return uri;
                }
            }
        }
        throw new ServerException(HttpStatus.NOT_FOUND, "There is no page for " + uri);
    }

}
