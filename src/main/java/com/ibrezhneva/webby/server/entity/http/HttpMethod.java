package com.ibrezhneva.webby.server.entity.http;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum HttpMethod {

    GET("GET"), POST("POST");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod getByName(String httpMethod) {
        return Arrays.stream(values())
                .filter(e -> e.name.equalsIgnoreCase(httpMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No method for name: " + httpMethod + " is found"));
    }
}