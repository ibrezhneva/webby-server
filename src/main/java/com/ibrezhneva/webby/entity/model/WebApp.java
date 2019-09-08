package com.ibrezhneva.webby.entity.model;

import com.ibrezhneva.webby.entity.http.HttpStatus;
import com.ibrezhneva.webby.exception.ServerException;
import lombok.Data;

import javax.servlet.Filter;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class WebApp {
    private String appFolder;
    private URLClassLoader classLoader;
    private Map<String, Class<? extends HttpServlet>> servletPathToClassMap;
    private List<FilterDefinitionDto> filterDefinitionDtos;

    private Map<String, HttpServlet> servletPathToServletMap = new ConcurrentHashMap<>();
    private Map<Class<? extends Filter>, Filter> clazzToFilterMap = new ConcurrentHashMap<>();

    public WebApp(String appFolder) {
        this.appFolder = appFolder;
    }

    public void process(AppServletRequest request, AppServletResponse response) {
        String servletPath = request.getServletPath();

        List<Filter> filters = getFiltersForServletPath(servletPath);
        AppFilterChain filterChain = new AppFilterChain(filters);
        filterChain.doFilter(request, response);
        if (response.getStatus() == HttpStatus.FOUND.getStatusCode()) {
            response.getWriter().flush();
            return;
        }

        String servletUrlPattern = getUrlPattern(servletPath);
        Class<? extends HttpServlet> servletClass = servletPathToClassMap.get(servletUrlPattern);
        try {
            HttpServlet servlet = servletPathToServletMap.computeIfAbsent(servletUrlPattern, e -> instantiateAndInit(servletClass));
            if (request.isRootRedirect()) {
                response.sendRedirect(request.getRequestURI() + request.getServletPath());
            } else {
                servlet.service(request, response);
            }
            response.getWriter().flush();
        } catch (ServletException e) {
            throw new RuntimeException("Error during servlet init method", e);
        } catch (Exception e) {
            throw new RuntimeException("Error during HTTP request handling", e);
        }
    }

    void destroy() {
        servletPathToServletMap.values().forEach(GenericServlet::destroy);
        clazzToFilterMap.values().forEach(Filter::destroy);
    }

    private String getUrlPattern(String uri) {
        for (String urlPattern : servletPathToClassMap.keySet()) {
            if (matchesUrlPattern(uri, urlPattern)) {
                return urlPattern;
            }
        }
        throw new ServerException(HttpStatus.NOT_FOUND, "There is no page for " + uri);
    }

    private List<Filter> getFiltersForServletPath(String servletPath) {
        List<Filter> filters = new ArrayList<>();
        for (FilterDefinitionDto filterDefinition : filterDefinitionDtos) {

            String filterUrl = filterDefinition.getUrlPattern();
            if (matchesUrlPattern(servletPath, filterUrl)) {
                Class<? extends Filter> clazz = filterDefinition.getClazz();
                Filter filter = clazzToFilterMap.computeIfAbsent(clazz, e -> instantiateAndInit(clazz));
                filters.add(filter);
            }
        }
        return filters;
    }

    private boolean matchesUrlPattern(String url, String pattern) {
        return url.equals(pattern) || url.matches(pattern.replace("*", ".*"));
    }

    private <T> T instantiateAndInit(Class<T> clazz) {
        try {
            T object = clazz.getDeclaredConstructor().newInstance();
            if (HttpServlet.class.isAssignableFrom(clazz)) {
                clazz.getMethod("init").invoke(object);
            }
            return object;
            // TODO: consider FilterConfig for init
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error during servlet instantiation", e);
        }
    }

}
