package com.ibrezhneva.webby.server.util;

import com.ibrezhneva.webby.server.entity.model.AppServletRequest;
import lombok.Cleanup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    private static final String requestString = "GET /app/index.html HTTP/1.1\n" +
            "Host: localhost:3000\n" +
            "Connection: keep-alive\n" +
            "Cache-Control: max-age=0\n" +
            "Upgrade-Insecure-Requests: 1\n" +
            "Content-Type: text/html; charset=utf-8\n" +
            "Cookie: name=F\n" +
            "\n";

    @Test
    @DisplayName("Verify Uri, Method and Protocol")
    void testInjectUriAndMethodAndProtocol() {
        AppServletRequest request = new AppServletRequest();
        String requestLine = "GET /app/index.html HTTP/1.1";
        RequestParser.injectUriAndMethodAndProtocol(request, requestLine);
        assertEquals("GET", request.getMethod());
        assertEquals("/app/index.html", request.getRequestURI());
        assertEquals("HTTP/1.1", request.getProtocol());
    }

    @Test
    @DisplayName("Verify web application Name and servlet Path")
    void testInjectWebAppNameAndServletPath() {
        AppServletRequest request = new AppServletRequest();
        request.setUri("/app/products/all");
        RequestParser.injectWebAppNameAndServletPath(request);
        assertEquals("app", request.getWebAppName());
        assertEquals("products/all", request.getServletPath());
    }

    @Test
    @DisplayName("Verify Query string")
    void testInjectQueryString() {
        AppServletRequest request = new AppServletRequest();
        request.setUri("/app/users/default?fullname=Fadi%20Fakhouri#top");
        RequestParser.injectQueryString(request);
        assertEquals("fullname=Fadi%20Fakhouri", request.getQueryString());
    }

    @Test
    @DisplayName("Verify request parsing")
    void testParseRequest() throws IOException {
        byte[] requestStringBytes = requestString.getBytes();
        try (InputStream inputStream = new ByteArrayInputStream(requestStringBytes)) {
            AppServletRequest appServletRequest = RequestParser.parseRequest(inputStream);
            assertNotNull(appServletRequest);
        }
    }

}