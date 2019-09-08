package com.ibrezhneva.webby.entity.model;

import lombok.RequiredArgsConstructor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.List;

@RequiredArgsConstructor
public class AppFilterChain implements FilterChain {

    private final List<Filter> filters;
    private int index;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) {
        if (index < filters.size()) {
            try {
                filters.get(index++).doFilter(request, response, this);
            } catch (Exception e) {
                throw new RuntimeException("Error during filtering", e);
            }
        }
    }
}
